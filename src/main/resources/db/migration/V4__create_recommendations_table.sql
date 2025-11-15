-- V4__create_recommendations_table.sql
CREATE TABLE recommendations (
    id BIGSERIAL PRIMARY KEY,
    factor_type VARCHAR(50) NOT NULL CHECK (factor_type IN ('ecological', 'management')),
    factor_number INTEGER NOT NULL,
    factor_description TEXT NOT NULL,
    recommendation_text TEXT NOT NULL,
    CONSTRAINT uq_recommendations_type_number UNIQUE (factor_type, factor_number)
);

CREATE INDEX idx_recommendations_type ON recommendations(factor_type);
CREATE INDEX idx_recommendations_number ON recommendations(factor_number);
