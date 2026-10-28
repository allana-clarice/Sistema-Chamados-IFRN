package com.example.sistemaschamadosifrn.dto;

import com.example.sistemaschamadosifrn.model.Setor;

public record SetorResponse(Long id, String nome, String sigla) {
    public static SetorResponse de(Setor s) {
        return new SetorResponse(s.getId(), s.getNome(), s.getSigla());
    }
}