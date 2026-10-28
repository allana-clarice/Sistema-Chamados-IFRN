package com.example.sistemaschamadosifrn.service;

import com.example.sistemaschamadosifrn.dto.SetorRequest;
import com.example.sistemaschamadosifrn.exception.RecursoNaoEncontradoException;
import com.example.sistemaschamadosifrn.exception.RegraNegocioException;
import com.example.sistemaschamadosifrn.model.Setor;
import com.example.sistemaschamadosifrn.repository.CategoriaRepository;
import com.example.sistemaschamadosifrn.repository.ChamadoRepository;
import com.example.sistemaschamadosifrn.repository.FiltroSpecs;
import com.example.sistemaschamadosifrn.repository.SetorRepository;
import com.example.sistemaschamadosifrn.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SetorService {

    private final SetorRepository setorRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ChamadoRepository chamadoRepository;

    public Setor criar(SetorRequest dto) {
        if (setorRepository.existsBySiglaIgnoreCase(dto.sigla().trim())) {
            throw new RegraNegocioException("Já existe um setor com a sigla " + dto.sigla().trim() + ".");
        }
        Setor setor = Setor.builder().nome(dto.nome().trim()).sigla(dto.sigla().trim()).build();
        return setorRepository.save(setor);
    }

    @Transactional(readOnly = true)
    public Setor buscarPorId(Long id) {
        return setorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Setor não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Setor> listar(String nome, Pageable pageable) {
        return setorRepository.findAll(FiltroSpecs.setores(nome), pageable);
    }

    public Setor atualizar(Long id, SetorRequest dto) {
        Setor setor = buscarPorId(id);
        if (setorRepository.existsBySiglaIgnoreCaseAndIdNot(dto.sigla().trim(), id)) {
            throw new RegraNegocioException("Já existe um setor com a sigla " + dto.sigla().trim() + ".");
        }
        setor.setNome(dto.nome().trim());
        setor.setSigla(dto.sigla().trim());
        return setorRepository.save(setor);
    }

    public void excluir(Long id) {
        Setor setor = buscarPorId(id);
        if (chamadoRepository.existsByCategoriaSetorId(id)) {
            throw new RegraNegocioException("Não é possível excluir o setor: existem chamados vinculados às suas categorias.");
        }
        if (categoriaRepository.existsBySetorId(id)) {
            throw new RegraNegocioException("Não é possível excluir o setor: ele possui categorias cadastradas.");
        }
        if (usuarioRepository.existsBySetorId(id)) {
            throw new RegraNegocioException("Não é possível excluir o setor: ele possui técnicos vinculados.");
        }
        setorRepository.delete(setor);
    }
}