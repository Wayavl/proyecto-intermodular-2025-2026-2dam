import { Context } from "@hono/hono";
import ProgressService from "../../application/services/progress-service.ts";
import UserRepository from "../../domain/repository/user-repository.ts";

type JwtPayload = {
    username?: string;
};

export default class ProgressController {
    private progressService: ProgressService;
    private userRepository: UserRepository;

    constructor(progressService: ProgressService, userRepository: UserRepository) {
        this.progressService = progressService;
        this.userRepository = userRepository;
    }

    private async resolveActingUserId(c: Context, paramUserId: string): Promise<string | Response> {
        const payload = c.get("jwtPayload") as JwtPayload | undefined;
        if (payload?.username === "admin") {
            if (!paramUserId) {
                return c.json({ error: "Missing user_id parameter" }, 400);
            }
            return paramUserId;
        }
        if (!payload?.username) {
            return c.json({ error: "Unauthorized access" }, 401);
        }
        const user = await this.userRepository.readUser(payload.username);
        if (!user) {
            return c.json({ error: "User not found" }, 404);
        }
        return String(user.user_id);
    }

    async getLearnedAlgorithms(c: Context) {
        try {
            const paramUserId = c.req.param("user_id");
            const userId = await this.resolveActingUserId(c, paramUserId);
            if (userId instanceof Response) return userId;

            const languageId = c.req.query("lang") || "es";
            const algorithms = await this.progressService.getLearnedAlgorithms(
                userId,
                languageId,
            );
            return c.json({ algorithms }, 200);
        } catch (error: unknown) {
            if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async getLearnedLessons(c: Context) {
        try {
            const paramUserId = c.req.param("user_id");
            const userId = await this.resolveActingUserId(c, paramUserId);
            if (userId instanceof Response) return userId;

            const languageId = c.req.query("lang") || "es";
            const lessons = await this.progressService.getLearnedLessons(userId, languageId);
            return c.json({ lessons }, 200);
        } catch (error: unknown) {
            if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async getCourseProgress(c: Context) {
        try {
            const paramUserId = c.req.param("user_id");
            const userId = await this.resolveActingUserId(c, paramUserId);
            if (userId instanceof Response) return userId;

            const languageId = c.req.query("lang") || "es";
            const courses = await this.progressService.getCourseProgressFromLearnedLessons(
                userId,
                languageId,
            );
            return c.json({ courses }, 200);
        } catch (error: unknown) {
            if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }
}
