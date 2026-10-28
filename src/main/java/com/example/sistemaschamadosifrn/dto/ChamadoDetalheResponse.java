package com.example.sistemaschamadosifrn.dto;

import com.example.sistemaschamadosifrn.model.Chamado;

import java.util.List;

public record ChamadoDetalheResponse(ChamadoResponse chamado, List<InteracaoResponse> interacoes) {
    public static ChamadoDetalheResponse de(Chamado c) {
        return new ChamadoDetalheResponse(ChamadoResponse.de(c),
                c.getInteracoes().stream().map(InteracaoResponse::de).toList());
    }
}