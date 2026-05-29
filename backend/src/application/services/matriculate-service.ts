import { MatriculateRepository } from "../../domain/repository/matriculate-repository.ts";
import { EnrollUserRequest, GetUserEnrollmentsRequest } from "../data/in/matriculate.in.ts";
import { EnrollmentResponse } from "../data/out/matriculate.out.ts";

export default class MatriculateService {
    private matriculateRepository: MatriculateRepository;

    constructor(matriculateRepository: MatriculateRepository) {
        this.matriculateRepository = matriculateRepository;
    }

    async enroll(request: EnrollUserRequest): Promise<void> {
        await this.matriculateRepository.enroll(request.user_id, request.course_id);
    }

    async unenroll(request: EnrollUserRequest): Promise<void> {
        await this.matriculateRepository.unenroll(request.user_id, request.course_id);
    }

    async markCourseAsFinished(request: EnrollUserRequest): Promise<void> {
        await this.matriculateRepository.markAsFinished(request.user_id, request.course_id);
    }

    async getUserEnrollments(request: GetUserEnrollmentsRequest): Promise<EnrollmentResponse[]> {
        return await this.matriculateRepository.getUserEnrollments(request.user_id);
    }
}
