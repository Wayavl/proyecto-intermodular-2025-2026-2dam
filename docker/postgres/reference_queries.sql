-- Paralearn — referencia PostgreSQL (alineada con init.db y la app Android)
-- Conexión Docker: host localhost:5432, db paralearn, user user_admin

-- =============================================================================
-- Catálogo fijo (seed)
-- =============================================================================

-- Idiomas de contenido / UI (language_name = código enviado en ?lang=)
--   en  → aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
--   es  → bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb
--   zh  → cccccccc-cccc-cccc-cccc-cccccccccccc

-- Configuraciones de usuario (configuration_id = UUID en API y tabla configured)
--   00000000-0000-0000-0000-000000000001  SELECTION  Instruction language   (en|es|zh)
--   00000000-0000-0000-0000-000000000002  BOOLEAN    Learning notifications (true|false)
--   00000000-0000-0000-0000-000000000003  BOOLEAN    Telemetry sync         (true|false)
--   00000000-0000-0000-0000-000000000004  BOOLEAN    Dark theme             (true=oscuro, false=claro)

SELECT language_id, language_name FROM language ORDER BY language_name;

SELECT c.configuration_id, c.type, ct.configuration_name, l.language_name
FROM configurations c
JOIN configurations_translations ct ON ct.configuration_id = c.configuration_id
JOIN language l ON l.language_id = ct.language
ORDER BY c.configuration_id, l.language_name;

-- =============================================================================
-- Usuarios y sesión
-- =============================================================================

SELECT user_id, username, email, join_date, streak, premium_expiration_date
FROM users
ORDER BY join_date DESC;

-- =============================================================================
-- Preferencias de usuario (configured)
-- =============================================================================

SELECT
    u.username,
    cfg.configuration_id,
    ct.configuration_name,
    cfg.value
FROM configured cfg
JOIN users u ON u.user_id = cfg.user_id
LEFT JOIN configurations_translations ct
    ON ct.configuration_id = cfg.config_id
    AND ct.language = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'
ORDER BY u.username, cfg.config_id;

-- Ejemplo: tema oscuro ON para un usuario (sustituir USER_UUID)
-- INSERT INTO configured (config_id, user_id, value) VALUES
--     ('00000000-0000-0000-0000-000000000004', 'USER_UUID', 'true')
-- ON CONFLICT (config_id, user_id) DO UPDATE SET value = EXCLUDED.value;

-- Ejemplo: idioma chino
-- INSERT INTO configured (config_id, user_id, value) VALUES
--     ('00000000-0000-0000-0000-000000000001', 'USER_UUID', 'zh')
-- ON CONFLICT (config_id, user_id) DO UPDATE SET value = EXCLUDED.value;

-- =============================================================================
-- Migración en BD ya existente (si faltan zh o dark theme)
-- =============================================================================

INSERT INTO language (language_id, language_name) VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'zh')
ON CONFLICT (language_name) DO NOTHING;

INSERT INTO configurations (configuration_id, type) VALUES
    ('00000000-0000-0000-0000-000000000004', 'BOOLEAN')
ON CONFLICT (configuration_id) DO NOTHING;

INSERT INTO configurations_translations (configuration_id, language, configuration_name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '教学语言'),
    ('00000000-0000-0000-0000-000000000002', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '学习通知'),
    ('00000000-0000-0000-0000-000000000003', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '遥测同步'),
    ('00000000-0000-0000-0000-000000000004', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Dark theme'),
    ('00000000-0000-0000-0000-000000000004', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Tema oscuro'),
    ('00000000-0000-0000-0000-000000000004', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '深色主题')
ON CONFLICT (configuration_name) DO NOTHING;

-- =============================================================================
-- Contenido y progreso (resumen)
-- =============================================================================

SELECT l.language_name, COUNT(*) AS courses
FROM courses_translations ct
JOIN language l ON l.language_id = ct.language
GROUP BY l.language_name;

SELECT u.username, c.course_id, m.begin_date, m.finish_date
FROM matriculate m
JOIN users u ON u.user_id = m.user_id
JOIN courses c ON c.course_id = m.course_id;

SELECT u.username, a.algorithm_id, al.finish_date
FROM algorithms_learned al
JOIN users u ON u.user_id = al.user_id;
