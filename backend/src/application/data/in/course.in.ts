export interface GetCoursesRequest {
    languageId: string;
}

export interface GetCourseRequest {
    courseId: string;
    languageId: string;
}

export interface GetCourseLessonsRequest {
    courseId: string;
    languageId: string;
}

export interface GetLessonRequest {
    lessonId: string;
    languageId: string;
}

export interface CreateCourseRequest {
    is_premium: boolean;
}

export interface CreateCourseTranslationRequest {
    courseId: string;
    languageName: string;
    name: string;
    description: string,
}

export interface UpdateCoursePremiumRequest {
    courseId: string;
    is_premium: boolean;
}

export interface UpdateCourseTranslationRequest {
    courseId: string;
    languageName: string;
    newName: string;
    description: string,
}

export interface DeleteCourseRequest {
    courseId: string;
}

export interface CreateLessonRequest {
    courseId: string;
    algorithmId?: string;
    lessonOrder: number;
}

export interface CreateLessonTranslationRequest {
    lessonId: string;
    languageName: string;
    subject: string;
    name: string;
    contentMd?: string;
}

export interface UpdateLessonOrderRequest {
    lessonId: string;
    newOrder: number;
}

export interface UpdateLessonAlgorithmRequest {
    lessonId: string;
    algorithmId?: string;
}

export interface UpdateLessonTranslationRequest {
    lessonId: string;
    languageName: string;
    subject: string;
    name: string;
    contentMd?: string;
}

export interface DeleteLessonRequest {
    lessonId: string;
}
