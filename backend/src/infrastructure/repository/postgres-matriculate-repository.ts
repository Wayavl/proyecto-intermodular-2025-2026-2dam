import { Client } from "@db/postgres";
import { Matriculate } from "../../domain/model/matriculate.ts";
import { MatriculateRepository } from "../../domain/repository/matriculate-repository.ts";

export default class PostgresMatriculateRepository
  implements MatriculateRepository {
  client: Client;
  constructor(client: Client) {
    this.client = client;
  }

  async enroll(user_id: string, course_id: string): Promise<void> {
    const query = `
      INSERT INTO matriculate (user_id, course_id) 
      VALUES ($1, $2) ON CONFLICT DO NOTHING;`;
    await this.client.queryObject(query, [user_id, course_id]);
  }

  async unenroll(user_id: string, course_id: string): Promise<void> {
    const query =
      `DELETE FROM matriculate WHERE user_id = $1 AND course_id = $2;`;
    await this.client.queryObject(query, [user_id, course_id]);
  }

  async markAsFinished(user_id: string, course_id: string): Promise<void> {
    const query = `
      UPDATE matriculate SET finish_date = CURRENT_DATE 
      WHERE user_id = $1 AND course_id = $2;`;
    await this.client.queryObject(query, [user_id, course_id]);
  }

  async getUserEnrollments(user_id: string): Promise<Matriculate[]> {
    const query = `SELECT * FROM matriculate WHERE user_id = $1;`;
    const result = await this.client.queryObject<Matriculate>(query, [user_id]);
    return result.rows;
  }
}
