package com.seuprojeto.concorrencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

/**
 * PARTE 1 — Entidade SEM controle de concorrência.
 * Usada para evidenciar o problema da Atualização Perdida (Lost Update).
 */
@Entity
public class ContaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Regra do roteiro: atributos monetários em BigDecimal.
    // precision/scale explícitos: boa prática para valores monetários.
    @Column(precision = 19, scale = 2)
    private BigDecimal saldo;

    public ContaBancaria() {
    }

    public ContaBancaria(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
