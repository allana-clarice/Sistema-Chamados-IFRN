package com.example.sistemaschamadosifrn.dto;

import com.example.sistemaschamadosifrn.model.Chamado;
import com.example.sistemaschamadosifrn.model.enums.PrioridadeChamado;
import com.example.sistemaschamadosifrn.model.enums.StatusChamado;

import java.time.LocalDateTime;

public record ChamadoResponse(Long id, String titulo, String descricao, String local,
                              PrioridadeChamado prioridade, StatusChamado status,
                              LocalDateTime dataAbertura, LocalDateTime dataFechamento, String solucao,
                              Long solicitanteId, String solicitanteNome,
                              Long categoriaId, String categoriaNome, Long setorId,
                              Long tecnicoId, String tecnicoNome) {

    public static ChamadoResponse de(Chamado c) {
        return new ChamadoResponse(c.getId(), c.getTitulo(), c.getDescricao(), c.getLocal(),
                c.getPrioridade(), c.getStatus(), c.getDataAbertura(), c.getDataFechamento(), c.getSolucao(),
                c.getSolicitante().getId(), c.getSolicitante().getNome(),
                c.getCategoria().getId(), c.getCategoria().getNome(), c.getCategoria().getSetor().getId(),
                c.getTecnico() != null ? c.getTecnico().getId() : null,
                c.getTecnico() != null ? c.getTecnico().getNome() : null);
    }
}