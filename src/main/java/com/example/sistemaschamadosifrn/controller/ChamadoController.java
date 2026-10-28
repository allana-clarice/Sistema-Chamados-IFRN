package com.example.sistemaschamadosifrn.controller;

import com.example.sistemaschamadosifrn.dto.AssumirChamadoDTO;
import com.example.sistemaschamadosifrn.dto.CancelarChamadoDTO;
import com.example.sistemaschamadosifrn.dto.ChamadoDetalheResponse;
import com.example.sistemaschamadosifrn.dto.ChamadoRequestDTO;
import com.example.sistemaschamadosifrn.dto.ChamadoResponse;
import com.example.sistemaschamadosifrn.dto.InteracaoRequestDTO;
import com.example.sistemaschamadosifrn.dto.InteracaoResponse;
import com.example.sistemaschamadosifrn.dto.PaginaResponse;
import com.example.sistemaschamadosifrn.dto.ResolverChamadoDTO;
import com.example.sistemaschamadosifrn.model.enums.PrioridadeChamado;
import com.example.sistemaschamadosifrn.model.enums.StatusChamado;
import com.example.sistemaschamadosifrn.service.ChamadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ChamadoController {

    private final ChamadoService chamadoService;

    @PostMapping("/chamados")
    public ResponseEntity<ChamadoResponse> abrir(@Valid @RequestBody ChamadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ChamadoResponse.de(chamadoService.abrirChamado(dto)));
    }

    @GetMapping("/chamados")
    public PaginaResponse<ChamadoResponse> listar(
            @RequestParam(required = false) StatusChamado status,
            @RequestParam(required = false) PrioridadeChamado prioridade,
            @RequestParam(required = false) Long setorId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long solicitanteId,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable pageable) {
        return PaginaResponse.de(chamadoService.listarComFiltros(status, prioridade, setorId, categoriaId,
                solicitanteId, tecnicoId, dataInicio, dataFim, pageable), ChamadoResponse::de);
    }

    @GetMapping("/chamados/{id}")
    public ChamadoDetalheResponse detalhar(@PathVariable Long id) {
        return ChamadoDetalheResponse.de(chamadoService.buscarPorId(id));
    }

    @GetMapping("/usuarios/{id}/chamados")
    public PaginaResponse<ChamadoResponse> listarPorUsuario(
            @PathVariable Long id,
            @RequestParam(required = false) StatusChamado status,
            @PageableDefault(sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable pageable) {
        return PaginaResponse.de(chamadoService.listarPorUsuario(id, status, pageable), ChamadoResponse::de);
    }

    @PatchMapping("/chamados/{id}/assumir")
    public ChamadoResponse assumir(@PathVariable Long id, @Valid @RequestBody AssumirChamadoDTO dto) {
        return ChamadoResponse.de(chamadoService.assumirChamado(id, dto.tecnicoId()));
    }

    @PatchMapping("/chamados/{id}/resolver")
    public ChamadoResponse resolver(@PathVariable Long id, @Valid @RequestBody ResolverChamadoDTO dto) {
        return ChamadoResponse.de(chamadoService.resolverChamado(id, dto));
    }

    @PatchMapping("/chamados/{id}/cancelar")
    public ChamadoResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelarChamadoDTO dto) {
        return ChamadoResponse.de(chamadoService.cancelarChamado(id, dto.solicitanteId()));
    }

    @PostMapping("/chamados/{id}/interacoes")
    public ResponseEntity<InteracaoResponse> interagir(@PathVariable Long id,
                                                       @Valid @RequestBody InteracaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InteracaoResponse.de(chamadoService.adicionarInteracao(id, dto)));
    }
}