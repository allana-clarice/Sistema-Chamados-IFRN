package com.example.sistemaschamadosifrn.service;

import com.example.sistemaschamadosifrn.dto.UsuarioRequest;
import com.example.sistemaschamadosifrn.exception.RecursoNaoEncontradoException;
import com.example.sistemaschamadosifrn.exception.RegraNegocioException;
import com.example.sistemaschamadosifrn.model.Setor;
import com.example.sistemaschamadosifrn.model.Usuario;
import com.example.sistemaschamadosifrn.model.enums.TipoUsuario;
import com.example.sistemaschamadosifrn.repository.FiltroSpecs;
import com.example.sistemaschamadosifrn.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SetorService setorService;

    public Usuario criar(UsuarioRequest dto) {
        if (usuarioRepository.existsByEmailIgnoreCase(dto.email().trim())) {
            throw new RegraNegocioException("Já existe um usuário com o e-mail " + dto.email().trim() + ".");
        }
        Usuario usuario = new Usuario();
        preencher(usuario, dto);
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Usuario> listar(String nome, TipoUsuario tipo, Long setorId, Pageable pageable) {
        return usuarioRepository.findAll(FiltroSpecs.usuarios(nome, tipo, setorId), pageable);
    }

    public Usuario atualizar(Long id, UsuarioRequest dto) {
        Usuario usuario = buscarPorId(id);
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(dto.email().trim(), id)) {
            throw new RegraNegocioException("Já existe um usuário com o e-mail " + dto.email().trim() + ".");
        }
        preencher(usuario, dto);
        return usuarioRepository.save(usuario);
    }

    private void preencher(Usuario usuario, UsuarioRequest dto) {
        Setor setor = null;
        if (dto.tipo() == TipoUsuario.TECNICO) {
            if (dto.setorId() == null) {
                throw new RegraNegocioException("Todo técnico deve pertencer a um setor (informe setorId).");
            }
            setor = setorService.buscarPorId(dto.setorId());
        }
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(dto.email().trim());
        usuario.setMatricula(dto.matricula().trim());
        usuario.setTipo(dto.tipo());
        usuario.setSetor(setor);
    }
}