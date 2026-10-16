package br.com.fiap.feedback.exceptions;

public class EmailNaoEnviadoException extends RuntimeException {

    public EmailNaoEnviadoException(String mensagem) {
        super(mensagem);
    }
}