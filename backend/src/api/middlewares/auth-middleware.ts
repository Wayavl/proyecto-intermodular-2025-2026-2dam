import { Context, Next } from "@hono/hono";
import { getCookie } from "@hono/hono/cookie";
import { verify } from "@hono/hono/jwt";

function extractBearerToken(c: Context): string | undefined {
    const authHeader = c.req.header("Authorization");
    if (authHeader?.startsWith("Bearer ")) {
        return authHeader.slice("Bearer ".length).trim();
    }
    return undefined;
}

function extractCookieToken(c: Context): string | undefined {
    const fromHelper = getCookie(c, "authorization");
    if (fromHelper) return fromHelper;

    const cookieHeader = c.req.header("Cookie");
    if (!cookieHeader) return undefined;

    for (const part of cookieHeader.split(";")) {
        const trimmed = part.trim();
        const separator = trimmed.indexOf("=");
        if (separator <= 0) continue;
        const name = trimmed.slice(0, separator).trim();
        if (name.toLowerCase() === "authorization") {
            return trimmed.slice(separator + 1).trim();
        }
    }
    return undefined;
}

export async function authMiddleware(c: Context, next: Next) {
    const token = extractCookieToken(c) ?? extractBearerToken(c);
    if (!token) {
        return c.json({ error: "Unauthorized access: Missing authorization token" }, 401);
    }
    
    try {
        const secret = Deno.env.get("JWT_SECRET") || "secret";
        const payload = await verify(token, secret, "HS256");
        c.set("jwtPayload", payload);
        await next();
    } catch {
        return c.json({ error: "Unauthorized access: Invalid or expired validation token" }, 401);
    }
}
