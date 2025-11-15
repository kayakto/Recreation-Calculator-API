-- V2__create_routes_table.sql
CREATE TABLE routes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    route_name VARCHAR(255) NOT NULL,
    route_type VARCHAR(50) NOT NULL CHECK (route_type IN ('однодневный', 'многодневный')),
    t_sut DECIMAL(4,1) NOT NULL CHECK (t_sut > 0 AND t_sut <= 24),
    t_sezon INTEGER NOT NULL CHECK (t_sezon > 0),
    gs INTEGER NOT NULL CHECK (gs > 0),
    tl INTEGER NOT NULL CHECK (tl > 0),
    td_array JSONB NOT NULL,
    dt_array JSONB NOT NULL,
    dg_array JSONB NOT NULL,
    v_array JSONB NOT NULL,
    ecological_factors JSONB DEFAULT '[]'::jsonb,
    management_factors JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_routes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_routes_user_id ON routes(user_id);
CREATE INDEX idx_routes_created_at ON routes(created_at DESC);
