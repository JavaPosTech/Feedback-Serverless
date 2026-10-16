package br.com.fiap.feedback.function;

import br.com.fiap.feedback.model.dto.notificacao.AvaliacaoCriticaDTO;
import br.com.fiap.feedback.service.NotificacaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class NotificarUrgenciaFunction {

    private final ObjectMapper objectMapper;
    private final NotificacaoService notificacaoService;

    @FunctionName("NotificarUrgencia")
    public void notificarUrgencia(
            @QueueTrigger(
                    name = "mensagem",
                    queueName = "avaliacoes-criticas",
                    connection = "AzureWebJobsStorage")
            String mensagem) throws JsonProcessingException {
        notificacaoService.notificarUrgencia(objectMapper.readValue(mensagem, AvaliacaoCriticaDTO.class));
    }
}