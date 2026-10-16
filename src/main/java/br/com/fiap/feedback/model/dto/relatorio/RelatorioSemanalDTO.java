package br.com.fiap.feedback.model.dto.relatorio;

import br.com.fiap.feedback.model.dto.avaliacao.AvaliacaoResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioSemanalDTO(
        LocalDate inicio,
        LocalDate fim,
        Integer totalAvaliacoes,
        BigDecimal mediaNotas,
        List<AvaliacoesPorDiaDTO> avaliacoesPorDia,
        List<AvaliacoesPorUrgenciaDTO> avaliacoesPorUrgencia,
        List<AvaliacaoResponseDTO> avaliacoes

) {}