import { Algorithms } from "../model/algorithms.ts";
import { LearnedAlgorithm } from "../model/learned-algorithm.ts";

export interface AlgorithmRepository {
  // CREATE
  createAlgorithm(
    is_premium: boolean,
    controls_yml?: string,
  ): Promise<{ algorithm_id: string }>;
  createAlgorithmTranslation(
    algorithm_id: string,
    language_id: string,
    title: string,
    subject?: string,
    explanation_md?: string,
    use_cases_md?: string,
  ): Promise<void>;

  // READ
  readAlgorithm(
    algorithm_id: string,
    language_id: string,
  ): Promise<Algorithms | null>;
  readAllAlgorithms(language_id: string): Promise<Algorithms[]>;
  getLearnedAlgorithmsByUser(
    user_id: string,
    language_id: string,
  ): Promise<LearnedAlgorithm[]>;

  // UPDATE (Void para el 204 No Content)
  updateAlgorithmPremium(
    algorithm_id: string,
    is_premium: boolean,
  ): Promise<void>;
  updateAlgorithmControls(
    algorithm_id: string,
    controls_yml: string,
  ): Promise<void>;
  updateAlgorithmTranslation(
    algorithm_id: string,
    language_id: string,
    title: string,
    subject?: string,
    explanation_md?: string,
    use_cases_md?: string,
  ): Promise<void>;

  // DELETE
  deleteAlgorithm(algorithm_id: string): Promise<void>;
}
