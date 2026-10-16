package br.com.fiap.feedback.function;

import br.com.fiap.feedback.config.AbstractFunctionTest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@QuarkusTest
@TestTransaction
class ReceberAvaliacaoFunctionTest extends AbstractFunctionTest {

    private String salvarAvaliacaoRequest;

    @Inject
    ReceberAvaliacaoFunction receberAvaliacaoFunction;

    @BeforeEach
    void setUp() throws IOException {
        if (salvarAvaliacaoRequest == null) {
            salvarAvaliacaoRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/avaliacao/salvarAvaliacaoRequest.json")));
        }
    }

    @Test
    void salvarAvaliacaoTest() {
        testPost(receberAvaliacaoFunction::salvarAvaliacao, "/avaliacao", salvarAvaliacaoRequest);
    }
}