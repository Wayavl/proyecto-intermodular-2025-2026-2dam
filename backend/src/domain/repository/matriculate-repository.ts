import { Matriculate } from "../model/matriculate.ts";

export interface MatriculateRepository {
  enroll(user_id: string, course_id: string): Promise<void>;
  unenroll(user_id: string, course_id: string): Promise<void>;
  markAsFinished(user_id: string, course_id: string): Promise<void>;
  getUserEnrollments(user_id: string): Promise<Matriculate[]>;
}
