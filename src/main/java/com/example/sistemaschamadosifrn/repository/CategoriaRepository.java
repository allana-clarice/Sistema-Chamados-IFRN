package com.example.sistemaschamadosifrn.repository;

import com.example.sistemaschamadosifrn.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {
    boolean existsBySetorId(Long setorId);
}