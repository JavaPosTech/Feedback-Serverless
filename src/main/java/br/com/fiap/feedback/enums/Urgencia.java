package br.com.fiap.feedback.enums;

import br.com.fiap.feedback.exceptions.NotaInvalidaException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Urgencia {

    CRITICA("Crítica"),
    MEDIA("Média"),
    BAIXA("Baixa");

    private final String descricao;

    public static Urgencia deNota(int nota) {
        if (nota < 0 || nota > 10) {
            throw new NotaInvalidaException("A nota deve estar entre 0 e 10!");
        }

        if (nota <= 3) {
            return CRITICA;
        }

        return nota <= 6 ? MEDIA : BAIXA;
    }
}