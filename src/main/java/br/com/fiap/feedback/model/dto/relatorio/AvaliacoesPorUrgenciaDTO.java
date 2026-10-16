package br.com.fiap.feedback.model.dto.relatorio;

import br.com.fiap.feedback.enums.Urgencia;

public record AvaliacoesPorUrgenciaDTO(Urgencia urgencia, Long quantidade) {}