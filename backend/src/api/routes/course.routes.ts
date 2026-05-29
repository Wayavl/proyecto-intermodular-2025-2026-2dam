import { Hono } from "@hono/hono";
import CourseController from "../controller/course-controller.ts";
import { adminMiddleware } from "../middlewares/admin-middleware.ts";

export function setupCourseRoutes(courseController: CourseController): Hono {
    const router = new Hono();
    
    // Rutas para los cursos
    router.get("/", (c) => courseController.getCourses(c));
    router.get("/:course_id", (c) => courseController.getCourse(c));
    router.get("/:course_id/lessons", (c) => courseController.getCourseLessons(c));
    router.get("/lessons/:lesson_id", (c) => courseController.getLesson(c));

    // Rutas protegidas de administrador
    router.post("/", adminMiddleware, (c) => courseController.createCourse(c));
    router.post("/:course_id/translations", adminMiddleware, (c) => courseController.createCourseTranslation(c));
    router.put("/:course_id/premium", adminMiddleware, (c) => courseController.updateCoursePremium(c));
    router.put("/:course_id/translations/:language_name", adminMiddleware, (c) => courseController.updateCourseTranslation(c));
    router.delete("/:course_id", adminMiddleware, (c) => courseController.deleteCourse(c));

    router.post("/:course_id/lessons", adminMiddleware, (c) => courseController.createLesson(c));
    router.post("/lessons/:lesson_id/translations", adminMiddleware, (c) => courseController.createLessonTranslation(c));
    router.put("/lessons/:lesson_id/order", adminMiddleware, (c) => courseController.updateLessonOrder(c));
    router.put("/lessons/:lesson_id/algorithm", adminMiddleware, (c) => courseController.updateLessonAlgorithm(c));
    router.put("/lessons/:lesson_id/translations/:language_name", adminMiddleware, (c) => courseController.updateLessonTranslation(c));
    router.delete("/lessons/:lesson_id", adminMiddleware, (c) => courseController.deleteLesson(c));

    return router;
}
