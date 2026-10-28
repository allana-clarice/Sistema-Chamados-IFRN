package com.example.sistemaschamadosifrn.dto;

import com.example.sistemaschamadosifrn.model.Categoria;

public record CategoriaResponse(Long id, String nome, String descricao, Long setorId, String setorSigla) {
    public static CategoriaResponse de(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao(),
                c.getSetor().getId(), c.getSetor().getSigla());
    }
}