-- Cria a tabela (se ainda não existir)
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Importa os dados a partir do CSV (ajuste o caminho se estiver fora do container)
        COPY exames(
    paciente_id,
    nome_paciente,
    tipo_exame,
    status_exame,
    valor_resultado,
    data_coleta,
    data_resultado,
    laboratorio,
    created_at
)
FROM '/var/lib/postgresql/data/exames.csv'
DELIMITER ','
CSV HEADER;
