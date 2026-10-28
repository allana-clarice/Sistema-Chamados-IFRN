package com.example.sistemaschamadosifrn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank @Size(max = 100) String nome,
        String descricao,
        @NotNull Long setorId) {
}