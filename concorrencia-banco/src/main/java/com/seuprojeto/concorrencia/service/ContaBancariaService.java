package com.seuprojeto.concorrencia.service;

import com.seuprojeto.concorrencia.exception.ContaNaoEncontradaException;
import com.seuprojeto.concorrencia.model.ContaBancaria;
import com.seuprojeto.concorrencia.repository.ContaBancariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

/**
 * PARTE 1 — Regras de negócio SEM tratamento de concorrência.
 *
 * IMPORTANTE: este service é VULNERÁVEL de propósito. O padrão
 * read-modify-write (ler -> modificar -> gravar) dentro de uma transação
 * curta permite que duas threads leiam o mesmo saldo e uma sobrescreva a
 * outra (Lost Update). Não há @Version nem lock — apenas @Transactional.
 */
@Service
public class ContaBancariaService {

    private final ContaBancariaRepository repository;

    public ContaBancariaService(ContaBancariaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ContaBancaria depositar(Long id, BigDecimal valor) {
        validarValor(valor);
        ContaBancaria conta = buscar(id);          // 1. LÊ
        conta.setSaldo(conta.getSaldo().add(valor)); // 2. MODIFICA
        return repository.save(conta);              // 3. GRAVA (janela de risco entre LÊ e GRAVA)
    }

    @Transactional
    public ContaBancaria sacar(Long id, BigDecimal valor) {
        validarValor(valor);
        ContaBancaria conta = buscar(id);
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar o saque.");
        }
        conta.setSaldo(conta.getSaldo().subtract(valor));
        return repository.save(conta);
    }

    private ContaBancaria buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo.");
        }
    }
}
