CREATE TABLE avaliacao (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_avaliacao PRIMARY KEY,
    descricao NVARCHAR(500) NOT NULL,
    nota TINYINT NOT NULL CONSTRAINT ck_avaliacao_nota CHECK (nota BETWEEN 0 AND 10),
    urgencia VARCHAR(10) NOT NULL CONSTRAINT ck_avaliacao_urgencia CHECK (urgencia IN ('CRITICA', 'MEDIA', 'BAIXA')),
    data_envio DATETIME2 NOT NULL
);

CREATE INDEX ix_avaliacao_data_envio ON avaliacao (data_envio);