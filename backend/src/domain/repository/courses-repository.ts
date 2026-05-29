import Course from "../model/course.ts";

export default interface CourseRepository {
  createCourse(
    is_premium: boolean,
  ): Promise<{ course_id: string }>;

  createCourseTranslation(
    course_id: string,
    language_id: string,
    name: string,
    description: string,
  ): Promise<void>;

  readCourse(
    course_id: string,
    language_id: string,
  ): Promise<Course | null>;

  readAllCourses(language_id: string): Promise<Course[]>;

  updateCoursePremium(
    course_id: string,
    is_premium: boolean,
  ): Promise<void>;

  updateCourseTranslation(
    course_id: string,
    language_id: string,
    new_name: string,
    description: string,
  ): Promise<void>;

  deleteCourse(course_id: string): Promise<void>;
}
