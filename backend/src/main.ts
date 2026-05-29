import { load } from "@std/dotenv";
await load({ export: true });
import { Hono } from "@hono/hono";
import { logger } from "@hono/hono/logger";
import UserRepository from "./domain/repository/user-repository.ts";
import PostgresUserRepository from "./infrastructure/repository/postgres-user-repository.ts";
import PostgresCoursesRepository from "./infrastructure/repository/postgres-courses-repository.ts";
import PostgresLessonRepository from "./infrastructure/repository/postgres-lesson-repository.ts";
import PostgresAlgorithmsRepository from "./infrastructure/repository/postgres-algorithms-repository.ts";
import PostgresMatriculateRepository from "./infrastructure/repository/postgres-matriculate-repository.ts";
import PostgresConfigurationRepository from "./infrastructure/repository/postgres-configuration-repository.ts";
import PostgresLanguageRepository from "./infrastructure/repository/postgres-language-repository.ts";
import { getPostgresDataSource } from "./infrastructure/data-source.ts";

import { setupUserRoutes } from "./api/routes/user.routes.ts";
import UserService from "./application/services/user-service.ts";
import UserController from "./api/controller/user-controller.ts";

import AlgorithmExecutionService from "./application/services/algorithm-execution-service.ts";
import AlgorithmService from "./application/services/algorithm-service.ts";
import AlgorithmController from "./api/controller/algorithm-controller.ts";
import { setupAlgorithmRoutes } from "./api/routes/algorithm.routes.ts";

import CourseService from "./application/services/course-service.ts";
import CourseController from "./api/controller/course-controller.ts";
import { setupCourseRoutes } from "./api/routes/course.routes.ts";

import MatriculateService from "./application/services/matriculate-service.ts";
import MatriculateController from "./api/controller/matriculate-controller.ts";
import { setupMatriculateRoutes } from "./api/routes/matriculate.routes.ts";

import ConfigurationService from "./application/services/configuration-service.ts";
import ConfigurationController from "./api/controller/configuration-controller.ts";
import { setupConfigurationRoutes } from "./api/routes/configuration.routes.ts";

import LanguageService from "./application/services/language-service.ts";
import LanguageController from "./api/controller/language-controller.ts";
import { setupLanguageRoutes } from "./api/routes/language.routes.ts";

import ProgressService from "./application/services/progress-service.ts";
import ProgressController from "./api/controller/progress-controller.ts";

interface GlobalState {
  repos: {
    userRepository: UserRepository;
  };
}

const dataSource = getPostgresDataSource();
const userRepository = new PostgresUserRepository(dataSource);
const coursesRepository = new PostgresCoursesRepository(dataSource);
const lessonRepository = new PostgresLessonRepository(dataSource);
const algorithmRepository = new PostgresAlgorithmsRepository(dataSource);
const matriculateRepository = new PostgresMatriculateRepository(dataSource);
const configurationRepository = new PostgresConfigurationRepository(dataSource);
const languageRepository = new PostgresLanguageRepository(dataSource);

// Manual Dependency Injection
// User
const userService = new UserService(userRepository);
const userController = new UserController(userService);
const progressService = new ProgressService(
  algorithmRepository,
  lessonRepository,
  userRepository,
);
const progressController = new ProgressController(progressService, userRepository);
const userRoutes = setupUserRoutes(userController, progressController);

// Algorithm Engine & Execution Engine
const algorithmExecutionService = new AlgorithmExecutionService();
const algorithmService = new AlgorithmService(algorithmRepository);
const algorithmController = new AlgorithmController(
  algorithmExecutionService,
  algorithmService,
);
const algorithmRoutes = setupAlgorithmRoutes(algorithmController);

// Course Engine
const courseService = new CourseService(coursesRepository, lessonRepository);
const courseController = new CourseController(courseService);
const courseRoutes = setupCourseRoutes(courseController);

// Matriculate Engine
const matriculateService = new MatriculateService(matriculateRepository);
const matriculateController = new MatriculateController(
  matriculateService,
  userRepository,
);
const matriculateRoutes = setupMatriculateRoutes(matriculateController);

// Configuration Engine
const configurationService = new ConfigurationService(configurationRepository);
const configurationController = new ConfigurationController(
  configurationService,
  userRepository,
);
const configurationRoutes = setupConfigurationRoutes(configurationController);

// Language Engine
const languageService = new LanguageService(languageRepository);
const languageController = new LanguageController(languageService);
const languageRoutes = setupLanguageRoutes(languageController);

const app = new Hono();
app.use("*", logger());

import { authMiddleware } from "./api/middlewares/auth-middleware.ts";

app.use("/api/algorithms/*", authMiddleware);
app.use("/api/courses/*", authMiddleware);
app.use("/api/matriculate/*", authMiddleware);
app.use("/api/configurations/*", authMiddleware);
app.use("/api/languages/*", authMiddleware);
// The entire /api/users is handled below, we protect specifically /profile using a quick middleware check or matching /api/users/profile/*
app.use("/api/users/profile/*", authMiddleware);
app.use("/api/users/*/learned/*", authMiddleware);

// Register routes
app.route("/api/users", userRoutes);
app.route("/api/algorithms", algorithmRoutes);
app.route("/api/courses", courseRoutes);
app.route("/api/matriculate", matriculateRoutes);
app.route("/api/configurations", configurationRoutes);
app.route("/api/languages", languageRoutes);

export default app;
