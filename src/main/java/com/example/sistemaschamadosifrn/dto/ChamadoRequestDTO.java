package com.example.sistemaschamadosifrn.dto;

import com.example.sistemaschamadosifrn.model.enums.PrioridadeChamado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChamadoRequestDTO(
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank String descricao,
        @NotBlank @Size(max = 100) String local,
        PrioridadeChamado prioridade,
        @NotNull Long solicitanteId,
        @NotNull Long categoriaId) {
}