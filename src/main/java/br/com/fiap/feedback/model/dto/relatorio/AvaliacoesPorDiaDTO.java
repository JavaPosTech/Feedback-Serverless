package br.com.fiap.feedback.model.dto.relatorio;

import java.time.LocalDate;

public record AvaliacoesPorDiaDTO(LocalDate dia, Long quantidade) {}