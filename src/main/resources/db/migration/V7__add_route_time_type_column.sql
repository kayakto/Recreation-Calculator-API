-- V7__add_route_time_type_column.sql

-- Добавить колонку route_time_type в таблицу routes
ALTER TABLE routes
ADD COLUMN route_time_type VARCHAR(50)
CHECK (route_time_type IN ('fixed_time', 'unlimited_time'));

-- Установить DEFAULT значение для существующих записей
ALTER TABLE routes
ALTER COLUMN route_time_type SET DEFAULT 'fixed_time';

-- Заполнить существующие записи значением по умолчанию
UPDATE routes
SET route_time_type = 'fixed_time'
WHERE route_time_type IS NULL;

-- Сделать колонку обязательной
ALTER TABLE routes
ALTER COLUMN route_time_type SET NOT NULL;