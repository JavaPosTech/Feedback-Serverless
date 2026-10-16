package br.com.fiap.feedback.client;

import br.com.fiap.feedback.model.dto.notificacao.AvaliacaoCriticaDTO;
import com.azure.storage.queue.QueueClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.io.UncheckedIOException;

@ApplicationScoped
@RequiredArgsConstructor
public class FilaClient {

    private final QueueClient queueClient;
    private final ObjectMapper objectMapper;

    public void publicar(AvaliacaoCriticaDTO avaliacaoCriticaDTO) {
        try {
            queueClient.sendMessage(objectMapper.writeValueAsString(avaliacaoCriticaDTO));
        } catch (JsonProcessingException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}