package com.seuprojeto.concorrencia.controller;

import com.seuprojeto.concorrencia.model.ContaBancaria;
import com.seuprojeto.concorrencia.service.ContaBancariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

/**
 * PARTE 1 — Endpoints da conta SEM controle de concorrência.
 * Tratamento de erro centralizado no ApiExceptionHandler (sem try/catch aqui).
 */
@RestController
@RequestMapping("/contas")
public class ContaBancariaController {

    private final ContaBancariaService service;

    public ContaBancariaController(ContaBancariaService service) {
        this.service = service;
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<ContaBancaria> deposito(
            @PathVariable Long id, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(service.depositar(id, valor));
    }

    @PostMapping("/{id}/saque")
    public ResponseEntity<ContaBancaria> saque(
            @PathVariable Long id, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(service.sacar(id, valor));
    }
}
