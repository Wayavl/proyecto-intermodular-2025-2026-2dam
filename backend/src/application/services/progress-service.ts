import { AlgorithmRepository } from "../../domain/repository/algorithms-repository.ts";
import { LessonRepository } from "../../domain/repository/lesson-repository.ts";
import UserRepository from "../../domain/repository/user-repository.ts";
import { LearnedAlgorithm } from "../../domain/model/learned-algorithm.ts";
import { LearnedLessonDetail } from "../../domain/model/learned-lesson-detail.ts";

export interface CourseLessonProgress {
  course_id: string;
  course_name: string;
  learned_count: number;
  total_count: number;
  progress_percent: number;
  is_completed: boolean;
}

export default class ProgressService {
  constructor(
    private algorithmRepository: AlgorithmRepository,
    private lessonRepository: LessonRepository,
    private userRepository: UserRepository,
  ) {}

  async getLearnedAlgorithms(
    user_id: string,
    language_id: string,
  ): Promise<LearnedAlgorithm[]> {
    return await this.algorithmRepository.getLearnedAlgorithmsByUser(
      user_id,
      language_id,
    );
  }

  async getLearnedLessons(
    user_id: string,
    language_id: string,
  ): Promise<LearnedLessonDetail[]> {
    return await this.lessonRepository.getLearnedLessonsDetailsByUser(
      user_id,
      language_id,
    );
  }

  async getCourseProgressFromLearnedLessons(
    user_id: string,
    language_id: string,
  ): Promise<CourseLessonProgress[]> {
    const learned = await this.getLearnedLessons(user_id, language_id);
    const byCourse = new Map<string, LearnedLessonDetail[]>();

    for (const row of learned) {
      const list = byCourse.get(row.course_id) ?? [];
      list.push(row);
      byCourse.set(row.course_id, list);
    }

    const progress: CourseLessonProgress[] = [];
    for (const [courseId, rows] of byCourse.entries()) {
      const total = await this.lessonRepository.countLessonsByCourse(courseId);
      const learnedCount = rows.length;
      const percent = total > 0
        ? Math.round((learnedCount / total) * 100)
        : 0;
      progress.push({
        course_id: courseId,
        course_name: rows[0]?.course_name ?? "Course",
        learned_count: learnedCount,
        total_count: total,
        progress_percent: percent,
        is_completed: total > 0 && learnedCount >= total,
      });
    }

    return progress.sort((a, b) => b.progress_percent - a.progress_percent);
  }

  async resolveUserIdForUsername(username: string): Promise<string | null> {
    const user = await this.userRepository.readUser(username);
    return user ? String(user.user_id) : null;
  }
}
