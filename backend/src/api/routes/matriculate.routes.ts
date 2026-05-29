import { Hono } from "@hono/hono";
import MatriculateController from "../controller/matriculate-controller.ts";

export function setupMatriculateRoutes(matriculateController: MatriculateController): Hono {
    const router = new Hono();
    
    router.post("/enroll", (c) => matriculateController.enroll(c));
    router.get("/:user_id", (c) => matriculateController.getEnrollments(c));
    router.delete("/:user_id/:course_id", (c) => matriculateController.unenroll(c));
    router.post("/:user_id/:course_id/finish", (c) => matriculateController.markAsFinished(c));

    return router;
}
