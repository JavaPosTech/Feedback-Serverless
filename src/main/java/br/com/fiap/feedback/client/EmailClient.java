package br.com.fiap.feedback.client;

import br.com.fiap.feedback.config.FeedbackConfig;
import br.com.fiap.feedback.exceptions.EmailNaoEnviadoException;
import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.core.util.polling.LongRunningOperationStatus;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class EmailClient {

    private final FeedbackConfig feedbackConfig;
    private final Instance<EmailAsyncClient> emailAsyncClient;

    public void enviar(String assunto, String conteudoHtml) {
        var email = feedbackConfig.email();

        if (email.simulado()) {
            Log.infof("E-mail simulado para %s com o assunto \"%s\"", email.destinatarios(), assunto);
            return;
        }

        var mensagem = new EmailMessage()
                .setSenderAddress(email.remetente())
                .setToRecipients(email.destinatarios().stream().map(EmailAddress::new).toList())
                .setSubject(assunto)
                .setBodyHtml(conteudoHtml);

        var resultado = emailAsyncClient.get()
                .beginSend(mensagem)
                .getSyncPoller()
                .waitForCompletion();

        if (resultado.getStatus() != LongRunningOperationStatus.SUCCESSFULLY_COMPLETED) {
            throw new EmailNaoEnviadoException("Não foi possível enviar o e-mail \"" + assunto + "\"!");
        }
    }
}