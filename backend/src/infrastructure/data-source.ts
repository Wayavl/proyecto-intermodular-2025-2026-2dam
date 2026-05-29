import { Client } from "@db/postgres"

export function getPostgresDataSource(): Client {
    return new Client({
        user: Deno.env.get("DATABASE_USER") || "root",
        password: Deno.env.get("DATABASE_PASSWORD"),
        database: Deno.env.get("DATABASE_DATABASE") || "db",
        hostname: Deno.env.get("DATABASE_HOSTNAME") || "localhost",
        port: Deno.env.get("DATABASE_PORT") || "5432"
    });
}