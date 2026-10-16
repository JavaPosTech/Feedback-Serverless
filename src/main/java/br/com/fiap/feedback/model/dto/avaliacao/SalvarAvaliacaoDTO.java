package br.com.fiap.feedback.model.dto.avaliacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SalvarAvaliacaoDTO(

        @NotNull(message = "A nota é obrigatória!")
        @Min(value = 0, message = "A nota deve estar entre 0 e 10!")
        @Max(value = 10, message = "A nota deve estar entre 0 e 10!")
        Integer nota,

        @NotBlank(message = "A descrição é obrigatória!")
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres!")
        String descricao

) {}