package com.seuprojeto.concorrencia.exception;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(Long id) {
        super("Conta não encontrada para o id: " + id);
    }
}
