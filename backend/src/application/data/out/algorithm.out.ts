export interface AlgorithmResponse {
    algorithm_id: string;
    is_premium: boolean;
    controls_yml?: string | null;
    controls_json?: string | null;
    title?: string;
    subject?: string | null;
    explanation_md?: string | null;
    use_cases_md?: string | null;
}

export interface ExecuteAlgorithmResponse {
    success: boolean;
    message: string;
}
