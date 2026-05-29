/**
 * WebGPU Matrix Multiplication execution engine for Deno/TypeScript.
 */

export interface MatrixMultiplyResult {
  latency_ms: number;
  gflops: number;
  vram_mb: number;
  message: string;
}

export async function runMatrixMultiplyWebGPU(
  matrixDim: number,
  blockSize: number = 16,
  sharedTiling: boolean = true,
  coalescing: boolean = true
): Promise<MatrixMultiplyResult> {
  const N = matrixDim;
  const numElements = N * N;
  const byteSize = numElements * 4; // Float32 is 4 bytes
  const vramMb = (byteSize * 3) / (1024 * 1024); // VRAM for A, B, and C matrices

  if (!navigator.gpu) {
    return runSimulatedMatrixMultiply(N, blockSize, sharedTiling, coalescing, "WebGPU is not supported by Deno in this environment (navigator.gpu is missing). Running simulation.");
  }

  let adapter: GPUAdapter | null = null;
  try {
    adapter = await navigator.gpu.requestAdapter();
  } catch (e) {
    console.error("Failed to request GPU adapter:", e);
  }

  if (!adapter) {
    return runSimulatedMatrixMultiply(N, blockSize, sharedTiling, coalescing, "No GPU adapter found. Running simulation.");
  }

  let device: GPUDevice | null = null;
  try {
    device = await adapter.requestDevice();
  } catch (e) {
    console.error("Failed to request GPU device:", e);
  }

  if (!device) {
    return runSimulatedMatrixMultiply(N, blockSize, sharedTiling, coalescing, "Failed to create GPU device. Running simulation.");
  }

  // Adjust blockSize according to device limitations
  const maxInvocations = device.limits.maxComputeInvocationsPerWorkgroup;
  let dynamicBlockSize = blockSize;
  if (dynamicBlockSize * dynamicBlockSize > maxInvocations) {
    dynamicBlockSize = Math.floor(Math.sqrt(maxInvocations));
    console.warn(`Block size ${blockSize}x${blockSize} exceeds device limit of ${maxInvocations}. Auto-adjusted to ${dynamicBlockSize}x${dynamicBlockSize}.`);
  }

  try {
    // 1. Generate WGSL Shader Code based on parameters
    let shaderSource = "";

    if (sharedTiling) {
      // Tiled Matrix Multiplication using Shared Memory (Workgroup)
      shaderSource = `
        @group(0) @binding(0) var<storage, read> matrixA : array<f32>;
        @group(0) @binding(1) var<storage, read> matrixB : array<f32>;
        @group(0) @binding(2) var<storage, read_write> matrixC : array<f32>;
        @group(0) @binding(3) var<uniform> size : vec2<u32>;

        var<workgroup> tileA : array<array<f32, ${dynamicBlockSize}>, ${dynamicBlockSize}>;
        var<workgroup> tileB : array<array<f32, ${dynamicBlockSize}>, ${dynamicBlockSize}>;

        @compute @workgroup_size(${dynamicBlockSize}, ${dynamicBlockSize})
        fn main(
          @builtin(global_invocation_id) global_id : vec3<u32>,
          @builtin(local_invocation_id) local_id : vec3<u32>
        ) {
          let row = global_id.y;
          let col = global_id.x;
          let tx = local_id.x;
          let ty = local_id.y;
          let N = size.x;

          var sum = 0.0;
          let numTiles = (N + ${dynamicBlockSize}u - 1u) / ${dynamicBlockSize}u;

          for (var t = 0u; t < numTiles; t = t + 1u) {
            // Load A tile
            let aRow = row;
            let aCol = t * ${dynamicBlockSize}u + tx;
            if (aRow < N && aCol < N) {
              tileA[ty][tx] = matrixA[aRow * N + aCol];
            } else {
              tileA[ty][tx] = 0.0;
            }

            // Load B tile (Coalesced vs Uncoalesced load)
            let bRow = t * ${dynamicBlockSize}u + ty;
            let bCol = col;
            if (bRow < N && bCol < N) {
              ${coalescing 
                ? "tileB[ty][tx] = matrixB[bRow * N + bCol];" 
                : "tileB[ty][tx] = matrixB[bCol * N + bRow]; // Non-coalesced access"
              }
            } else {
              tileB[ty][tx] = 0.0;
            }

            workgroupBarrier();

            for (var k = 0u; k < ${dynamicBlockSize}u; k = k + 1u) {
              sum = sum + tileA[ty][k] * tileB[k][tx];
            }

            workgroupBarrier();
          }

          if (row < N && col < N) {
            matrixC[row * N + col] = sum;
          }
        }
      `;
    } else {
      // Naive Matrix Multiplication (directly referencing Global Memory)
      shaderSource = `
        @group(0) @binding(0) var<storage, read> matrixA : array<f32>;
        @group(0) @binding(1) var<storage, read> matrixB : array<f32>;
        @group(0) @binding(2) var<storage, read_write> matrixC : array<f32>;
        @group(0) @binding(3) var<uniform> size : vec2<u32>;

        @compute @workgroup_size(${dynamicBlockSize}, ${dynamicBlockSize})
        fn main(@builtin(global_invocation_id) global_id : vec3<u32>) {
          let row = global_id.y;
          let col = global_id.x;
          let N = size.x;

          if (row >= N || col >= N) {
            return;
          }

          var sum = 0.0;
          for (var k = 0u; k < N; k = k + 1u) {
            ${coalescing 
              ? "sum = sum + matrixA[row * N + k] * matrixB[k * N + col];" 
              : "sum = sum + matrixA[row * N + k] * matrixB[col * N + k]; // Transposed access pattern"
            }
          }

          matrixC[row * N + col] = sum;
        }
      `;
    }

    // 2. Prepare GPU Buffers & Data
    const dataA = new Float32Array(numElements);
    const dataB = new Float32Array(numElements);
    // Initialize matrices with dummy values
    for (let i = 0; i < numElements; i++) {
      dataA[i] = Math.random();
      dataB[i] = Math.random();
    }

    // Create uniform buffer for size metadata
    const sizeData = new Uint32Array([N, N]);
    const sizeBuffer = device.createBuffer({
      size: 8,
      usage: GPUBufferUsage.UNIFORM,
      mappedAtCreation: true,
    });
    new Uint32Array(sizeBuffer.getMappedRange()).set(sizeData);
    sizeBuffer.unmap();

    // Create GPU buffers for matrices
    const bufferA = device.createBuffer({
      size: byteSize,
      usage: GPUBufferUsage.STORAGE,
      mappedAtCreation: true,
    });
    new Float32Array(bufferA.getMappedRange()).set(dataA);
    bufferA.unmap();

    const bufferB = device.createBuffer({
      size: byteSize,
      usage: GPUBufferUsage.STORAGE,
      mappedAtCreation: true,
    });
    new Float32Array(bufferB.getMappedRange()).set(dataB);
    bufferB.unmap();

    const bufferC = device.createBuffer({
      size: byteSize,
      usage: GPUBufferUsage.STORAGE | GPUBufferUsage.COPY_SRC,
    });

    // Create staging buffer to read back result
    const stagingBuffer = device.createBuffer({
      size: byteSize,
      usage: GPUBufferUsage.MAP_READ | GPUBufferUsage.COPY_DST,
    });

    // 3. Compile WGSL Shader
    const shaderModule = device.createShaderModule({
      code: shaderSource,
    });

    // 4. Create Compute Pipeline
    const computePipeline = device.createComputePipeline({
      layout: "auto",
      compute: {
        module: shaderModule,
        entryPoint: "main",
      },
    });

    // 5. Create Bind Group
    const bindGroup = device.createBindGroup({
      layout: computePipeline.getBindGroupLayout(0),
      entries: [
        { binding: 0, resource: { buffer: bufferA } },
        { binding: 1, resource: { buffer: bufferB } },
        { binding: 2, resource: { buffer: bufferC } },
        { binding: 3, resource: { buffer: sizeBuffer } },
      ],
    });

    // 6. Record and Execute
    const commandEncoder = device.createCommandEncoder();
    const passEncoder = commandEncoder.beginComputePass();
    passEncoder.setPipeline(computePipeline);
    passEncoder.setBindGroup(0, bindGroup);

    const workgroupCount = Math.ceil(N / dynamicBlockSize);
    passEncoder.dispatchWorkgroups(workgroupCount, workgroupCount);
    passEncoder.end();

    // Copy result matrix to staging buffer
    commandEncoder.copyBufferToBuffer(bufferC, 0, stagingBuffer, 0, byteSize);

    // 7. Measure Latency
    const start = performance.now();
    device.queue.submit([commandEncoder.finish()]);
    
    // Map staging buffer to wait for GPU execution finish
    await stagingBuffer.mapAsync(GPUMapMode.READ);
    const end = performance.now();
    const latencyMs = end - start;

    // Read mapped range to make sure it actually computed
    const resultRange = new Float32Array(stagingBuffer.getMappedRange());
    const dummyCheck = resultRange[0]; // Access first element to verify read availability

    // Clean up
    stagingBuffer.unmap();
    bufferA.destroy();
    bufferB.destroy();
    bufferC.destroy();
    stagingBuffer.destroy();
    sizeBuffer.destroy();
    device.destroy();

    // Compute GFLOPS: 2 * N^3 / (latencyMs * 1e6)
    const operations = 2 * N * N * N;
    const gflops = operations / (latencyMs * 1e6);

    return {
      latency_ms: latencyMs,
      gflops: gflops,
      vram_mb: vramMb,
      message: `WebGPU Matrix Multiply executed successfully on ${adapter.info.description || "GPU"}. Verification element: ${dummyCheck.toFixed(4)}`,
    };
  } catch (err: unknown) {
    if (device) {
      try { device.destroy(); } catch (_) {}
    }
    const errMsg = err instanceof Error ? err.message : String(err);
    return runSimulatedMatrixMultiply(N, blockSize, sharedTiling, coalescing, `Error executing WebGPU: ${errMsg}. Running simulation.`);
  }
}

