import { Context } from "@hono/hono";
import CourseService from "../../application/services/course-service.ts";
import { 
  GetCoursesRequest, 
  GetCourseLessonsRequest, 
  GetCourseRequest, 
  GetLessonRequest, 
  CreateCourseRequest, 
  CreateCourseTranslationRequest, 
  UpdateCoursePremiumRequest, 
  UpdateCourseTranslationRequest, 
  DeleteCourseRequest, 
  CreateLessonRequest, 
  CreateLessonTranslationRequest, 
  UpdateLessonOrderRequest, 
  UpdateLessonAlgorithmRequest, 
  UpdateLessonTranslationRequest, 
  DeleteLessonRequest 
} from "../../application/data/in/course.in.ts";

export default class CourseController {
    private courseService: CourseService;

    constructor(courseService: CourseService) {
        this.courseService = courseService;
    }

    async getCourses(c: Context) {
        try {
            const languageId = c.req.query("lang") || "default_lang_uuid";
            const request: GetCoursesRequest = { languageId };
            const courses = await this.courseService.getAllCourses(request);
            return c.json({ courses }, 200);
        } catch (error: unknown) {
            if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async getCourse(c: Context) {
        try {
            const courseId = c.req.param("course_id");
            const languageId = c.req.query("lang") || "default_lang_uuid";
            if (!courseId) return c.json({ error: "Missing course_id" }, 400);

            const request: GetCourseRequest = { courseId, languageId };
            const course = await this.courseService.getCourse(request);
            if (!course) return c.json({ error: "Course not found" }, 404);
            return c.json({ course }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async getCourseLessons(c: Context) {
        try {
            const courseId = c.req.param("course_id");
            const languageId = c.req.query("lang") || "default_lang_uuid";

            if (!courseId) {
                return c.json({ error: "Missing course_id" }, 400);
            }

            const request: GetCourseLessonsRequest = { courseId, languageId };
            const lessons = await this.courseService.getCourseLessons(request);
            return c.json({ lessons }, 200);
        } catch (error: unknown) {
             if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async getLesson(c: Context) {
        try {
            const lessonId = c.req.param("lesson_id");
            const languageId = c.req.query("lang") || "default_lang_uuid";
            if (!lessonId) return c.json({ error: "Missing lesson_id" }, 400);

            const request: GetLessonRequest = { lessonId, languageId };
            const lesson = await this.courseService.getLesson(request);
            if (!lesson) return c.json({ error: "Lesson not found" }, 404);
            return c.json({ lesson }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async createCourse(c: Context) {
        try {
            const body = await c.req.json();
            const request: CreateCourseRequest = { is_premium: body.is_premium || false };
            const response = await this.courseService.createCourse(request);
            return c.json({ message: "Course created", course_id: response.course_id }, 201);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async createCourseTranslation(c: Context) {
        try {
            const courseId = c.req.param("course_id");
            const body = await c.req.json();
            
            // Validación estricta de description añadida aquí
            if (!courseId || !body.language_name || !body.name || !body.description) {
                return c.json({ error: "Missing parameters (course_id, language_name, name, and description are required)" }, 400);
            }

            const request: CreateCourseTranslationRequest = { 
                courseId, 
                languageName: body.language_name, 
                name: body.name,
                description: body.description 
            };
            await this.courseService.createCourseTranslation(request);
            return c.json({ message: "Translation created" }, 201);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async updateCoursePremium(c: Context) {
        try {
            const courseId = c.req.param("course_id");
            const body = await c.req.json();
            if (!courseId || body.is_premium === undefined) return c.json({ error: "Missing parameters" }, 400);

            const request: UpdateCoursePremiumRequest = { courseId, is_premium: body.is_premium };
            await this.courseService.updateCoursePremium(request);
            return c.json({ message: "Course premium status updated" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async updateCourseTranslation(c: Context) {
        try {
            const courseId = c.req.param("course_id");
            const languageName = c.req.param("language_name");
            const body = await c.req.json();
            
            // Validación estricta de description añadida también aquí
            if (!courseId || !languageName || !body.name || !body.description) {
                return c.json({ error: "Missing parameters (name and description are required)" }, 400);
            }

            const request: UpdateCourseTranslationRequest = { 
                courseId, 
                languageName, 
                newName: body.name,
                newDescription: body.description
            };
            await this.courseService.updateCourseTranslation(request);
            return c.json({ message: "Translation updated" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async deleteCourse(c: Context) {
        try {
            const courseId = c.req.param("course_id");
            if (!courseId) return c.json({ error: "Missing course_id" }, 400);

            const request: DeleteCourseRequest = { courseId };
            await this.courseService.deleteCourse(request);
            return c.json({ message: "Course deleted" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async createLesson(c: Context) {
        try {
            const courseId = c.req.param("course_id");
            const body = await c.req.json();
            if (!courseId || body.lesson_order === undefined) return c.json({ error: "Missing parameters" }, 400);

            const request: CreateLessonRequest = { courseId, lessonOrder: body.lesson_order, algorithmId: body.algorithm_id };
            const response = await this.courseService.createLesson(request);
            return c.json({ message: "Lesson created", lesson_id: response.lesson_id }, 201);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async createLessonTranslation(c: Context) {
        try {
            const lessonId = c.req.param("lesson_id");
            const body = await c.req.json();
            
            // Revertido a su estado original (sin requerir description para las lecciones)
            if (!lessonId || !body.language_name || !body.subject || !body.name) {
                return c.json({ error: "Missing parameters" }, 400);
            }

            const request: CreateLessonTranslationRequest = { 
                lessonId, 
                languageName: body.language_name, 
                subject: body.subject, 
                name: body.name, 
                contentMd: body.content_md 
            };
            await this.courseService.createLessonTranslation(request);
            return c.json({ message: "Lesson translation created" }, 201);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async updateLessonOrder(c: Context) {
        try {
            const lessonId = c.req.param("lesson_id");
            const body = await c.req.json();
            if (!lessonId || body.new_order === undefined) return c.json({ error: "Missing parameters" }, 400);

            const request: UpdateLessonOrderRequest = { lessonId, newOrder: body.new_order };
            await this.courseService.updateLessonOrder(request);
            return c.json({ message: "Lesson order updated" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async updateLessonAlgorithm(c: Context) {
        try {
            const lessonId = c.req.param("lesson_id");
            const body = await c.req.json();
            if (!lessonId) return c.json({ error: "Missing parameters" }, 400);

            const request: UpdateLessonAlgorithmRequest = { lessonId, algorithmId: body.algorithm_id };
            await this.courseService.updateLessonAlgorithm(request);
            return c.json({ message: "Lesson algorithm updated" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async updateLessonTranslation(c: Context) {
        try {
            const lessonId = c.req.param("lesson_id");
            const languageName = c.req.param("language_name");
            const body = await c.req.json();
            
            // Revertido a su estado original (sin requerir description para las lecciones)
            if (!lessonId || !languageName || !body.subject || !body.name) {
                return c.json({ error: "Missing parameters" }, 400);
            }

            const request: UpdateLessonTranslationRequest = { 
                lessonId, 
                languageName, 
                subject: body.subject, 
                name: body.name, 
                contentMd: body.content_md 
            };
            await this.courseService.updateLessonTranslation(request);
            return c.json({ message: "Lesson translation updated" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async deleteLesson(c: Context) {
        try {
            const lessonId = c.req.param("lesson_id");
            if (!lessonId) return c.json({ error: "Missing lesson_id" }, 400);

            const request: DeleteLessonRequest = { lessonId };
            await this.courseService.deleteLesson(request);
            return c.json({ message: "Lesson deleted" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }
}