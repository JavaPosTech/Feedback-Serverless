package br.com.fiap.feedback.exceptions;

public class NotaInvalidaException extends RuntimeException {

    public NotaInvalidaException(String mensagem) {
        super(mensagem);
    }
}