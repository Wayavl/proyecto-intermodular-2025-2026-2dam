import { AlgorithmRepository } from "../../domain/repository/algorithms-repository.ts";
import { GetAlgorithmsRequest, GetAlgorithmRequest, CreateAlgorithmRequest, CreateAlgorithmTranslationRequest, UpdateAlgorithmPremiumRequest, UpdateAlgorithmTranslationRequest, UpdateAlgorithmControlsRequest, DeleteAlgorithmRequest } from "../data/in/algorithm.in.ts";
import { AlgorithmResponse } from "../data/out/algorithm.out.ts";
import { parse } from "jsr:@std/yaml@1";

/**
 * Converts a YAML controls configuration to a JSON string representation,
 * with backward compatibility for dictionary configurations.
 */
function yamlToJson(yamlStr: string | null | undefined): string | null {
    if (!yamlStr) return null;
    try {
        const parsed = parse(yamlStr);
        if (Array.isArray(parsed)) {
            return JSON.stringify(parsed);
        } else if (typeof parsed === "object" && parsed !== null) {
            // Convert dictionary format to array format for backward compatibility
            const arrayFormat = Object.entries(parsed).map(([key, val]: [string, any]) => {
                const isSlider = val && typeof val.min === "number" && typeof val.max === "number";
                return {
                    type: isSlider ? "slider" : "toggle",
                    id: key,
                    label: key.toUpperCase().replace(/_/g, " "),
                    min: val?.min ?? null,
                    max: val?.max ?? null,
                    defaultValue: val?.default ?? val?.defaultValue ?? null
                };
            });
            // Append default button since older config didn't have one
            arrayFormat.push({
                type: "button",
                id: "run_default",
                label: "Run Benchmark",
                action: "run",
                params: {}
            });
            return JSON.stringify(arrayFormat);
        }
        return null;
    } catch (e) {
        console.error("Failed to parse YAML:", e);
        return null;
    }
}

export default class AlgorithmService {
    private algorithmRepository: AlgorithmRepository;

    constructor(algorithmRepository: AlgorithmRepository) {
        this.algorithmRepository = algorithmRepository;
    }

    async getAlgorithms(request: GetAlgorithmsRequest): Promise<AlgorithmResponse[]> {
        const algorithms = await this.algorithmRepository.readAllAlgorithms(request.languageId);
        return algorithms.map(algo => ({
            ...algo,
            controls_json: yamlToJson(algo.controls_yml)
        }));
    }

    async getAlgorithm(request: GetAlgorithmRequest): Promise<AlgorithmResponse | null> {
        const algo = await this.algorithmRepository.readAlgorithm(request.algorithmId, request.languageId);
        if (!algo) return null;
        return {
            ...algo,
            controls_json: yamlToJson(algo.controls_yml)
        };
    }

    async createAlgorithm(request: CreateAlgorithmRequest): Promise<{ algorithm_id: string }> {
        return await this.algorithmRepository.createAlgorithm(request.is_premium, request.controls_yml);
    }

    async createAlgorithmTranslation(request: CreateAlgorithmTranslationRequest): Promise<void> {
        await this.algorithmRepository.createAlgorithmTranslation(request.algorithmId, request.languageName, request.title, request.subject, request.explanation_md, request.use_cases_md);
    }

    async updateAlgorithmPremium(request: UpdateAlgorithmPremiumRequest): Promise<void> {
        await this.algorithmRepository.updateAlgorithmPremium(request.algorithmId, request.is_premium);
    }

    async updateAlgorithmControls(request: UpdateAlgorithmControlsRequest): Promise<void> {
        await this.algorithmRepository.updateAlgorithmControls(request.algorithmId, request.controls_yml);
    }

    async updateAlgorithmTranslation(request: UpdateAlgorithmTranslationRequest): Promise<void> {
        await this.algorithmRepository.updateAlgorithmTranslation(request.algorithmId, request.languageName, request.title, request.subject, request.explanation_md, request.use_cases_md);
    }

    async deleteAlgorithm(request: DeleteAlgorithmRequest): Promise<void> {
        await this.algorithmRepository.deleteAlgorithm(request.algorithmId);
    }
}
