import { LearnedLesson } from "../model/learned-lesson.ts";
import { LearnedLessonDetail } from "../model/learned-lesson-detail.ts";
import { Lesson } from "../model/lesson.ts";

export interface LessonRepository {
  // ================= CATÁLOGO BASE =================
  createLesson(
    course_id: string,
    lesson_order: number,
  ): Promise<{ lesson_id: string }>;

  createLessonTranslation(
    lesson_id: string,
    language_id: string,
    subject: string,
    name: string,
    content_md?: string,
  ): Promise<void>;

  // READ
  readLesson(lesson_id: string, language_id: string): Promise<Lesson | null>;

  // ¡Muy importante para Android! Devuelve todo el curso ordenado
  readLessonsByCourse(
    course_id: string,
    language_id: string,
  ): Promise<Lesson[]>;

  // UPDATE
  updateLessonOrder(lesson_id: string, new_order: number): Promise<void>;

  updateLessonTranslation(
    lesson_id: string,
    language_id: string,
    subject: string,
    name: string,
    content_md?: string,
  ): Promise<void>;

  // DELETE
  deleteLesson(lesson_id: string): Promise<void>;

  // ================= PROGRESO DEL USUARIO ('lessons_learned') =================
  markLessonAsLearned(user_id: string, lesson_id: string): Promise<void>;
  getLearnedLessonsByUser(user_id: string): Promise<LearnedLesson[]>;
  getLearnedLessonsDetailsByUser(
    user_id: string,
    language_id: string,
  ): Promise<LearnedLessonDetail[]>;
  countLessonsByCourse(course_id: string): Promise<number>;
}
