package br.com.fiap.feedback.model.dto.avaliacao;

import br.com.fiap.feedback.enums.Urgencia;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvaliacaoResponseDTO(
        UUID id,
        Integer nota,
        String descricao,
        Urgencia urgencia,
        LocalDateTime dataEnvio

) {}