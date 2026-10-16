package br.com.fiap.feedback.config;

import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.HttpStatusType;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

public record HttpRequestMessageFake(URI uri, Optional<String> body) implements HttpRequestMessage<Optional<String>> {

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public Map<String, String> getHeaders() {
        return Map.of();
    }

    @Override
    public Map<String, String> getQueryParameters() {
        return Map.of();
    }

    @Override
    public Optional<String> getBody() {
        return body;
    }

    @Override
    public HttpResponseMessage.Builder createResponseBuilder(HttpStatus status) {
        return new HttpResponseMessageFake.Builder().status(status);
    }

    @Override
    public HttpResponseMessage.Builder createResponseBuilder(HttpStatusType status) {
        return new HttpResponseMessageFake.Builder().status(status);
    }
}