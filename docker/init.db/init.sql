-- Extensión necesaria para generar UUIDs automáticamente en PostgreSQL
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==========================================
-- TABLAS PRINCIPALES (Entidades Fuertes)
-- ==========================================

CREATE TABLE language (
    language_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    language_name TEXT NOT NULL UNIQUE
);

CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    join_date DATE NOT NULL DEFAULT CURRENT_DATE,
    premium_expiration_date DATE,
    last_streak DATE,
    streak INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE courses (
    course_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    is_premium BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE courses_translations(
    course_translation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    language UUID NOT NULL REFERENCES language(language_id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE algorithms (
    algorithm_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    is_premium BOOLEAN NOT NULL DEFAULT FALSE,
    controls_yml TEXT
);

CREATE TABLE algorithms_translations(
    algorithm_translation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    algorithm_id UUID NOT NULL REFERENCES algorithms(algorithm_id) ON DELETE CASCADE,
    language UUID NOT NULL REFERENCES language(language_id) ON DELETE CASCADE,
    title TEXT UNIQUE NOT NULL,
    subject TEXT,
    explanation_md TEXT,
    use_cases_md TEXT
);

CREATE TABLE configurations (
    configuration_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type TEXT NOT NULL
);

CREATE TABLE configurations_translations (
    configuration_translation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    configuration_id UUID NOT NULL REFERENCES configurations(configuration_id) ON DELETE CASCADE, -- Fix: Apunta a la PK real
    language UUID NOT NULL REFERENCES language(language_id) ON DELETE CASCADE,
    configuration_name TEXT NOT NULL UNIQUE
);

-- ==========================================
-- TABLAS CON DEPENDENCIAS (Entidades Débiles)
-- ==========================================

CREATE TABLE lessons (
    lesson_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    algorithm_id UUID REFERENCES algorithms(algorithm_id) ON DELETE SET NULL,
    lesson_order INTEGER NOT NULL -- Fix: Eliminada coma huérfana que rompía la sintaxis
);

CREATE TABLE lessons_translations (
    lesson_translation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lesson_id UUID NOT NULL REFERENCES lessons(lesson_id) ON DELETE CASCADE,
    language UUID NOT NULL REFERENCES language(language_id) ON DELETE CASCADE, -- <-- ESTA ES LA COLUMNA QUE FALTA
    subject TEXT NOT NULL,
    name TEXT NOT NULL, -- <-- OJO AQUÍ: Quité el UNIQUE (Lee abajo por qué)
    content_md TEXT,
    CONSTRAINT unique_lesson_language UNIQUE (lesson_id, language) -- Garantiza una traducción por idioma para cada lección
);

-- ==========================================
-- TABLAS DE RELACIÓN (Muchos a Muchos)
-- ==========================================

CREATE TABLE matriculate (
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    course_id UUID REFERENCES courses(course_id) ON DELETE CASCADE,
    begin_date DATE NOT NULL DEFAULT CURRENT_DATE,
    finish_date DATE,
    PRIMARY KEY (user_id, course_id)
);

CREATE TABLE algorithms_learned (
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    algorithm_id UUID REFERENCES algorithms(algorithm_id) ON DELETE CASCADE,
    finish_date DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (user_id, algorithm_id)
);

CREATE TABLE lessons_learned (
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    lesson_id UUID REFERENCES lessons(lesson_id) ON DELETE CASCADE,
    finish_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (user_id, lesson_id)
);

-- Busca la tabla 'configured' en tu init.sql y reemplázala por esta:
CREATE TABLE configured (
    config_id UUID REFERENCES configurations(configuration_id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    value TEXT NOT NULL,
    PRIMARY KEY (config_id, user_id)
);

-- Como la FK de TEXT a UUID no se puede hacer directamente de forma estricta en PostgreSQL sin casts,
-- si tu backend maneja 'config_id' como el nombre del string de configuración (ej: "theme_mode"), 
-- la lógica se mantiene perfectamente con esta estructura de PK compuesta.

-- ==========================================
-- ÍNDICES PARA LA TABLA 'lessons'
-- ==========================================

CREATE INDEX idx_lessons_course_id_order ON lessons(course_id, lesson_order);
CREATE INDEX idx_lessons_algorithm_id ON lessons(algorithm_id);

-- ==========================================
-- ÍNDICES PARA TABLAS MANY-TO-MANY
-- ==========================================

CREATE INDEX idx_matriculate_course_id ON matriculate(course_id);
CREATE INDEX idx_algorithms_learned_algorithm_id ON algorithms_learned(algorithm_id);
CREATE INDEX idx_lessons_learned_lesson_id ON lessons_learned(lesson_id);
CREATE INDEX idx_configured_user_id ON configured(user_id);
CREATE INDEX idx_configured_user_id_composite ON configured(user_id, config_id);

-- ==========================================
-- SEED: idiomas y configuraciones de usuario
-- ==========================================

INSERT INTO language (language_id, language_name) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'en'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'es'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'zh')
ON CONFLICT (language_name) DO NOTHING;

INSERT INTO configurations (configuration_id, type) VALUES
    ('00000000-0000-0000-0000-000000000001', 'SELECTION'),
    ('00000000-0000-0000-0000-000000000002', 'BOOLEAN'),
    ('00000000-0000-0000-0000-000000000003', 'BOOLEAN'),
    ('00000000-0000-0000-0000-000000000004', 'BOOLEAN')
ON CONFLICT (configuration_id) DO NOTHING;

INSERT INTO configurations_translations (configuration_id, language, configuration_name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Instruction language'),
    ('00000000-0000-0000-0000-000000000001', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Idioma de instrucción'),
    ('00000000-0000-0000-0000-000000000002', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Learning notifications'),
    ('00000000-0000-0000-0000-000000000002', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Notificaciones de aprendizaje'),
    ('00000000-0000-0000-0000-000000000003', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Telemetry sync'),
    ('00000000-0000-0000-0000-000000000003', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Sincronización de telemetría'),
    ('00000000-0000-0000-0000-000000000001', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '教学语言'),
    ('00000000-0000-0000-0000-000000000002', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '学习通知'),
    ('00000000-0000-0000-0000-000000000003', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '遥测同步'),
    ('00000000-0000-0000-0000-000000000004', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Dark theme'),
    ('00000000-0000-0000-0000-000000000004', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Tema oscuro'),
    ('00000000-0000-0000-0000-000000000004', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '深色主题')
ON CONFLICT (configuration_name) DO NOTHING;