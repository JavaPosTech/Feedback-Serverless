package br.com.fiap.feedback.config;

import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.QueueMessageEncoding;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@ApplicationScoped
public class AzureConfig {

    @Produces
    @Singleton
    public QueueClient queueClient(FeedbackConfig feedbackConfig) {
        var fila = feedbackConfig.fila();
        var builder = new QueueClientBuilder()
                .queueName(fila.nome())
                .messageEncoding(QueueMessageEncoding.BASE64);

        fila.connectionString().ifPresentOrElse(
                builder::connectionString,
                () -> builder
                        .endpoint(fila.endpoint().orElseThrow(() -> new IllegalStateException("Configure FILA_ENDPOINT ou a connection string da fila!")))
                        .credential(new DefaultAzureCredentialBuilder().build()));

        var queueClient = builder.buildClient();
        queueClient.createIfNotExists();

        return queueClient;
    }

    @Produces
    @Singleton
    public EmailAsyncClient emailAsyncClient(FeedbackConfig feedbackConfig) {
        var connectionString = feedbackConfig.email()
                .connectionString()
                .orElseThrow(() -> new IllegalStateException("Configure EMAIL_CONNECTION_STRING para enviar e-mails!"));

        return new EmailClientBuilder()
                .connectionString(connectionString)
                .buildAsyncClient();
    }
}