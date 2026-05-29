export interface LearnedAlgorithm {
  algorithm_id: string;
  finish_date: Date;
  is_premium?: boolean;
  controls_yml?: string | null;
  title?: string;
  subject?: string | null;
  explanation_md?: string | null;
  use_cases_md?: string | null;
}
