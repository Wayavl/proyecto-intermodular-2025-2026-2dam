import { Client } from "@db/postgres";
import { AlgorithmRepository } from "../../domain/repository/algorithms-repository.ts";
import { Algorithms } from "../../domain/model/algorithms.ts";
import { LearnedAlgorithm } from "../../domain/model/learned-algorithm.ts";

export default class PostgresAlgorithmRepository
  implements AlgorithmRepository {
  client: Client;

  constructor(client: Client) {
    this.client = client;
  }

  // ==========================================
  // CREATE
  // ==========================================

  async createAlgorithm(
    is_premium: boolean = false,
    controls_yml: string = "",
  ): Promise<{ algorithm_id: string }> {
    const query = `
      INSERT INTO algorithms (is_premium, controls_yml) 
      VALUES ($1, $2) 
      RETURNING algorithm_id;
    `;
    const result = await this.client.queryObject<{ algorithm_id: string }>(
      query,
      [
        is_premium,
        controls_yml,
      ],
    );
    return result.rows[0];
  }

  async createAlgorithmTranslation(
    algorithm_id: string,
    language_id: string,
    title: string,
    subject?: string,
    explanation_md?: string,
    use_cases_md?: string,
  ): Promise<void> {
    const query = `
      INSERT INTO algorithms_translations 
        (algorithm_id, language, title, subject, explanation_md, use_cases_md) 
      VALUES ($1, (SELECT language_id FROM language WHERE language_name = $2), $3, $4, $5, $6);
    `;
    await this.client.queryObject(query, [
      algorithm_id,
      language_id,
      title,
      subject || null,
      explanation_md || null,
      use_cases_md || null,
    ]);
  }

  // ==========================================
  // READ
  // ==========================================

  async readAlgorithm(
    algorithm_id: string,
    language_id: string,
  ): Promise<Algorithms | null> {
    const query = `
      SELECT 
        a.algorithm_id, a.is_premium, a.controls_yml, 
        at.title, at.subject, at.explanation_md, at.use_cases_md
      FROM algorithms a
      LEFT JOIN language l ON l.language_name = $2
      LEFT JOIN algorithms_translations at 
        ON a.algorithm_id = at.algorithm_id AND at.language = l.language_id
      WHERE a.algorithm_id = $1;
    `;
    const result = await this.client.queryObject<Algorithms>(query, [
      algorithm_id,
      language_id,
    ]);
    return result.rows[0] || null;
  }

  async readAllAlgorithms(language_id: string): Promise<Algorithms[]> {
    const query = `
      SELECT 
        a.algorithm_id, a.is_premium, a.controls_yml, 
        at.title, at.subject, at.explanation_md, at.use_cases_md
      FROM algorithms a
      JOIN language l ON l.language_name = $1
      JOIN algorithms_translations at 
        ON a.algorithm_id = at.algorithm_id AND at.language = l.language_id;
    `;
    const result = await this.client.queryObject<Algorithms>(query, [
      language_id,
    ]);
    return result.rows;
  }

  async getLearnedAlgorithmsByUser(
    user_id: string,
    language_id: string,
  ): Promise<LearnedAlgorithm[]> {
    const query = `
      SELECT
        a.algorithm_id,
        al.finish_date,
        a.is_premium,
        a.controls_yml,
        at.title,
        at.subject,
        at.explanation_md,
        at.use_cases_md
      FROM algorithms_learned al
      JOIN algorithms a ON a.algorithm_id = al.algorithm_id
      LEFT JOIN language l ON l.language_name = $2
      LEFT JOIN algorithms_translations at
        ON a.algorithm_id = at.algorithm_id AND at.language = l.language_id
      WHERE al.user_id = $1
      ORDER BY al.finish_date DESC;
    `;
    const result = await this.client.queryObject<LearnedAlgorithm>(query, [
      user_id,
      language_id,
    ]);
    return result.rows;
  }

  // ==========================================
  // UPDATE
  // ==========================================

  async updateAlgorithmPremium(
    algorithm_id: string,
    is_premium: boolean,
  ): Promise<void> {
    const query = `
      UPDATE algorithms 
      SET is_premium = $1 
      WHERE algorithm_id = $2;
    `;
    await this.client.queryObject(query, [is_premium, algorithm_id]);
  }

  async updateAlgorithmControls(
    algorithm_id: string,
    controls_yml: string,
  ): Promise<void> {
    const query = `
      UPDATE algorithms 
      SET controls_yml = $1 
      WHERE algorithm_id = $2;
    `;
    await this.client.queryObject(query, [controls_yml, algorithm_id]);
  }

  async updateAlgorithmTranslation(
    algorithm_id: string,
    language_id: string,
    title: string,
    subject?: string,
    explanation_md?: string,
    use_cases_md?: string,
  ): Promise<void> {
    // Usamos COALESCE para no sobrescribir con NULL si solo actualizamos un campo,
    // o actualizamos todo directamente. Para simplificar, actualiza lo que recibe.
    const query = `
      UPDATE algorithms_translations 
      SET 
        title = $1, 
        subject = COALESCE($2, subject), 
        explanation_md = COALESCE($3, explanation_md), 
        use_cases_md = COALESCE($4, use_cases_md)
      WHERE algorithm_id = $5 AND language = (SELECT language_id FROM language WHERE language_name = $6);
    `;
    await this.client.queryObject(query, [
      title,
      subject || null,
      explanation_md || null,
      use_cases_md || null,
      algorithm_id,
      language_id,
    ]);
  }

  // ==========================================
  // DELETE
  // ==========================================

  async deleteAlgorithm(algorithm_id: string): Promise<void> {
    // Al igual que en cursos, el ON DELETE CASCADE en algorithms_translations
    // se encargará de limpiar los textos asociados en todos los idiomas automáticamente.
    const query = `
      DELETE FROM algorithms 
      WHERE algorithm_id = $1;
    `;
    await this.client.queryObject(query, [algorithm_id]);
  }
}
