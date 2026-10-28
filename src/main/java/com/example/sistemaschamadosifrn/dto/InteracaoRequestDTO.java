package com.example.sistemaschamadosifrn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InteracaoRequestDTO(@NotNull Long autorId, @NotBlank String texto) {
}