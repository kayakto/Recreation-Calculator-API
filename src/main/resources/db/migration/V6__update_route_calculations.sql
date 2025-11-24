-- V6__update_route_calculations.sql

-- Добавить поле max_groups если его нет
ALTER TABLE route_calculations
ADD COLUMN max_groups INTEGER DEFAULT NULL;

-- Сделать поля nullable (если они еще NOT NULL)
ALTER TABLE route_calculations
ALTER COLUMN pcc DROP NOT NULL;

ALTER TABLE route_calculations
ALTER COLUMN rcc DROP NOT NULL;

ALTER TABLE route_calculations
ALTER COLUMN bcc DROP NOT NULL;