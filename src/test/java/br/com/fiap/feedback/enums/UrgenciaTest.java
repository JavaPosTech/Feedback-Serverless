package br.com.fiap.feedback.enums;

import br.com.fiap.feedback.config.AbstractTest;
import br.com.fiap.feedback.exceptions.NotaInvalidaException;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
class UrgenciaTest extends AbstractTest {

    @Test
    void deNotaTest() {
        Assertions.assertEquals(Urgencia.CRITICA, Urgencia.deNota(0));
        Assertions.assertEquals(Urgencia.CRITICA, Urgencia.deNota(3));
        Assertions.assertEquals(Urgencia.MEDIA, Urgencia.deNota(4));
        Assertions.assertEquals(Urgencia.MEDIA, Urgencia.deNota(6));
        Assertions.assertEquals(Urgencia.BAIXA, Urgencia.deNota(7));
        Assertions.assertEquals(Urgencia.BAIXA, Urgencia.deNota(10));
        Assertions.assertThrows(NotaInvalidaException.class, () -> Urgencia.deNota(-1));
        Assertions.assertThrows(NotaInvalidaException.class, () -> Urgencia.deNota(11));
    }
}