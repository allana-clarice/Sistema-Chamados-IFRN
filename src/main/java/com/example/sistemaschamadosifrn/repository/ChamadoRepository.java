package com.example.sistemaschamadosifrn.repository;

import com.example.sistemaschamadosifrn.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChamadoRepository extends JpaRepository<Chamado, Long>, JpaSpecificationExecutor<Chamado> {
    boolean existsByCategoriaId(Long categoriaId);
    boolean existsByCategoriaSetorId(Long setorId);
}