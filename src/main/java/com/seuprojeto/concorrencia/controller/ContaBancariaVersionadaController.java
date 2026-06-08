package com.seuprojeto.concorrencia.controller;

import com.seuprojeto.concorrencia.model.ContaBancariaVersionada;
import com.seuprojeto.concorrencia.service.ContaBancariaVersionadaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

/**
 * PARTE 2 — Endpoints da conta versionada.
 * Base path diferente da Parte 1 para que as duas abordagens coexistam
 * no mesmo projeto sem colisão de rota.
 */
@RestController
@RequestMapping("/contas-versionadas")
public class ContaBancariaVersionadaController {

    private final ContaBancariaVersionadaService service;

    public ContaBancariaVersionadaController(ContaBancariaVersionadaService service) {
        this.service = service;
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<ContaBancariaVersionada> deposito(
            @PathVariable Long id, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(service.depositar(id, valor));
    }

    @PostMapping("/{id}/saque")
    public ResponseEntity<ContaBancariaVersionada> saque(
            @PathVariable Long id, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(service.sacar(id, valor));
    }
}
