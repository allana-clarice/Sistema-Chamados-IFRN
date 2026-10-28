package com.example.sistemaschamadosifrn.service;

import com.example.sistemaschamadosifrn.dto.CategoriaRequest;
import com.example.sistemaschamadosifrn.exception.RecursoNaoEncontradoException;
import com.example.sistemaschamadosifrn.exception.RegraNegocioException;
import com.example.sistemaschamadosifrn.model.Categoria;
import com.example.sistemaschamadosifrn.model.Setor;
import com.example.sistemaschamadosifrn.repository.CategoriaRepository;
import com.example.sistemaschamadosifrn.repository.ChamadoRepository;
import com.example.sistemaschamadosifrn.repository.FiltroSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ChamadoRepository chamadoRepository;
    private final SetorService setorService;

    public Categoria criar(CategoriaRequest dto) {
        Setor setor = setorService.buscarPorId(dto.setorId());
        Categoria categoria = Categoria.builder()
                .nome(dto.nome().trim())
                .descricao(dto.descricao())
                .setor(setor)
                .build();
        return categoriaRepository.save(categoria);
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Categoria> listar(String nome, Long setorId, Pageable pageable) {
        return categoriaRepository.findAll(FiltroSpecs.categorias(nome, setorId), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Categoria> listarPorSetor(Long setorId, Pageable pageable) {
        setorService.buscarPorId(setorId);
        return categoriaRepository.findAll(FiltroSpecs.categorias(null, setorId), pageable);
    }

    public Categoria atualizar(Long id, CategoriaRequest dto) {
        Categoria categoria = buscarPorId(id);
        categoria.setNome(dto.nome().trim());
        categoria.setDescricao(dto.descricao());
        categoria.setSetor(setorService.buscarPorId(dto.setorId()));
        return categoriaRepository.save(categoria);
    }

    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id);
        if (chamadoRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException("Não é possível excluir a categoria: existem chamados vinculados a ela.");
        }
        categoriaRepository.delete(categoria);
    }
}