package com.example.sistemaschamadosifrn.controller;

import com.example.sistemaschamadosifrn.dto.CategoriaRequest;
import com.example.sistemaschamadosifrn.dto.CategoriaResponse;
import com.example.sistemaschamadosifrn.dto.PaginaResponse;
import com.example.sistemaschamadosifrn.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaResponse.de(categoriaService.criar(dto)));
    }

    @GetMapping
    public PaginaResponse<CategoriaResponse> listar(@RequestParam(required = false) String nome,
                                                    @RequestParam(required = false) Long setorId,
                                                    Pageable pageable) {
        return PaginaResponse.de(categoriaService.listar(nome, setorId, pageable), CategoriaResponse::de);
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscar(@PathVariable Long id) {
        return CategoriaResponse.de(categoriaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public CategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest dto) {
        return CategoriaResponse.de(categoriaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}