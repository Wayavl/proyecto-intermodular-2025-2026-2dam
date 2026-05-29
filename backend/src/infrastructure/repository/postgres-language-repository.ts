import { Client } from "@db/postgres";
import { Language } from "../../domain/model/language.ts";
import { LanguageRepository } from "../../domain/repository/language-repository.ts";

export default class PostgresLanguageRepository implements LanguageRepository {
  client: Client;
  constructor(client: Client) {
    this.client = client;
  }

  async createLanguage(name: string): Promise<{ language_id: string }> {
    const query =
      `INSERT INTO language (language_name) VALUES ($1) RETURNING language_id;`;
    const result = await this.client.queryObject<{ language_id: string }>(
      query,
      [name],
    );
    return result.rows[0];
  }

  async readAllLanguages(): Promise<Language[]> {
    const query = `SELECT * FROM language ORDER BY language_name ASC;`;
    const result = await this.client.queryObject<Language>(query);
    return result.rows;
  }

  async deleteLanguage(name: string): Promise<void> {
    const query = `DELETE FROM language WHERE language_name = $1;`;
    await this.client.queryObject(query, [name]);
  }
}
