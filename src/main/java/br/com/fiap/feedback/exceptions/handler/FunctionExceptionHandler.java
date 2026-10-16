package br.com.fiap.feedback.exceptions.handler;

import br.com.fiap.feedback.exceptions.NotaInvalidaException;
import br.com.fiap.feedback.exceptions.dto.CampoInvalidoResponseDTO;
import br.com.fiap.feedback.exceptions.dto.ErrorResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

import java.io.UncheckedIOException;

@ApplicationScoped
@RequiredArgsConstructor
public class FunctionExceptionHandler {

    private final ObjectMapper objectMapper;

    public HttpResponseMessage handle(Exception ex, HttpRequestMessage<?> pHttpRequestMessage) {
        return switch (ex) {
            case ConstraintViolationException e -> handleConstraintViolationException(e, pHttpRequestMessage);
            case JsonProcessingException e -> handleJsonProcessingException(e, pHttpRequestMessage);
            case NotaInvalidaException e -> handleNotaInvalidaException(e, pHttpRequestMessage);
            default -> handleException(ex, pHttpRequestMessage);
        };
    }

    private HttpResponseMessage handleConstraintViolationException(ConstraintViolationException ex, HttpRequestMessage<?> pHttpRequestMessage) {

        var errors = ex.getConstraintViolations()
                .stream()
                .map(violacao -> new CampoInvalidoResponseDTO(campo(violacao), violacao.getMessage()))
                .toList();

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida!",
                pHttpRequestMessage.getUri().getPath(),
                "/Feedback-Serverless/problems/requisicao-invalida",
                "Um ou mais campos estão inválidos!",
                errors);

        return responder(pHttpRequestMessage, HttpStatus.BAD_REQUEST, response);
    }

    private HttpResponseMessage handleJsonProcessingException(JsonProcessingException ex, HttpRequestMessage<?> pHttpRequestMessage) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Corpo da Requisição Inválido!",
                pHttpRequestMessage.getUri().getPath(),
                "/Feedback-Serverless/problems/corpo-requisicao-invalido",
                "Envie um JSON com os campos descricao (texto) e nota (número de 0 a 10)!");

        return responder(pHttpRequestMessage, HttpStatus.BAD_REQUEST, response);
    }

    private HttpResponseMessage handleNotaInvalidaException(NotaInvalidaException ex, HttpRequestMessage<?> pHttpRequestMessage) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Nota Inválida!",
                pHttpRequestMessage.getUri().getPath(),
                "/Feedback-Serverless/problems/nota-invalida",
                ex.getMessage());

        return responder(pHttpRequestMessage, HttpStatus.BAD_REQUEST, response);
    }

    private HttpResponseMessage handleException(Exception ex, HttpRequestMessage<?> pHttpRequestMessage) {

        Log.error("Erro inesperado ao processar a requisição!", ex);

        var response = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno!",
                pHttpRequestMessage.getUri().getPath(),
                "/Feedback-Serverless/problems/erro-interno",
                "Ocorreu um erro inesperado ao processar a requisição!");

        return responder(pHttpRequestMessage, HttpStatus.INTERNAL_SERVER_ERROR, response);
    }

    private String campo(ConstraintViolation<?> violacao) {
        String campo = null;

        for (var no : violacao.getPropertyPath()) {
            campo = no.getName();
        }

        return campo;
    }

    private HttpResponseMessage responder(HttpRequestMessage<?> pHttpRequestMessage, HttpStatus status, ErrorResponseDTO response) {
        try {
            return pHttpRequestMessage.createResponseBuilder(status)
                    .header("Content-Type", "application/problem+json")
                    .body(objectMapper.writeValueAsString(response))
                    .build();
        } catch (JsonProcessingException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}