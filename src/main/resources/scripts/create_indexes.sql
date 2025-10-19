-- ===============================================
-- Script: create_indexes.sql
-- Objetivo: Criação de índices adicionais no PostgreSQL
-- ===============================================

CREATE INDEX IF NOT EXISTS idx_paciente_data
    ON exames (paciente_id, data_coleta DESC);

CREATE INDEX IF NOT EXISTS idx_status_resultado
    ON exames (status_exame, data_resultado DESC);

CREATE INDEX IF NOT EXISTS idx_created_at
    ON exames (created_at DESC);
