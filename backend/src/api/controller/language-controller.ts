import { Context } from "@hono/hono";
import LanguageService from "../../application/services/language-service.ts";
import { CreateLanguageRequest, DeleteLanguageRequest } from "../../application/data/in/language.in.ts";

export default class LanguageController {
    private languageService: LanguageService;

    constructor(languageService: LanguageService) {
        this.languageService = languageService;
    }

    async createLanguage(c: Context) {
        try {
            const body = await c.req.json();
            if (!body.name) {
                return c.json({ error: "Missing name" }, 400);
            }
            const request: CreateLanguageRequest = { name: body.name };
            await this.languageService.createLanguage(request);
            return c.json({ message: "Language created" }, 201);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async getLanguages(c: Context) {
        try {
            const languages = await this.languageService.getAllLanguages();
            return c.json({ languages }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async deleteLanguage(c: Context) {
        try {
            const name = c.req.param("name");
            if (!name) return c.json({ error: "Missing language name" }, 400);

            const request: DeleteLanguageRequest = { name };
            await this.languageService.deleteLanguage(request);
            return c.json({ message: "Language deleted" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }
}
