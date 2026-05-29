import { Language } from "../model/language.ts";

export interface LanguageRepository {
  createLanguage(name: string): Promise<{ language_id: string }>;
  readAllLanguages(): Promise<Language[]>;
  deleteLanguage(name: string): Promise<void>;
}
