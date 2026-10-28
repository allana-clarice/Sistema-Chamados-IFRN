package com.example.sistemaschamadosifrn.controller;

import com.example.sistemaschamadosifrn.dto.CategoriaResponse;
import com.example.sistemaschamadosifrn.dto.PaginaResponse;
import com.example.sistemaschamadosifrn.dto.SetorRequest;
import com.example.sistemaschamadosifrn.dto.SetorResponse;
import com.example.sistemaschamadosifrn.service.CategoriaService;
import com.example.sistemaschamadosifrn.service.SetorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setores")
@RequiredArgsConstructor
public class SetorController {

    private final SetorService setorService;
    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<SetorResponse> criar(@Valid @RequestBody SetorRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(SetorResponse.de(setorService.criar(dto)));
    }

    @GetMapping
    public PaginaResponse<SetorResponse> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return PaginaResponse.de(setorService.listar(nome, pageable), SetorResponse::de);
    }

    @GetMapping("/{id}")
    public SetorResponse buscar(@PathVariable Long id) {
        return SetorResponse.de(setorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public SetorResponse atualizar(@PathVariable Long id, @Valid @RequestBody SetorRequest dto) {
        return SetorResponse.de(setorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        setorService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/categorias")
    public PaginaResponse<CategoriaResponse> listarCategorias(@PathVariable Long id, Pageable pageable) {
        return PaginaResponse.de(categoriaService.listarPorSetor(id, pageable), CategoriaResponse::de);
    }
}