package com.seuprojeto.concorrencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.math.BigDecimal;

/**
 * PARTE 2 — Entidade COM controle de concorrência otimista.
 * O campo anotado com @Version faz o Hibernate adicionar
 * "... WHERE id = ? AND version = ?" em todo UPDATE e incrementar a versão.
 * Se a versão lida não bater com a do banco no momento da escrita,
 * é lançada a exceção de lock otimista.
 */
@Entity
public class ContaBancariaVersionada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2)
    private BigDecimal saldo;

    @Version
    private Integer version;

    public ContaBancariaVersionada() {
    }

    public ContaBancariaVersionada(BigDecimal saldo) {
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

    // Exposto de propósito: assim a resposta JSON mostra a versão
    // incrementando a cada operação — útil para evidenciar no relatório.
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
