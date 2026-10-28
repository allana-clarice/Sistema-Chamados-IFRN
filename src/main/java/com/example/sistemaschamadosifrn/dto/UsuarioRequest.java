package com.example.sistemaschamadosifrn.dto;

import com.example.sistemaschamadosifrn.model.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(max = 50) String matricula,
        @NotNull TipoUsuario tipo,
        Long setorId) {
}