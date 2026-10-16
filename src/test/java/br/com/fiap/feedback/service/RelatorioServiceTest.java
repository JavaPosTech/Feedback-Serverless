package br.com.fiap.feedback.service;

import br.com.fiap.feedback.config.AbstractTest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
class RelatorioServiceTest extends AbstractTest {

    @Inject
    RelatorioService relatorioService;

    @Test
    void gerarRelatorioSemanalTest() {
        var relatorio = Assertions.assertDoesNotThrow(() -> relatorioService.gerarRelatorioSemanal());
        Assertions.assertNotNull(relatorio);
    }
}