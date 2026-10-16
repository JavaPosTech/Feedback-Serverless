package br.com.fiap.feedback.exceptions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(
        Integer status,
        String title,
        String instance,
        URI type,
        String detail,
        List<?> errors,
        LocalDateTime timestamp

) {

    public ErrorResponseDTO(Integer status, String title, String instance, String type, String detail) {
        this(status, title, instance, URI.create(type), detail, null, LocalDateTime.now());
    }

    public ErrorResponseDTO(Integer status, String title, String instance, String type, String detail, List<?> errors) {
        this(status, title, instance, URI.create(type), detail, errors, LocalDateTime.now());
    }
}