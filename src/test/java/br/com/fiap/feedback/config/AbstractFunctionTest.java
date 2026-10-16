package br.com.fiap.feedback.config;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractFunctionTest extends AbstractTest {

    protected void testPost(Function<HttpRequestMessage<Optional<String>>, HttpResponseMessage> function, String url, String requestBody) {
        var requisicao = new HttpRequestMessageFake(URI.create("http://localhost" + url), Optional.of(requestBody));
        var resposta = function.apply(requisicao);

        Assertions.assertEquals(HttpStatus.CREATED.value(), resposta.getStatusCode(), String.valueOf(resposta.getBody()));
    }
}