# Paralearn

Why are we named after Paralearn?

- This name's from "Paralelism" + "Learn" Slogan: Learn

## Why choose us

Most applications that are dedicated in teaching you how the algorithm works but
forget to tell you how to implement them. We offer an easy way to introduce
yourself in the world of paralelism teaching you how to think and implement
algorithms on GPU and CPU.

## Project's Architecture

### Persistence

I've decided to use PostgreSQL for simplicity and documentation. It is widely
use in large companies and offers a well structured model with a lot of
compatibilty between API's

### Frontend

After thinking for a long time I decided to use Android Jetpack Compose on the
frontend because there were only two options available because of the project
constains the teacher made me use. The other option was Kotlin Multiplatform on
Web but it was very hard to program with almost 0 documentation.

### Backend

I decided to use Deno, a server like Node.js with the main difference being
safer, faster and offering an first class support for WebGPU, the GPU API I am
going to teach. Deno also supports Rust which may help me teach low level
algorithms for CPU.

### Deploy

My service is going to be deployed in Docker.

- Building the android APK.
- Creating the database.
- Serving the Server.
- Creating TLS certificates.

## Main Database

The database name's "paralearn". -- Extensión necesaria para generar UUIDs
automáticamente en PostgreSQL CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ========================================== -- TABLAS PRINCIPALES (Entidades
Fuertes) -- ==========================================

CREATE TABLE language ( language_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
language_name TEXT NOT NULL UNIQUE );

CREATE TABLE users ( user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
username TEXT UNIQUE NOT NULL, email TEXT UNIQUE NOT NULL, password_hash TEXT
NOT NULL, join_date DATE NOT NULL DEFAULT CURRENT_DATE, premium_expiration_date
DATE, last_streak DATE, streak INTEGER NOT NULL DEFAULT 0 );

CREATE TABLE courses ( course_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
is_premium BOOLEAN NOT NULL DEFAULT FALSE );

CREATE TABLE courses_translations( course_translation_id UUID PRIMARY KEY
DEFAULT gen_random_uuid(), course_id UUID NOT NULL REFERENCES courses(course_id)
ON DELETE CASCADE, language UUID NOT NULL REFERENCES language(language_id) ON
DELETE CASCADE, name TEXT NOT NULL UNIQUE ); CREATE TABLE algorithms (
algorithm_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), is_premium BOOLEAN NOT
NULL DEFAULT FALSE, controls_yml TEXT );

CREATE TABLE algorithms_translations( algorithm_translation_id UUID PRIMARY KEY
DEFAULT gen_random_uuid(), algorithm_id UUID NOT NULL REFERENCES
algorithms(algorithm_id) ON DELETE CASCADE, language UUID NOT NULL REFERENCES
language(language_id) ON DELETE CASCADE, title TEXT UNIQUE NOT NULL, subject
TEXT, explanation_md TEXT, use_cases_md TEXT );

CREATE TABLE configurations ( configuration_id UUID PRIMARY KEY DEFAULT
gen_random_uuid(), type TEXT NOT NULL );

CREATE TABLE configurations_translations ( configuration_translation_id UUID
PRIMARY KEY DEFAULT gen_random_uuid(), configuration_id UUID NOT NULL REFERENCES
configurations(config_id), language UUID NOT NULL REFERENCES
language(language_id) ON DELETE CASCADE, configuration_name TEXT NOT NULL UNIQUE
);

-- ========================================== -- TABLAS CON DEPENDENCIAS
(Entidades Débiles) -- ==========================================

CREATE TABLE lessons ( lesson_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
course_id UUID NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
algorithm_id UUID REFERENCES algorithms(algorithm_id) ON DELETE SET NULL,
lesson_order INTEGER NOT NULL, -- 'order' es palabra reservada en SQL, mejor
usar 'lesson_order' );

CREATE TABLE lessons_translations ( lesson_translation_id UUID PRIMARY KEY
DEFAULT gen_random_uuid(), lesson_id UUID NOT NULL REFERENCES lessons(lesson_id)
ON DELETE CASCADE, subject TEXT NOT NULL, name TEXT UNIQUE NOT NULL, content_md
TEXT );

-- ========================================== -- TABLAS DE RELACIÓN (Muchos a
Muchos) -- ==========================================

CREATE TABLE matriculate ( user_id UUID REFERENCES users(user_id) ON DELETE
CASCADE, course_id UUID REFERENCES courses(course_id) ON DELETE CASCADE,
begin_date DATE NOT NULL DEFAULT CURRENT_DATE, finish_date DATE, PRIMARY KEY
(user_id, course_id) );

CREATE TABLE algorithms_learned ( user_id UUID REFERENCES users(user_id) ON
DELETE CASCADE, algorithm_id UUID REFERENCES algorithms(algorithm_id) ON DELETE
CASCADE, finish_date DATE NOT NULL DEFAULT CURRENT_DATE, PRIMARY KEY (user_id,
algorithm_id) );

CREATE TABLE lessons_learned ( user_id UUID REFERENCES users(user_id) ON DELETE
CASCADE, lesson_id UUID REFERENCES lessons(lesson_id) ON DELETE CASCADE,
finish_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_DATE, PRIMARY KEY (user_id,
lesson_id) );

CREATE TABLE configured ( config_id TEXT REFERENCES
configurations(configurations_id) ON DELETE CASCADE, user_id UUID REFERENCES
users(user_id) ON DELETE CASCADE, value TEXT NOT NULL, PRIMARY KEY
(configurations_id, user_id) );

-- ========================================== -- ÍNDICES PARA LA TABLA 'lessons'
-- ==========================================

-- 1. Buscar lecciones de un curso y ordenarlas -- Cuando un usuario entra a un
curso, tu API hará: SELECT * FROM lessons WHERE course_id = X ORDER BY
lesson_order -- Un índice compuesto (course_id + lesson_order) hace que esta
query sea instantánea. CREATE INDEX idx_lessons_course_id_order ON
lessons(course_id, lesson_order);

-- 2. Saber qué lecciones usan un algoritmo específico -- Muy útil si decides
actualizar un algoritmo y necesitas saber qué lecciones impacta. CREATE INDEX
idx_lessons_algorithm_id ON lessons(algorithm_id);

-- ========================================== -- ÍNDICES PARA TABLAS
MANY-TO-MANY -- ========================================== -- Nota: En tus
tablas intermedias, la PRIMARY KEY compuesta (ej. user_id, course_id) -- ya
actúa como un índice perfecto cuando buscas por 'user_id'. -- Pero si haces la
búsqueda inversa (ej. "dame todos los usuarios del curso X"), necesitas un
índice para la segunda columna.

-- 3. Buscar todos los alumnos matriculados en un curso específico CREATE INDEX
idx_matriculate_course_id ON matriculate(course_id);

-- 4. Buscar todos los usuarios que han completado un algoritmo concreto
(estadísticas de uso) CREATE INDEX idx_algorithms_learned_algorithm_id ON
algorithms_learned(algorithm_id);

-- 5. Buscar todos los usuarios que han completado una lección concreta CREATE
INDEX idx_lessons_learned_lesson_id ON lessons_learned(lesson_id);

-- 6. Buscar todas las configuraciones aplicadas a un usuario específico -- (La
PK actual es config_id, user_id; este índice optimiza buscar por user_id) CREATE
INDEX idx_configured_user_id ON configured(user_id);
