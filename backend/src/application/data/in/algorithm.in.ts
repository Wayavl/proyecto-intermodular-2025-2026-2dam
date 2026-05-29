export interface GetAlgorithmsRequest {
    languageId: string;
}

export interface GetAlgorithmRequest {
    algorithmId: string;
    languageId: string;
}

export interface ExecuteAlgorithmRequest {
    algorithmId: string;
    params: Record<string, unknown>;
}

export interface CreateAlgorithmRequest {
    is_premium: boolean;
    controls_yml: string;
}
export interface CreateAlgorithmTranslationRequest {
    algorithmId: string;
    languageName: string;
    title: string;
    subject?: string;
    explanation_md?: string;
    use_cases_md?: string;
}
export interface UpdateAlgorithmPremiumRequest {
    algorithmId: string;
    is_premium: boolean;
}
export interface UpdateAlgorithmTranslationRequest {
    algorithmId: string;
    languageName: string;
    title: string;
    subject?: string;
    explanation_md?: string;
    use_cases_md?: string;
}
export interface UpdateAlgorithmControlsRequest {
    algorithmId: string;
    controls_yml: string;
}
export interface DeleteAlgorithmRequest {
    algorithmId: string;
}
