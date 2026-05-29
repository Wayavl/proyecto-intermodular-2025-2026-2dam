export interface CourseResponse {
    course_id: string;
    is_premium: boolean;
}

export interface LessonResponse {
    lesson_id: string;
    course_id: string;
    algorithm_id: string | null;
    lesson_order: number;
}
