import { Context } from "@hono/hono";
import AlgorithmExecutionService from "../../application/services/algorithm-execution-service.ts";
import AlgorithmService from "../../application/services/algorithm-service.ts";
import { ExecuteAlgorithmRequest, GetAlgorithmsRequest, GetAlgorithmRequest, CreateAlgorithmRequest, CreateAlgorithmTranslationRequest, UpdateAlgorithmPremiumRequest, UpdateAlgorithmTranslationRequest, UpdateAlgorithmControlsRequest, DeleteAlgorithmRequest } from "../../application/data/in/algorithm.in.ts";

export default class AlgorithmController {
    private executionService: AlgorithmExecutionService;
    private algorithmService: AlgorithmService;

    constructor(executionService: AlgorithmExecutionService, algorithmService: AlgorithmService) {
        this.executionService = executionService;
        this.algorithmService = algorithmService;
    }

    async executeAlgorithm(c: Context) {
        try {
            const algorithmId = c.req.param("algorithm_id");
            if (!algorithmId) {
                return c.json({ error: "Missing algorithm_id parameter" }, 400);
            }
            
            const body = await c.req.json();
            const request: ExecuteAlgorithmRequest = {
                algorithmId,
                params: body
            };

            // Execute the backend pre-defined action and return its metrics
            const result = await this.executionService.execute(request);

            return c.json(result, 200);
        } catch (error: unknown) {
            if (error instanceof Error) {
                // If it is a mapping error or execution error
                if (error.message.includes("does not exist")) {
                    return c.json({ error: error.message }, 404);
                }
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal execution error occurrred" }, 500);
        }
    }

    async getAlgorithms(c: Context) {
        try {
            const languageId = c.req.query("lang") || "default_lang_uuid";
            const request: GetAlgorithmsRequest = { languageId };
            const algorithms = await this.algorithmService.getAlgorithms(request);
            return c.json({ algorithms }, 200);
        } catch (error: unknown) {
             if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async getAlgorithm(c: Context) {
        try {
            const algorithmId = c.req.param("algorithm_id");
            if (!algorithmId) {
                return c.json({ error: "Missing algorithm_id parameter" }, 400);
            }
            const languageId = c.req.query("lang") || "default_lang_uuid";
            const request: GetAlgorithmRequest = { algorithmId, languageId };
            const algorithm = await this.algorithmService.getAlgorithm(request);
            return c.json({ algorithm }, 200);
        } catch (error: unknown) {
             if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async createAlgorithm(c: Context) {
        try {
            const body = await c.req.json();
            const request: CreateAlgorithmRequest = { is_premium: body.is_premium || false, controls_yml: body.controls_yml || "" };
            const response = await this.algorithmService.createAlgorithm(request);
            return c.json({ message: "Algorithm created", algorithm_id: response.algorithm_id }, 201);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async createAlgorithmTranslation(c: Context) {
        try {
            const algorithmId = c.req.param("algorithm_id");
            const body = await c.req.json();
            if (!algorithmId || !body.language_name || !body.title) return c.json({ error: "Missing parameters" }, 400);

            const request: CreateAlgorithmTranslationRequest = { algorithmId, languageName: body.language_name, title: body.title, subject: body.subject, explanation_md: body.explanation_md, use_cases_md: body.use_cases_md };
            await this.algorithmService.createAlgorithmTranslation(request);
            return c.json({ message: "Algorithm translation created" }, 201);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async updateAlgorithmPremium(c: Context) {
        try {
            const algorithmId = c.req.param("algorithm_id");
            const body = await c.req.json();
            if (!algorithmId || body.is_premium === undefined) return c.json({ error: "Missing parameters" }, 400);

            const request: UpdateAlgorithmPremiumRequest = { algorithmId, is_premium: body.is_premium };
            await this.algorithmService.updateAlgorithmPremium(request);
            return c.json({ message: "Algorithm premium status updated" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async updateAlgorithmControls(c: Context) {
        try {
            const algorithmId = c.req.param("algorithm_id");
            const body = await c.req.json();
            if (!algorithmId || !body.controls_yml) return c.json({ error: "Missing parameters" }, 400);

            const request: UpdateAlgorithmControlsRequest = { algorithmId, controls_yml: body.controls_yml };
            await this.algorithmService.updateAlgorithmControls(request);
            return c.json({ message: "Algorithm controls updated" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async updateAlgorithmTranslation(c: Context) {
        try {
            const algorithmId = c.req.param("algorithm_id");
            const languageName = c.req.param("language_name");
            const body = await c.req.json();
            if (!algorithmId || !languageName || !body.title) return c.json({ error: "Missing parameters" }, 400);

            const request: UpdateAlgorithmTranslationRequest = { algorithmId, languageName, title: body.title, subject: body.subject, explanation_md: body.explanation_md, use_cases_md: body.use_cases_md };
            await this.algorithmService.updateAlgorithmTranslation(request);
            return c.json({ message: "Algorithm translation updated" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async deleteAlgorithm(c: Context) {
        try {
            const algorithmId = c.req.param("algorithm_id");
            if (!algorithmId) return c.json({ error: "Missing algorithm_id parameter" }, 400);
            const request: DeleteAlgorithmRequest = { algorithmId };
            await this.algorithmService.deleteAlgorithm(request);
            return c.json({ message: "Algorithm deleted" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }
}
