import { Client } from "@db/postgres";
import { ConfigurationCatalog } from "../../domain/model/configuration-catalog.ts";
import { UserConfiguration } from "../../domain/model/user-configuration.ts";
import { ConfigurationRepository } from "../../domain/repository/configuration-repository.ts";

export default class PostgresConfigurationRepository
  implements ConfigurationRepository {
  client: Client;

  constructor(client: Client) {
    this.client = client;
  }

  // ==========================================
  // CATÁLOGO BASE ('configurations' y traducciones)
  // ==========================================

  async createConfiguration(
    type: string,
  ): Promise<{ configuration_id: string }> {
    const query = `
      INSERT INTO configurations (type) 
      VALUES ($1) 
      RETURNING configuration_id;
    `;
    const result = await this.client.queryObject<{ configuration_id: string }>(
      query,
      [type],
    );
    return result.rows[0];
  }

  async createConfigurationTranslation(
    configuration_id: string,
    language_id: string,
    configuration_name: string,
  ): Promise<void> {
    const query = `
      INSERT INTO configurations_translations (configuration_id, language, configuration_name) 
      VALUES ($1, (SELECT language_id FROM language WHERE language_name = $2), $3);
    `;
    await this.client.queryObject(query, [
      configuration_id,
      language_id,
      configuration_name,
    ]);
  }

  async readConfiguration(
    configuration_id: string,
    language_id: string,
  ): Promise<ConfigurationCatalog | null> {
    const query = `
      SELECT c.configuration_id, c.type, ct.configuration_name 
      FROM configurations c
      LEFT JOIN language l ON l.language_name = $2
      LEFT JOIN configurations_translations ct 
        ON c.configuration_id = ct.configuration_id AND ct.language = l.language_id
      WHERE c.configuration_id = $1;
    `;
    const result = await this.client.queryObject<ConfigurationCatalog>(query, [
      configuration_id,
      language_id,
    ]);
    return result.rows[0] || null;
  }

  async deleteConfiguration(configuration_id: string): Promise<void> {
    const query = `DELETE FROM configurations WHERE configuration_id = $1;`;
    await this.client.queryObject(query, [configuration_id]);
  }

  // ==========================================
  // CONFIGURACIÓN DEL USUARIO (La tabla 'configured' con el VALUE)
  // ==========================================

  // ESTA ES LA QUERY CLAVE: Junta lo que configuró el usuario + el tipo base + el nombre traducido
  async getUserConfigurations(
    user_id: string,
    language_id: string, // Si aquí pasas 'es' o 'en', la query de abajo es correcta
  ): Promise<UserConfiguration[]> {
    const query = `
      SELECT 
        c.configuration_id, 
        c.type, 
        ct.configuration_name, 
        uc.value 
      FROM configured uc
      JOIN configurations c ON uc.config_id = c.configuration_id
      LEFT JOIN language l ON l.language_name = $2
      LEFT JOIN configurations_translations ct ON c.configuration_id = ct.configuration_id AND ct.language = l.language_id
      WHERE uc.user_id = $1;
    `;
    const result = await this.client.queryObject<UserConfiguration>(query, [
      user_id,
      language_id,
    ]);
    return result.rows;
  }

  // Equivalente a un "UPSERT": Si la configuración no existe para el usuario, la crea. Si existe, actualiza el valor.
  async setUserConfigurationValue(
    user_id: string,
    configuration_id: string,
    value: string,
  ): Promise<void> {
    const query = `
      INSERT INTO configured (user_id, config_id, value)
      VALUES ($1, $2, $3)
      ON CONFLICT (config_id, user_id) 
      DO UPDATE SET value = EXCLUDED.value;
    `;
    await this.client.queryObject(query, [user_id, configuration_id, value]);
  }

  async deleteUserConfiguration(
    user_id: string,
    configuration_id: string,
  ): Promise<void> {
    const query = `
      DELETE FROM configured 
      WHERE user_id = $1 AND config_id = $2;
    `;
    await this.client.queryObject(query, [user_id, configuration_id]);
  }
}
