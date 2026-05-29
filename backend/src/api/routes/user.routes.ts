import { Hono } from "@hono/hono";
import UserController from "../controller/user-controller.ts";
import ProgressController from "../controller/progress-controller.ts";

export const USER_ROUTER = new Hono();

// We inject dependencies here inside a builder or we can wire it in main.ts.
// Returning a function to inject controller makes it cleaner.
export function setupUserRoutes(
    userController: UserController,
    progressController: ProgressController,
): Hono {
    const router = new Hono();
    
    router.post("/register", (c) => userController.register(c));
    router.post("/login", (c) => userController.login(c));
    router.get("/:user_id/learned/algorithms", (c) => progressController.getLearnedAlgorithms(c));
    router.get("/:user_id/learned/lessons", (c) => progressController.getLearnedLessons(c));
    router.get("/:user_id/learned/courses", (c) => progressController.getCourseProgress(c));
    router.get("/profile/:username", (c) => userController.getProfile(c));
    router.post("/logout", (c) => userController.logout(c));
    router.put("/:username/username", (c) => userController.updateUsername(c));
    router.put("/:username/email", (c) => userController.updateEmail(c));
    router.put("/:username/password", (c) => userController.updatePassword(c));
    router.delete("/:username", (c) => userController.deleteUser(c));

    return router;
}

export default USER_ROUTER;
