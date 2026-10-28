package com.example.sistemaschamadosifrn.controller;

import com.example.sistemaschamadosifrn.dto.PaginaResponse;
import com.example.sistemaschamadosifrn.dto.UsuarioRequest;
import com.example.sistemaschamadosifrn.dto.UsuarioResponse;
import com.example.sistemaschamadosifrn.model.enums.TipoUsuario;
import com.example.sistemaschamadosifrn.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.de(usuarioService.criar(dto)));
    }

    @GetMapping
    public PaginaResponse<UsuarioResponse> listar(@RequestParam(required = false) String nome,
                                                  @RequestParam(required = false) TipoUsuario tipo,
                                                  @RequestParam(required = false) Long setorId,
                                                  Pageable pageable) {
        return PaginaResponse.de(usuarioService.listar(nome, tipo, setorId, pageable), UsuarioResponse::de);
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return UsuarioResponse.de(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest dto) {
        return UsuarioResponse.de(usuarioService.atualizar(id, dto));
    }
}