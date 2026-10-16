package br.com.fiap.feedback.config;

import io.smallrye.config.ConfigMapping;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@ConfigMapping(prefix = "feedback")
public interface FeedbackConfig {

    ZoneId fusoHorario();

    Fila fila();

    Email email();

    interface Fila {

        String nome();

        Optional<String> endpoint();

        Optional<String> connectionString();
    }

    interface Email {

        boolean simulado();

        String remetente();

        List<String> destinatarios();

        Optional<String> connectionString();
    }
}