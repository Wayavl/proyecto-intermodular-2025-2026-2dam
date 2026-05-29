export interface EnrollmentResponse {
    user_id: string;
    course_id: string;
    begin_date: Date;
    finish_date: Date | null;
}
