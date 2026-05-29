import { ConfigurationCatalog } from "../model/configuration-catalog.ts";
import { UserConfiguration } from "../model/user-configuration.ts";

export interface ConfigurationRepository {
  // ================= CATÁLOGO BASE =================
  createConfiguration(type: string): Promise<{ configuration_id: string }>;
  createConfigurationTranslation(
    configuration_id: string,
    language_id: string,
    configuration_name: string,
  ): Promise<void>;

  readConfiguration(
    configuration_id: string,
    language_id: string,
  ): Promise<ConfigurationCatalog | null>;
  deleteConfiguration(configuration_id: string): Promise<void>;

  // ================= CONFIGURACIÓN DEL USUARIO (La tabla 'configured') =================

  // Obtiene todas las configuraciones aplicadas de un usuario con sus nombres traducidos
  getUserConfigurations(
    user_id: string,
    language_id: string,
  ): Promise<UserConfiguration[]>;

  // Inserta o actualiza el valor de una configuración para un usuario
  setUserConfigurationValue(
    user_id: string,
    configuration_id: string,
    value: string,
  ): Promise<void>;

  // Borra una configuración específica de un usuario (para que vuelva al valor por defecto)
  deleteUserConfiguration(
    user_id: string,
    configuration_id: string,
  ): Promise<void>;
}
