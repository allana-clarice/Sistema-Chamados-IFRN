package com.example.sistemaschamadosifrn.dto;

import com.example.sistemaschamadosifrn.model.Interacao;

import java.time.LocalDateTime;

public record InteracaoResponse(Long id, String texto, LocalDateTime dataHora, Long autorId, String autorNome) {
    public static InteracaoResponse de(Interacao i) {
        return new InteracaoResponse(i.getId(), i.getTexto(), i.getDataHora(),
                i.getAutor().getId(), i.getAutor().getNome());
    }
}