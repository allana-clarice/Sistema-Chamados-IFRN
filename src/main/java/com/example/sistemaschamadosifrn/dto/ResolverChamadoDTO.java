package com.example.sistemaschamadosifrn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResolverChamadoDTO(@NotNull Long tecnicoId, @NotBlank String solucao) {
}