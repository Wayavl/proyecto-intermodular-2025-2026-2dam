export interface Lesson {
  lesson_id: string;
  course_id: string;
  lesson_order: number;
  subject?: string;
  name?: string;
  content_md?: string;
}
