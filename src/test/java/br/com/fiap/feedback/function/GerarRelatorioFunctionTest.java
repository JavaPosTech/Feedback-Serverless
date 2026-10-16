package br.com.fiap.feedback.function;

import br.com.fiap.feedback.config.AbstractTest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
class GerarRelatorioFunctionTest extends AbstractTest {

    @Inject
    GerarRelatorioFunction gerarRelatorioFunction;

    @Test
    void gerarRelatorioTest() {
        Assertions.assertDoesNotThrow(() -> gerarRelatorioFunction.gerarRelatorio("{}"));
    }
}