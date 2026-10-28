package com.example.sistemaschamadosifrn.dto;

import jakarta.validation.constraints.NotNull;

public record CancelarChamadoDTO(@NotNull Long solicitanteId) {
}