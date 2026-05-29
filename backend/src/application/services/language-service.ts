import { LanguageRepository } from "../../domain/repository/language-repository.ts";
import { CreateLanguageRequest, DeleteLanguageRequest } from "../data/in/language.in.ts";
import { LanguageResponse } from "../data/out/language.out.ts";

export default class LanguageService {
    private languageRepository: LanguageRepository;

    constructor(languageRepository: LanguageRepository) {
        this.languageRepository = languageRepository;
    }

    async createLanguage(request: CreateLanguageRequest): Promise<void> {
        await this.languageRepository.createLanguage(request.name);
    }

    async getAllLanguages(): Promise<LanguageResponse[]> {
        const models = await this.languageRepository.readAllLanguages();
        return models.map(m => ({
            language_id: m.language_id!,
            language_name: m.language_name
        }));
    }

    async deleteLanguage(request: DeleteLanguageRequest): Promise<void> {
        await this.languageRepository.deleteLanguage(request.name);
    }
}
