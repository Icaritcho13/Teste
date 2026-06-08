package com.seuprojeto.concorrencia.repository;

import com.seuprojeto.concorrencia.model.ContaBancariaVersionada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaBancariaVersionadaRepository
        extends JpaRepository<ContaBancariaVersionada, Long> {
}
