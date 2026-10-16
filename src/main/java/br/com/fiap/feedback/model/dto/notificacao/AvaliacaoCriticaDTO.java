package br.com.fiap.feedback.model.dto.notificacao;

import br.com.fiap.feedback.enums.Urgencia;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvaliacaoCriticaDTO(
        UUID id,
        String descricao,
        Integer nota,
        Urgencia urgencia,
        LocalDateTime dataEnvio

) {}