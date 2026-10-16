INSERT INTO avaliacao (id, descricao, nota, urgencia, data_envio) VALUES
    (NEWID(), N'Aula excelente, muito didática', 9, 'BAIXA', DATEADD(DAY, -1, SYSDATETIME())),
    (NEWID(), N'Exemplos práticos ajudaram bastante', 8, 'BAIXA', DATEADD(DAY, -2, SYSDATETIME())),
    (NEWID(), N'Conteúdo bom, mas o áudio falhou em alguns trechos', 5, 'MEDIA', DATEADD(DAY, -2, SYSDATETIME())),
    (NEWID(), N'O vídeo travou do início ao fim', 2, 'CRITICA', DATEADD(DAY, -3, SYSDATETIME())),
    (NEWID(), N'Explicação confusa sobre filas', 4, 'MEDIA', DATEADD(DAY, -5, SYSDATETIME()));