-- ===============================================
-- Script: create_tables.sql
-- Objetivo: Criação da tabela 'exames' no PostgreSQL
-- ===============================================

CREATE TABLE IF NOT EXISTS exames (
                                      id SERIAL PRIMARY KEY,
                                      paciente_id VARCHAR(50),
    nome_paciente VARCHAR(120),
    tipo_exame VARCHAR(100),
    status_exame VARCHAR(40),
    valor_resultado NUMERIC(10,2),
    data_coleta TIMESTAMP,
    data_resultado TIMESTAMP,
    laboratorio VARCHAR(100),
    observacao TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Índices adicionais podem ser aplicados via 'create_indexes.sql'
