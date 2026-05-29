import { Client } from "@db/postgres";
import UserRepository from "../../domain/repository/user-repository.ts";
import User from "../../domain/model/user.ts";

export default class PostgresUserRepository implements UserRepository {
  client: Client;

  constructor(client: Client) {
    this.client = client;
  }

  async login(
    password_hash: string,
    username?: string,
    email?: string,
  ): Promise<{ username: string }> {
    const query = `
      SELECT username 
      FROM users 
      WHERE (username = $1 OR email = $2) AND password_hash = $3;
    `;
    const result = await this.client.queryObject<{ username: string }>(query, [
      username || null,
      email || null,
      password_hash,
    ]);

    if (result.rowCount === 0) {
      throw new Error("Credenciales inválidas"); // O tu error personalizado
    }
    return result.rows[0];
  }

  async register(
    username: string,
    email: string,
    password_hash: string,
  ): Promise<{ username: string }> {
    const query = `
      INSERT INTO users (username, email, password_hash) 
      VALUES ($1, $2, $3) 
      RETURNING username;
    `;
    const result = await this.client.queryObject<{ username: string }>(query, [
      username,
      email,
      password_hash,
    ]);
    return result.rows[0];
  }

  async readUser(username: string): Promise<User | null> {
    const query = `SELECT * FROM users WHERE username = $1;`;
    const result = await this.client.queryObject<User>(query, [username]);
    return result.rows[0] || null;
  }

  async readUserById(user_id: string): Promise<User | null> {
    const query = `SELECT * FROM users WHERE user_id = $1;`;
    const result = await this.client.queryObject<User>(query, [user_id]);
    return result.rows[0] || null;
  }

  async findByUsernameOrEmail(identifier: string): Promise<User | null> {
    const query = `SELECT * FROM users WHERE username = $1 OR email = $1;`;
    const result = await this.client.queryObject<User>(query, [identifier]);
    return result.rows[0] || null;
  }

  async updateUsername(username: string, new_username: string): Promise<void> {
    const query = `UPDATE users SET username = $1 WHERE username = $2;`;
    await this.client.queryObject(query, [new_username, username]);
  }

  async updateEmail(username: string, new_email: string): Promise<void> {
    const query = `UPDATE users SET email = $1 WHERE username = $2;`;
    await this.client.queryObject(query, [new_email, username]);
  }

  async updatePassword(
    username: string,
    new_password_hash: string,
  ): Promise<void> {
    const query = `UPDATE users SET password_hash = $1 WHERE username = $2;`;
    await this.client.queryObject(query, [new_password_hash, username]);
  }

  async updateStreak(username: string): Promise<void> {
    // Incrementa la racha y actualiza automáticamente la fecha al día de hoy
    const query = `
      UPDATE users 
      SET streak = streak + 1, last_streak = CURRENT_DATE 
      WHERE username = $1;
    `;
    await this.client.queryObject(query, [username]);
  }

  async updatePremium(username: string, months: number): Promise<void> {
    // Lógica avanzada de PostgreSQL:
    // Si no tiene premium o ya caducó, suma los meses desde HOY.
    // Si aún tiene premium activo, suma los meses a la fecha de caducidad futura.
    const query = `
      UPDATE users 
      SET premium_expiration_date = GREATEST(CURRENT_DATE, COALESCE(premium_expiration_date, CURRENT_DATE)) + ($1 || ' months')::INTERVAL
      WHERE username = $2;
    `;
    await this.client.queryObject(query, [months, username]);
  }

  async deleteUser(username: string): Promise<void> {
    const query = `DELETE FROM users WHERE username = $1;`;
    await this.client.queryObject(query, [username]);
  }
}
