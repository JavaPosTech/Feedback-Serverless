package br.com.fiap.feedback.service;

import br.com.fiap.feedback.config.AbstractTest;
import br.com.fiap.feedback.enums.Urgencia;
import br.com.fiap.feedback.model.dto.notificacao.AvaliacaoCriticaDTO;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

@QuarkusTest
@TestTransaction
class NotificacaoServiceTest extends AbstractTest {

    @Inject
    NotificacaoService notificacaoService;

    @Test
    void notificarUrgenciaTest() {
        var avaliacaoCriticaDTO = new AvaliacaoCriticaDTO(
                UUID.randomUUID(),
                "O vídeo da aula 3 não carrega",
                1,
                Urgencia.CRITICA,
                LocalDateTime.now());

        Assertions.assertDoesNotThrow(() -> notificacaoService.notificarUrgencia(avaliacaoCriticaDTO));
    }
}