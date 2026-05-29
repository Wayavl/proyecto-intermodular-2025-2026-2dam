import { ConfigurationRepository } from "../../domain/repository/configuration-repository.ts";
import { GetUserConfigurationsRequest, SetUserConfigurationRequest, ResetUserConfigurationRequest, CreateConfigurationRequest, CreateConfigurationTranslationRequest, DeleteConfigurationRequest } from "../data/in/configuration.in.ts";
import { UserConfigurationResponse } from "../data/out/configuration.out.ts";

export default class ConfigurationService {
    private configurationRepository: ConfigurationRepository;

    constructor(configurationRepository: ConfigurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    async getUserConfigurations(request: GetUserConfigurationsRequest): Promise<UserConfigurationResponse[]> {
        return await this.configurationRepository.getUserConfigurations(request.user_id, request.language_id);
    }

    async setUserConfigurationValue(request: SetUserConfigurationRequest): Promise<void> {
        await this.configurationRepository.setUserConfigurationValue(request.user_id, request.configuration_id, request.value);
    }

    async resetUserConfiguration(request: ResetUserConfigurationRequest): Promise<void> {
        await this.configurationRepository.deleteUserConfiguration(request.user_id, request.configuration_id);
    }

    async createConfiguration(request: CreateConfigurationRequest): Promise<{ configuration_id: string }> {
        return await this.configurationRepository.createConfiguration(request.type);
    }

    async createConfigurationTranslation(request: CreateConfigurationTranslationRequest): Promise<void> {
        await this.configurationRepository.createConfigurationTranslation(request.configurationId, request.languageName, request.configurationName);
    }

    async deleteConfiguration(request: DeleteConfigurationRequest): Promise<void> {
        await this.configurationRepository.deleteConfiguration(request.configurationId);
    }
}