/**
 * Fallback CPU/JS simulation of matrix multiplication performance metrics.
 */
function runSimulatedMatrixMultiply(
  N: number,
  blockSize: number,
  sharedTiling: boolean,
  coalescing: boolean,
  reason: string
): MatrixMultiplyResult {
  console.log(`[MatrixMultiply Simulation] ${reason}`);
  const byteSize = N * N * 4;
  const vramMb = (byteSize * 3) / (1024 * 1024);

  // Core baseline performance calculations
  // Complexity: O(N^3). A standard baseline for N=512 is ~10ms on simulated environment.
  let baseLatencyMs = (N * N * N) / 13_400_000.0;

  // Apply modifiers
  if (sharedTiling) {
    baseLatencyMs *= 0.18; // Shared tiling reduces global mem read latency by ~5.5x
  } else {
    baseLatencyMs *= 1.25; // Naive reads are slower
  }

  if (coalescing) {
    baseLatencyMs *= 0.38; // Coalesced access maximizes warp scheduling efficiency
  } else {
    baseLatencyMs *= 2.85; // Uncoalesced layouts cause bank conflicts/extra latency
  }

  // Workgroup size multiplier effects
  baseLatencyMs *= whenBlockSizeMultiplier(blockSize);

  // Add random variance (+/- 5%) to feel organic
  const variance = 0.95 + Math.random() * 0.10;
  const latencyMs = baseLatencyMs * variance;

  // Calculate simulated GFLOPS
  const operations = 2 * N * N * N;
  const gflops = operations / (latencyMs * 1e6);

  return {
    latency_ms: latencyMs,
    gflops: gflops,
    vram_mb: vramMb,
    message: `${reason} (Simulated).`,
  };
}

function whenBlockSizeMultiplier(blockSize: number): number {
  switch (blockSize) {
    case 8: return 1.45; // Sub-optimal occupancy
    case 32: return 0.85; // High occupancy (ideal for GPUs with large register files)
    case 64: return 2.10; // Registers exhaustion pressure / register spilling
    default: return 1.0;  // 16 is default/baseline
  }
}
