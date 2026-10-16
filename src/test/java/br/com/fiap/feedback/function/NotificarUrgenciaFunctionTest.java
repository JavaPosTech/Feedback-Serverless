package br.com.fiap.feedback.function;

import br.com.fiap.feedback.config.AbstractTest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@QuarkusTest
@TestTransaction
class NotificarUrgenciaFunctionTest extends AbstractTest {

    private String notificarUrgenciaRequest;

    @Inject
    NotificarUrgenciaFunction notificarUrgenciaFunction;

    @BeforeEach
    void setUp() throws IOException {
        if (notificarUrgenciaRequest == null) {
            notificarUrgenciaRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/notificacao/notificarUrgenciaRequest.json")));
        }
    }

    @Test
    void notificarUrgenciaTest() {
        Assertions.assertDoesNotThrow(() -> notificarUrgenciaFunction.notificarUrgencia(notificarUrgenciaRequest));
    }
}