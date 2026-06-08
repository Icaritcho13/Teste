package com.seuprojeto.concorrencia.service;

import com.seuprojeto.concorrencia.exception.ContaNaoEncontradaException;
import com.seuprojeto.concorrencia.model.ContaBancariaVersionada;
import com.seuprojeto.concorrencia.repository.ContaBancariaVersionadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

/**
 * PARTE 2 — Regras de negócio COM controle de concorrência otimista (@Version).
 *
 * O uso de saveAndFlush() é proposital: ele força o UPDATE (com a checagem
 * de @Version) AINDA dentro da chamada do repositório. Isso garante que um
 * conflito seja traduzido para a exceção do Spring
 * (ObjectOptimisticLockingFailureException), capturada pelo ApiExceptionHandler
 * e respondida como 409 Conflict. Sem o flush explícito, a exceção só ocorreria
 * no commit (fora do repositório) e poderia escapar como erro 500 genérico.
 */
@Service
public class ContaBancariaVersionadaService {

    private final ContaBancariaVersionadaRepository repository;

    public ContaBancariaVersionadaService(ContaBancariaVersionadaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ContaBancariaVersionada depositar(Long id, BigDecimal valor) {
        validarValor(valor);
        ContaBancariaVersionada conta = buscar(id);
        conta.setSaldo(conta.getSaldo().add(valor));
        return repository.saveAndFlush(conta);
    }

    @Transactional
    public ContaBancariaVersionada sacar(Long id, BigDecimal valor) {
        validarValor(valor);
        ContaBancariaVersionada conta = buscar(id);
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar o saque.");
        }
        conta.setSaldo(conta.getSaldo().subtract(valor));
        return repository.saveAndFlush(conta);
    }

    private ContaBancariaVersionada buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo.");
        }
    }
}
