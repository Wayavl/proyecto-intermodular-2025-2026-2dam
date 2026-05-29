import { ExecuteAlgorithmRequest } from "../data/in/algorithm.in.ts";
import { runMatrixMultiplyWebGPU } from "../../infrastructure/webgpu/matrix-multiply.ts";

export default class AlgorithmExecutionService {
    // Map function mapping algorithm_id to an asynchronous execution function returning metrics
    private executionEngine = new Map<string, (params: Record<string, unknown>) => Promise<Record<string, unknown>>>();

    constructor() {
        // Register demo-algorithm
        this.executionEngine.set("demo-algorithm", (params: Record<string, unknown>) => {
            console.log("--> EXECUTING demo-algorithm WITH PARAMS:", params);
            return Promise.resolve({
                success: true,
                message: "Demo execution successful",
                latency_ms: 12.5,
                gflops: 120.0,
                vram_mb: 2.5
            });
        });

        // Register mock-algo-matrix (GPU Matrix Multiplication WebGPU runner)
        this.executionEngine.set("mock-algo-matrix", async (params: Record<string, unknown>) => {
            console.log("--> EXECUTING mock-algo-matrix WITH PARAMS:", params);
            
            // Extract parameters sent by client
            const matrixDim = Number(params.matrix_dim ?? 1024);
            const blockSize = Number(params.block_size ?? 16);
            
            // Parse boolean parameters (might come as boolean or string representation)
            const sharedTiling = params.shared_tiling === true || String(params.shared_tiling) === "true";
            const coalescing = params.coalescing === true || String(params.coalescing) === "true";

            const result = await runMatrixMultiplyWebGPU(
                matrixDim,
                blockSize,
                sharedTiling,
                coalescing
            );

            return {
                success: true,
                message: result.message,
                latency_ms: result.latency_ms,
                gflops: result.gflops,
                vram_mb: result.vram_mb
            };
        });
    }

    async execute(request: ExecuteAlgorithmRequest): Promise<Record<string, unknown>> {
        const executeLogic = this.executionEngine.get(request.algorithmId);
        if (!executeLogic) {
            throw new Error(`Execution for algorithm ${request.algorithmId} does not exist or is not registered.`);
        }
        return await executeLogic(request.params);
    }
}
