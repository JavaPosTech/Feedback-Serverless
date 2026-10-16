package br.com.fiap.feedback.config;

import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatusType;

import java.util.HashMap;
import java.util.Map;

public record HttpResponseMessageFake(HttpStatusType status, Map<String, String> headers, Object body) implements HttpResponseMessage {

    @Override
    public HttpStatusType getStatus() {
        return status;
    }

    @Override
    public String getHeader(String nome) {
        return headers.get(nome);
    }

    @Override
    public Object getBody() {
        return body;
    }

    public static class Builder implements HttpResponseMessage.Builder {

        private Object body;
        private HttpStatusType status;
        private final Map<String, String> headers = new HashMap<>();

        @Override
        public HttpResponseMessage.Builder status(HttpStatusType status) {
            this.status = status;
            return this;
        }

        @Override
        public HttpResponseMessage.Builder header(String nome, String valor) {
            headers.put(nome, valor);
            return this;
        }

        @Override
        public HttpResponseMessage.Builder body(Object body) {
            this.body = body;
            return this;
        }

        @Override
        public HttpResponseMessage build() {
            return new HttpResponseMessageFake(status, Map.copyOf(headers), body);
        }
    }
}