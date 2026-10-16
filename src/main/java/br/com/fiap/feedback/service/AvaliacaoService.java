package br.com.fiap.feedback.service;

import br.com.fiap.feedback.client.FilaClient;
import br.com.fiap.feedback.config.FeedbackConfig;
import br.com.fiap.feedback.enums.Urgencia;
import br.com.fiap.feedback.model.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.fiap.feedback.model.dto.avaliacao.SalvarAvaliacaoDTO;
import br.com.fiap.feedback.model.entity.Avaliacao;
import br.com.fiap.feedback.model.mapper.AvaliacaoMapper;
import br.com.fiap.feedback.repository.AvaliacaoRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@ApplicationScoped
@RequiredArgsConstructor
public class AvaliacaoService {

    private final FilaClient filaClient;
    private final FeedbackConfig feedbackConfig;
    private final AvaliacaoMapper avaliacaoMapper;
    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoResponseDTO salvar(@Valid SalvarAvaliacaoDTO salvarAvaliacaoDTO) {
        var avaliacao = QuarkusTransaction.joiningExisting().call(() -> gravar(salvarAvaliacaoDTO));

        if (avaliacao.getUrgencia() == Urgencia.CRITICA) {
            filaClient.publicar(avaliacaoMapper.toAvaliacaoCriticaDTO(avaliacao));
        }

        return avaliacaoMapper.toAvaliacaoResponseDTO(avaliacao);
    }

    private Avaliacao gravar(SalvarAvaliacaoDTO salvarAvaliacaoDTO) {
        var avaliacao = avaliacaoMapper.toAvaliacao(salvarAvaliacaoDTO);

        avaliacao.setUrgencia(Urgencia.deNota(avaliacao.getNota()));
        avaliacao.setDataEnvio(LocalDateTime.now(feedbackConfig.fusoHorario()));

        avaliacaoRepository.persist(avaliacao);

        return avaliacao;
    }
}