package br.com.fiap.feedback.service;

import br.com.fiap.feedback.config.AbstractTest;
import br.com.fiap.feedback.model.dto.avaliacao.SalvarAvaliacaoDTO;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
class AvaliacaoServiceTest extends AbstractTest {

    @Inject
    AvaliacaoService avaliacaoService;

    @Test
    void salvarTest() {
        var salvarAvaliacaoDTO = new SalvarAvaliacaoDTO(
                2,
                "A aula de filas ficou sem áudio na metade");

        var avaliacao = Assertions.assertDoesNotThrow(() -> avaliacaoService.salvar(salvarAvaliacaoDTO));
        Assertions.assertNotNull(avaliacao);
    }
}