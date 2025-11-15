-- Вставьте с простым паролем (без BCrypt)
INSERT INTO users (email, login, password, created_at, updated_at)
VALUES ('user', 'user', 'user', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Рекомендации
INSERT INTO recommendations (factor_type, factor_number, factor_description, recommendation_text) VALUES
('ecological', 1, 'Присутствуют неблагоприятные погодные условия', 'Рекомендуется ограничить посещение'),
('ecological', 2, 'Высокий уровень эрозии почвы', 'Необходимо укрепление тропы'),
('management', 1, 'Отсутствие системы мониторинга', 'Внедрить систему контроля'),
('management', 2, 'Недостаточная инфраструктура', 'Развитие инфраструктуры')
ON CONFLICT DO NOTHING;
