-- V3__create_route_calculations_table.sql
CREATE TABLE route_calculations (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL UNIQUE,
    cfn DECIMAL(3,2) NOT NULL CHECK (cfn > 0 AND cfn <= 1),
    m_coefficient DECIMAL(3,2) NOT NULL CHECK (m_coefficient > 0 AND m_coefficient <= 1),
    bcc INTEGER NOT NULL,
    pcc INTEGER NOT NULL,
    rcc INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_calculations_route FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);

CREATE INDEX idx_calculations_route_id ON route_calculations(route_id);
