export interface GetUserConfigurationsRequest {
    user_id: string;
    language_id: string;
}

export interface SetUserConfigurationRequest {
    user_id: string;
    configuration_id: string;
    value: string;
}

export interface ResetUserConfigurationRequest {
    user_id: string;
    configuration_id: string;
}

export interface CreateConfigurationRequest {
    type: string;
}

export interface CreateConfigurationTranslationRequest {
    configurationId: string;
    languageName: string;
    configurationName: string;
}

export interface DeleteConfigurationRequest {
    configurationId: string;
}
