import CoursesRepository from "../../domain/repository/courses-repository.ts";
import { LessonRepository } from "../../domain/repository/lesson-repository.ts";
import { GetCoursesRequest, GetCourseLessonsRequest, GetCourseRequest, GetLessonRequest, CreateCourseRequest, CreateCourseTranslationRequest, UpdateCoursePremiumRequest, UpdateCourseTranslationRequest, DeleteCourseRequest, CreateLessonRequest, CreateLessonTranslationRequest, UpdateLessonOrderRequest, UpdateLessonAlgorithmRequest, UpdateLessonTranslationRequest, DeleteLessonRequest } from "../data/in/course.in.ts";
import { CourseResponse, LessonResponse } from "../data/out/course.out.ts";

export default class CourseService {
    private courseRepository: CoursesRepository;
    private lessonRepository: LessonRepository;

    constructor(courseRepository: CoursesRepository, lessonRepository: LessonRepository) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
    }

    async getAllCourses(request: GetCoursesRequest): Promise<CourseResponse[]> {
        return await this.courseRepository.readAllCourses(request.languageId);
    }

    async getCourse(request: GetCourseRequest): Promise<CourseResponse | null> {
        return await this.courseRepository.readCourse(request.courseId, request.languageId);
    }

    async getCourseLessons(request: GetCourseLessonsRequest): Promise<LessonResponse[]> {
        return await this.lessonRepository.readLessonsByCourse(request.courseId, request.languageId);
    }

    async getLesson(request: GetLessonRequest): Promise<LessonResponse | null> {
        return await this.lessonRepository.readLesson(request.lessonId, request.languageId);
    }

    async createCourse(request: CreateCourseRequest): Promise<{ course_id: string }> {
        return await this.courseRepository.createCourse(request.is_premium);
    }

    async createCourseTranslation(request: CreateCourseTranslationRequest): Promise<void> {
        await this.courseRepository.createCourseTranslation(request.courseId, request.languageName, request.name, request.description);
    }

    async updateCoursePremium(request: UpdateCoursePremiumRequest): Promise<void> {
        await this.courseRepository.updateCoursePremium(request.courseId, request.is_premium);
    }

    async updateCourseTranslation(request: UpdateCourseTranslationRequest): Promise<void> {
        await this.courseRepository.updateCourseTranslation(request.courseId, request.languageName, request.newName, request.description);
    }

    async deleteCourse(request: DeleteCourseRequest): Promise<void> {
        await this.courseRepository.deleteCourse(request.courseId);
    }

    async createLesson(request: CreateLessonRequest): Promise<{ lesson_id: string }> {
        return await this.lessonRepository.createLesson(request.courseId, request.algorithmId || null, request.lessonOrder);
    }

    async createLessonTranslation(request: CreateLessonTranslationRequest): Promise<void> {
        await this.lessonRepository.createLessonTranslation(request.lessonId, request.languageName, request.subject, request.name, request.contentMd || "");
    }

    async updateLessonOrder(request: UpdateLessonOrderRequest): Promise<void> {
        await this.lessonRepository.updateLessonOrder(request.lessonId, request.newOrder);
    }

    async updateLessonAlgorithm(request: UpdateLessonAlgorithmRequest): Promise<void> {
        await this.lessonRepository.updateLessonAlgorithm(request.lessonId, request.algorithmId || null);
    }

    async updateLessonTranslation(request: UpdateLessonTranslationRequest): Promise<void> {
        await this.lessonRepository.updateLessonTranslation(request.lessonId, request.languageName, request.subject, request.name, request.contentMd || "");
    }

    async deleteLesson(request: DeleteLessonRequest): Promise<void> {
        await this.lessonRepository.deleteLesson(request.lessonId);
    }
}
