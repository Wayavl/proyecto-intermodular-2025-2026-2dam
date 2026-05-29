import { Context } from "@hono/hono";
import MatriculateService from "../../application/services/matriculate-service.ts";
import { EnrollUserRequest, GetUserEnrollmentsRequest } from "../../application/data/in/matriculate.in.ts";
import UserRepository from "../../domain/repository/user-repository.ts";
import { denyUnlessUserResourceAccess } from "../middlewares/access-control.ts";

export default class MatriculateController {
    private matriculateService: MatriculateService;
    private userRepository: UserRepository;

    constructor(matriculateService: MatriculateService, userRepository: UserRepository) {
        this.matriculateService = matriculateService;
        this.userRepository = userRepository;
    }

    async enroll(c: Context) {
        try {
            const body = await c.req.json();
            const { user_id, course_id } = body;
            const denied = await denyUnlessUserResourceAccess(
                c,
                user_id,
                this.userRepository,
                "Forbidden: Cannot enroll other users",
            );
            if (denied) return denied;
            if (!user_id || !course_id) {
                return c.json({ error: "Missing user_id or course_id" }, 400);
            }
            const request: EnrollUserRequest = { user_id, course_id };
            await this.matriculateService.enroll(request);
            return c.json({ message: "Enrolled successfully" }, 200);
        } catch (error: unknown) {
             if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async getEnrollments(c: Context) {
        try {
            const userId = c.req.param("user_id");
            const denied = await denyUnlessUserResourceAccess(
                c,
                userId,
                this.userRepository,
                "Forbidden: Cannot view other users enrollments",
            );
            if (denied) return denied;
            if (!userId) {
                return c.json({ error: "Missing user_id" }, 400);
            }
            const request: GetUserEnrollmentsRequest = { user_id: userId };
            const enrollments = await this.matriculateService.getUserEnrollments(request);
            return c.json({ enrollments }, 200);
        } catch (error: unknown) {
            if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async unenroll(c: Context) {
        try {
            const userId = c.req.param("user_id");
            const courseId = c.req.param("course_id");
            const denied = await denyUnlessUserResourceAccess(
                c,
                userId,
                this.userRepository,
                "Forbidden: Cannot modify other users enrollments",
            );
            if (denied) return denied;
            if (!userId || !courseId) return c.json({ error: "Missing user_id or course_id" }, 400);
            
            const request: EnrollUserRequest = { user_id: userId, course_id: courseId };
            await this.matriculateService.unenroll(request);
            return c.json({ message: "Unenrolled successfully" }, 200);
        } catch (error: unknown) {
             if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async markAsFinished(c: Context) {
        try {
            const userId = c.req.param("user_id");
            const courseId = c.req.param("course_id");
            const denied = await denyUnlessUserResourceAccess(
                c,
                userId,
                this.userRepository,
                "Forbidden: Cannot modify other users enrollments",
            );
            if (denied) return denied;
            if (!userId || !courseId) return c.json({ error: "Missing user_id or course_id" }, 400);
            
            const request: EnrollUserRequest = { user_id: userId, course_id: courseId };
            await this.matriculateService.markCourseAsFinished(request);
            return c.json({ message: "Course marked as completed" }, 200);
        } catch (error: unknown) {
             if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }
}
