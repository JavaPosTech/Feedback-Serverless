package br.com.fiap.feedback.service;

import br.com.fiap.feedback.client.EmailClient;
import br.com.fiap.feedback.model.dto.notificacao.AvaliacaoCriticaDTO;
import br.com.fiap.feedback.template.EmailTemplates;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class NotificacaoService {

    private final EmailClient emailClient;

    public void notificarUrgencia(AvaliacaoCriticaDTO avaliacaoCriticaDTO) {
        var assunto = "Avaliação com urgência " + avaliacaoCriticaDTO.urgencia().getDescricao().toLowerCase() + " recebida";
        var conteudo = EmailTemplates.urgencia(avaliacaoCriticaDTO).render();

        emailClient.enviar(assunto, conteudo);
    }
}