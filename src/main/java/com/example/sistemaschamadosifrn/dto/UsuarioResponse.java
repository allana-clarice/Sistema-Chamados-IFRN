package com.example.sistemaschamadosifrn.dto;

import com.example.sistemaschamadosifrn.model.Usuario;
import com.example.sistemaschamadosifrn.model.enums.TipoUsuario;

public record UsuarioResponse(Long id, String nome, String email, String matricula,
                              TipoUsuario tipo, Long setorId, String setorSigla) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getMatricula(), u.getTipo(),
                u.getSetor() != null ? u.getSetor().getId() : null,
                u.getSetor() != null ? u.getSetor().getSigla() : null);
    }
}