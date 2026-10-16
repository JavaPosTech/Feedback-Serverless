package br.com.fiap.feedback.service;

import br.com.fiap.feedback.client.EmailClient;
import br.com.fiap.feedback.config.FeedbackConfig;
import br.com.fiap.feedback.model.dto.relatorio.AvaliacoesPorUrgenciaDTO;
import br.com.fiap.feedback.model.dto.relatorio.RelatorioSemanalDTO;
import br.com.fiap.feedback.model.mapper.AvaliacaoMapper;
import br.com.fiap.feedback.repository.AvaliacaoRepository;
import br.com.fiap.feedback.template.EmailTemplates;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@ApplicationScoped
@RequiredArgsConstructor
public class RelatorioService {

    private static final DateTimeFormatter DIA_MES = DateTimeFormatter.ofPattern("dd/MM");

    private final EmailClient emailClient;
    private final FeedbackConfig feedbackConfig;
    private final AvaliacaoMapper avaliacaoMapper;
    private final AvaliacaoRepository avaliacaoRepository;

    public RelatorioSemanalDTO gerarRelatorioSemanal() {
        var hoje = LocalDate.now(feedbackConfig.fusoHorario());
        var relatorio = QuarkusTransaction.joiningExisting().call(() -> montar(hoje.minusDays(7), hoje));
        var assunto = "Relatório semanal de avaliações - " + relatorio.inicio().format(DIA_MES) + " a " + relatorio.fim().format(DIA_MES);

        emailClient.enviar(assunto, EmailTemplates.relatorioSemanal(relatorio).render());

        return relatorio;
    }

    private RelatorioSemanalDTO montar(LocalDate inicio, LocalDate fimExclusivo) {
        var de = inicio.atStartOfDay();
        var ate = fimExclusivo.atStartOfDay();
        var avaliacoes = avaliacaoRepository.listarPorPeriodo(de, ate);
        var media = avaliacaoRepository.calcularMediaNotas(de, ate);

        return new RelatorioSemanalDTO(
                inicio,
                fimExclusivo.minusDays(1),
                avaliacoes.size(),
                media == null ? null : BigDecimal.valueOf(media).setScale(1, RoundingMode.HALF_UP),
                avaliacaoRepository.contarPorDia(de, ate),
                avaliacaoRepository.contarPorUrgencia(de, ate)
                        .stream()
                        .sorted(Comparator.comparing(AvaliacoesPorUrgenciaDTO::urgencia))
                        .toList(),
                avaliacaoMapper.toAvaliacaoResponseDTOList(avaliacoes));
    }
}