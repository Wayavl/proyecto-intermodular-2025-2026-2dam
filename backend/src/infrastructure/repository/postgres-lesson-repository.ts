import { Client } from "@db/postgres";
import { LearnedLesson } from "../../domain/model/learned-lesson.ts";
import { LearnedLessonDetail } from "../../domain/model/learned-lesson-detail.ts";
import { Lesson } from "../../domain/model/lesson.ts";
import { LessonRepository } from "../../domain/repository/lesson-repository.ts";

export default class PostgresLessonRepository implements LessonRepository {
  client: Client;

  constructor(client: Client) {
    this.client = client;
  }

  // ==========================================
  // CREATE
  // ==========================================

  async createLesson(
    course_id: string,
    algorithm_id: string | null,
    lesson_order: number,
  ): Promise<{ lesson_id: string }> {
    const query = `
      INSERT INTO lessons (course_id, algorithm_id, lesson_order) 
      VALUES ($1, $2, $3) 
      RETURNING lesson_id;
    `;
    const result = await this.client.queryObject<{ lesson_id: string }>(query, [
      course_id,
      algorithm_id, // Postgres driver maneja automáticamente el null
      lesson_order,
    ]);
    return result.rows[0];
  }

  async createLessonTranslation(
    lesson_id: string,
    language_id: string,
    subject: string,
    name: string,
    content_md: string = "",
  ): Promise<void> {
    const query = `
      INSERT INTO lessons_translations (lesson_id, language, subject, name, content_md) 
      VALUES ($1, (SELECT language_id FROM language WHERE language_name = $2), $3, $4, $5);
    `;
    await this.client.queryObject(query, [
      lesson_id,
      language_id,
      subject,
      name,
      content_md,
    ]);
  }

  // ==========================================
  // READ
  // ==========================================

  async readLesson(
    lesson_id: string,
    language_id: string,
  ): Promise<Lesson | null> {
    const query = `
      SELECT 
        l.lesson_id, l.course_id, l.algorithm_id, l.lesson_order,
        lt.subject, lt.name, lt.content_md
      FROM lessons l
      LEFT JOIN language lang ON lang.language_name = $2
      LEFT JOIN lessons_translations lt 
        ON l.lesson_id = lt.lesson_id AND lt.language = lang.language_id
      WHERE l.lesson_id = $1;
    `;
    const result = await this.client.queryObject<Lesson>(query, [
      lesson_id,
      language_id,
    ]);
    return result.rows[0] || null;
  }

  // Esta query es la que usarás el 90% del tiempo cuando entres a un curso en Android
  async readLessonsByCourse(
    course_id: string,
    language_id: string,
  ): Promise<Lesson[]> {
    const query = `
      SELECT 
        l.lesson_id, l.course_id, l.algorithm_id, l.lesson_order,
        lt.subject, lt.name, lt.content_md
      FROM lessons l
      LEFT JOIN language lang ON lang.language_name = $2
      LEFT JOIN lessons_translations lt 
        ON l.lesson_id = lt.lesson_id AND lt.language = lang.language_id
      WHERE l.course_id = $1
      ORDER BY l.lesson_order ASC;
    `;
    // Fíjate en el ORDER BY l.lesson_order ASC. Gracias a tu índice SQL, esto vuela.
    const result = await this.client.queryObject<Lesson>(query, [
      course_id,
      language_id,
    ]);
    return result.rows;
  }

  // ==========================================
  // UPDATE
  // ==========================================

  async updateLessonOrder(lesson_id: string, new_order: number): Promise<void> {
    const query = `UPDATE lessons SET lesson_order = $1 WHERE lesson_id = $2;`;
    await this.client.queryObject(query, [new_order, lesson_id]);
  }

  async updateLessonAlgorithm(
    lesson_id: string,
    algorithm_id: string | null,
  ): Promise<void> {
    const query = `UPDATE lessons SET algorithm_id = $1 WHERE lesson_id = $2;`;
    await this.client.queryObject(query, [algorithm_id, lesson_id]);
  }

  async updateLessonTranslation(
    lesson_id: string,
    language_id: string,
    subject: string,
    name: string,
    content_md: string = "",
  ): Promise<void> {
    const query = `
      UPDATE lessons_translations 
      SET subject = $1, name = $2, content_md = $3
      WHERE lesson_id = $4 AND language = (SELECT language_id FROM language WHERE language_name = $5);
    `;
    await this.client.queryObject(query, [
      subject,
      name,
      content_md,
      lesson_id,
      language_id,
    ]);
  }

  // ==========================================
  // DELETE
  // ==========================================

  async deleteLesson(lesson_id: string): Promise<void> {
    // Al borrar la lección, el ON DELETE CASCADE se lleva la traducción y
    // borra los registros de los alumnos en 'lessons_learned'.
    const query = `DELETE FROM lessons WHERE lesson_id = $1;`;
    await this.client.queryObject(query, [lesson_id]);
  }

  // ==========================================
  // PROGRESO DEL USUARIO (Entidad débil: lessons_learned)
  // ==========================================

  async markLessonAsLearned(user_id: string, lesson_id: string): Promise<void> {
    // Equivalente a UPSERT. Si repasa la lección, actualizamos la fecha.
    const query = `
      INSERT INTO lessons_learned (user_id, lesson_id, finish_date)
      VALUES ($1, $2, CURRENT_TIMESTAMP)
      ON CONFLICT (user_id, lesson_id) 
      DO UPDATE SET finish_date = CURRENT_TIMESTAMP;
    `;
    await this.client.queryObject(query, [user_id, lesson_id]);
  }

  async getLearnedLessonsByUser(user_id: string): Promise<LearnedLesson[]> {
    const query = `
      SELECT lesson_id, finish_date 
      FROM lessons_learned 
      WHERE user_id = $1;
    `;
    const result = await this.client.queryObject<LearnedLesson>(query, [
      user_id,
    ]);
    return result.rows;
  }

  async getLearnedLessonsDetailsByUser(
    user_id: string,
    language_id: string,
  ): Promise<LearnedLessonDetail[]> {
    const query = `
      SELECT
        ll.lesson_id,
        ll.finish_date,
        l.course_id,
        lt.name AS lesson_name,
        ct.name AS course_name
      FROM lessons_learned ll
      JOIN lessons l ON l.lesson_id = ll.lesson_id
      LEFT JOIN language lang ON lang.language_name = $2
      LEFT JOIN lessons_translations lt
        ON l.lesson_id = lt.lesson_id AND lt.language = lang.language_id
      LEFT JOIN courses_translations ct
        ON l.course_id = ct.course_id AND ct.language = lang.language_id
      WHERE ll.user_id = $1
      ORDER BY ll.finish_date DESC;
    `;
    const result = await this.client.queryObject<LearnedLessonDetail>(query, [
      user_id,
      language_id,
    ]);
    return result.rows;
  }

  async countLessonsByCourse(course_id: string): Promise<number> {
    const query = `
      SELECT COUNT(*)::int AS total
      FROM lessons
      WHERE course_id = $1;
    `;
    const result = await this.client.queryObject<{ total: number }>(query, [
      course_id,
    ]);
    return result.rows[0]?.total ?? 0;
  }
}
