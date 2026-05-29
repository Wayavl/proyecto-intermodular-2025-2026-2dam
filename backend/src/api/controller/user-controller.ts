import { Context } from "@hono/hono";
import { setCookie } from "@hono/hono/cookie";
import { sign } from "@hono/hono/jwt";
import UserService from "../../application/services/user-service.ts";
import UserAlreadyExists from "../../domain/exceptions/user/user-already-exists.ts";
import UserDoesNotExists from "../../domain/exceptions/user/user-does-not-exists.ts";
import UserPasswordIsIncorrect from "../../domain/exceptions/user/user-password-is-incorrect.ts";
import { RegisterUserRequest, LoginUserRequest, GetUserRequest, UpdateUserRequest } from "../../application/data/in/user.in.ts";

export default class UserController {
    private userService: UserService;

    constructor(userService: UserService) {
        this.userService = userService;
    }

    async register(c: Context) {
        try {
            const body = await c.req.json();
            const { username, email, password } = body;
            if (!username || !email || !password) {
                return c.json({ error: "Missing fields" }, 400);
            }
            const request: RegisterUserRequest = { username, email, password_plain: password };
            const user = await this.userService.register(request);

            const payload = {
                id: user.user_id,
                user_id: user.user_id,
                username: user.username,
                exp: Math.floor(Date.now() / 1000) + 60 * 60 * 24 * 30, // 1 month
            };
            const secret = Deno.env.get("JWT_SECRET") || "secret";
            const token = await sign(payload, secret, "HS256");
            const cookieSecure = new URL(c.req.url).protocol === "https:";

            setCookie(c, "authorization", token, {
                httpOnly: true,
                maxAge: 60 * 60 * 24 * 30,
                path: "/",
                secure: cookieSecure,
                sameSite: "Strict"
            });

            return c.json({ message: "User registered", user, access_token: token }, 201);
        } catch (error: unknown) {
            if (error instanceof UserAlreadyExists) {
                return c.json({ error: error.message }, 409);
            }
            if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async login(c: Context) {
        try {
            const body = await c.req.json();
            const { password, username, email } = body;
            if (!password || (!username && !email)) {
                return c.json({ error: "Missing fields" }, 400);
            }
            const request: LoginUserRequest = { password_plain: password, username, email };
            const user = await this.userService.login(request);

            const payload = {
                id: user.user_id,
                user_id: user.user_id,
                username: user.username,
                exp: Math.floor(Date.now() / 1000) + 60 * 60 * 24 * 30, // 1 month
            };
            const secret = Deno.env.get("JWT_SECRET") || "secret";
            const token = await sign(payload, secret, "HS256");
            const cookieSecure = new URL(c.req.url).protocol === "https:";

            setCookie(c, "authorization", token, {
                httpOnly: true,
                maxAge: 60 * 60 * 24 * 30,
                path: "/",
                secure: cookieSecure,
                sameSite: "Strict"
            });

            return c.json({ message: "Login successful", user, access_token: token }, 200);
        } catch (error: unknown) {
            if (error instanceof UserDoesNotExists || error instanceof UserPasswordIsIncorrect) {
                return c.json({ error: error.message }, 401);
            }
            if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async getProfile(c: Context) {
        try {
            const username = c.req.param("username");
            if (!username) {
                return c.json({ error: "Missing parameter :username" }, 400);
            }
            const request: GetUserRequest = { username };
            const profile = await this.userService.getUser(request);
            return c.json({ profile }, 200);
        } catch (error: unknown) {
            if (error instanceof UserDoesNotExists) {
                return c.json({ error: error.message }, 404);
            }
            if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    logout(c: Context) {
        setCookie(c, "authorization", "", { maxAge: 0, path: "/" });
        return c.json({ message: "Logged out" }, 200);
    }

    async updateUsername(c: Context) {
        try {
            const username = c.req.param("username");
            const payload = c.get("jwtPayload");
            if (payload?.username !== "admin" && payload?.username !== username) {
                return c.json({ error: "Forbidden: Account horizontal access denied" }, 403);
            }

            const { new_username } = await c.req.json();
            if (!username || !new_username) return c.json({ error: "Missing fields" }, 400);
            
            const request: UpdateUserRequest = { username, new_username };
            await this.userService.updateUsername(request);
            return c.json({ message: "Username updated" }, 200);
        } catch (e: unknown) {
            return c.json({ error: (e as Error).message }, 500);
        }
    }

    async updateEmail(c: Context) {
        try {
            const username = c.req.param("username");
            const payload = c.get("jwtPayload");
            if (payload?.username !== "admin" && payload?.username !== username) {
                return c.json({ error: "Forbidden: Account horizontal access denied" }, 403);
            }

            const { new_email } = await c.req.json();
            if (!username || !new_email) return c.json({ error: "Missing fields" }, 400);
            
            const request: UpdateUserRequest = { username, new_email };
            await this.userService.updateEmail(request);
            return c.json({ message: "Email updated" }, 200);
        } catch (e: unknown) {
            return c.json({ error: (e as Error).message }, 500);
        }
    }

    async updatePassword(c: Context) {
        try {
            const username = c.req.param("username");
            const payload = c.get("jwtPayload");
            if (payload?.username !== "admin" && payload?.username !== username) {
                return c.json({ error: "Forbidden: Account horizontal access denied" }, 403);
            }

            const { new_password } = await c.req.json();
            if (!username || !new_password) return c.json({ error: "Missing fields" }, 400);
            
            const request: UpdateUserRequest = { username, new_password_plain: new_password };
            await this.userService.updatePassword(request);
            return c.json({ message: "Password updated" }, 200);
        } catch (e: unknown) {
            return c.json({ error: (e as Error).message }, 500);
        }
    }

    async deleteUser(c: Context) {
        try {
            const username = c.req.param("username");
            const payload = c.get("jwtPayload");
            if (payload?.username !== "admin" && payload?.username !== username) {
                return c.json({ error: "Forbidden: Account horizontal access denied" }, 403);
            }

            if (!username) return c.json({ error: "Missing fields" }, 400);
            
            const request: GetUserRequest = { username };
            await this.userService.deleteUser(request);
            return c.json({ message: "Account deleted" }, 200);
        } catch (e: unknown) {
            return c.json({ error: (e as Error).message }, 500);
        }
    }
}