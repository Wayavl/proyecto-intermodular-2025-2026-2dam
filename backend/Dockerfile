# Build stage
FROM denoland/deno:latest AS builder
WORKDIR /app
COPY . .

# Run installation pointing explicitly to your backend configuration
RUN deno install --config ./backend/deno.json --entrypoint ./backend/src/main.ts

# Production stage
FROM denoland/deno:latest
WORKDIR /app
COPY --from=builder /app .

EXPOSE 80

# Keep Deno aware of your import maps at runtime using --config
CMD ["deno", "serve", "--config", "./backend/deno.json", "--allow-net", "--allow-read", "--allow-env", "./backend/src/main.ts"]