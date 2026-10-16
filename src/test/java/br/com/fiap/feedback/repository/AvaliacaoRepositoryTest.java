package br.com.fiap.feedback.repository;

import br.com.fiap.feedback.config.AbstractTest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@QuarkusTest
@TestTransaction
class AvaliacaoRepositoryTest extends AbstractTest {

    @Inject
    AvaliacaoRepository avaliacaoRepository;

    @Test
    void listarPorPeriodoTest() {
        Assertions.assertDoesNotThrow(() -> avaliacaoRepository.listarPorPeriodo(LocalDate.now().minusDays(7).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()));
    }

    @Test
    void calcularMediaNotasTest() {
        Assertions.assertDoesNotThrow(() -> avaliacaoRepository.calcularMediaNotas(LocalDate.now().minusDays(7).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()));
    }

    @Test
    void contarPorDiaTest() {
        Assertions.assertDoesNotThrow(() -> avaliacaoRepository.contarPorDia(LocalDate.now().minusDays(7).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()));
    }

    @Test
    void contarPorUrgenciaTest() {
        Assertions.assertDoesNotThrow(() -> avaliacaoRepository.contarPorUrgencia(LocalDate.now().minusDays(7).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()));
    }
}