package com.seuprojeto.concorrencia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tratamento de erro centralizado para TODOS os controllers (Parte 1 e Parte 2).
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ResponseEntity<String> tratarNaoEncontrada(ContaNaoEncontradaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarRequisicaoInvalida(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**
     * PARTE 2 — núcleo da solução.
     * Lançada quando duas transações concorrentes tentam atualizar o mesmo
     * registro e a versão (@Version) não bate. Responde 409 Conflict.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> tratarConflito(ObjectOptimisticLockingFailureException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Conflito de concorrência: outra operação alterou esta conta. Tente novamente.");
    }
}
