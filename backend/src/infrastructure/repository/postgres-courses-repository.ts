import { Client } from "@db/postgres";
import Course from "../../domain/model/course.ts";
import CourseRepository from "../../domain/repository/courses-repository.ts";

export default class PostgresCourseRepository implements CourseRepository {
  client: Client;

  constructor(client: Client) {
    this.client = client;
  }

  async createCourse(
    is_premium: boolean = false,
  ): Promise<{ course_id: string }> {
    const query = `
      INSERT INTO courses (is_premium) 
      VALUES ($1) 
      RETURNING course_id;
    `;
    const result = await this.client.queryObject<{ course_id: string }>(query, [
      is_premium,
    ]);
    return result.rows[0];
  }

  async createCourseTranslation(
    course_id: string,
    language_id: string,
    name: string,
    description: string,
  ): Promise<void> {
    const query = `
      INSERT INTO courses_translations (course_id, language, name, description) 
      VALUES ($1, (SELECT language_id FROM language WHERE language_name = $2), $3, $4);
    `;
    await this.client.queryObject(query, [course_id, language_id, name, description]);
  }

  async readCourse(
    course_id: string,
    language_id: string,
  ): Promise<Course | null> {
    const query = `
      SELECT c.course_id, c.is_premium, ct.name, ct.description
      FROM courses c
      LEFT JOIN language l ON l.language_name = $2
      LEFT JOIN courses_translations ct ON c.course_id = ct.course_id AND ct.language = l.language_id
      WHERE c.course_id = $1;
    `;
    const result = await this.client.queryObject<Course>(query, [
      course_id,
      language_id,
    ]);
    return result.rows[0] || null;
  }

  async readAllCourses(language_id: string): Promise<Course[]> {
    const query = `
      SELECT c.course_id, c.is_premium, ct.name, ct.description
      FROM courses c
      JOIN language l ON l.language_name = $1
      JOIN courses_translations ct ON c.course_id = ct.course_id AND ct.language = l.language_id;
    `;
    const result = await this.client.queryObject<Course>(query, [language_id]);
    return result.rows;
  }

  async updateCoursePremium(
    course_id: string,
    is_premium: boolean,
  ): Promise<void> {
    const query = `
      UPDATE courses 
      SET is_premium = $1 
      WHERE course_id = $2;
    `;
    await this.client.queryObject(query, [is_premium, course_id]);
  }

  async updateCourseTranslation(
    course_id: string,
    language_id: string,
    new_name: string,
    description: string,
  ): Promise<void> {
    const query = `
      UPDATE courses_translations 
      SET name = $1, description = $2
      WHERE course_id = $3 AND language = (SELECT language_id FROM language WHERE language_name = $4);
    `;
    await this.client.queryObject(query, [new_name, description, course_id, language_id]);
  }

  // Borrar el curso.
  // OJO: Como en tu base de datos pusiste ON DELETE CASCADE en courses_translations,
  // esto borra el curso y todas sus traducciones automáticamente. Eficiencia pura.
  async deleteCourse(course_id: string): Promise<void> {
    const query = `
      DELETE FROM courses 
      WHERE course_id = $1;
    `;
    await this.client.queryObject(query, [course_id]);
  }
}