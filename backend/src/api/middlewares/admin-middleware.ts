import { Context, Next } from "@hono/hono";

export const adminMiddleware = async (c: Context, next: Next) => {
    const payload = c.get("jwtPayload");
    
    // In this app, we hardcode the admin username as 'admin'. 
    // Any other user will be rejected.
    if (!payload || payload.username !== "admin") {
        return c.json({ error: "Forbidden: Administrator access required" }, 403);
    }
    
    await next();
};
