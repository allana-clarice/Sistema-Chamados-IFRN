package com.example.sistemaschamadosifrn.service;

import com.example.sistemaschamadosifrn.dto.ChamadoRequestDTO;
import com.example.sistemaschamadosifrn.dto.InteracaoRequestDTO;
import com.example.sistemaschamadosifrn.dto.ResolverChamadoDTO;
import com.example.sistemaschamadosifrn.exception.RecursoNaoEncontradoException;
import com.example.sistemaschamadosifrn.exception.RegraNegocioException;
import com.example.sistemaschamadosifrn.model.Categoria;
import com.example.sistemaschamadosifrn.model.Chamado;
import com.example.sistemaschamadosifrn.model.Interacao;
import com.example.sistemaschamadosifrn.model.Usuario;
import com.example.sistemaschamadosifrn.model.enums.PrioridadeChamado;
import com.example.sistemaschamadosifrn.model.enums.StatusChamado;
import com.example.sistemaschamadosifrn.model.enums.TipoUsuario;
import com.example.sistemaschamadosifrn.repository.ChamadoRepository;
import com.example.sistemaschamadosifrn.repository.FiltroSpecs;
import com.example.sistemaschamadosifrn.repository.InteracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final InteracaoRepository interacaoRepository;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    // Regras 1 e 2
    public Chamado abrirChamado(ChamadoRequestDTO dto) {
        Usuario solicitante = usuarioService.buscarPorId(dto.solicitanteId());
        if (solicitante.getTipo() != TipoUsuario.SOLICITANTE) {
            throw new RegraNegocioException("Apenas usuários do tipo SOLICITANTE podem abrir chamados.");
        }

        Categoria categoria = categoriaService.buscarPorId(dto.categoriaId());

        Chamado chamado = Chamado.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .local(dto.local())
                .prioridade(dto.prioridade() != null ? dto.prioridade() : PrioridadeChamado.MEDIA)
                .status(StatusChamado.ABERTO)
                .dataAbertura(LocalDateTime.now())
                .solicitante(solicitante)
                .categoria(categoria)
                .build();

        return chamadoRepository.save(chamado);
    }

    @Transactional(readOnly = true)
    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Chamado> listarComFiltros(StatusChamado status, PrioridadeChamado prioridade, Long setorId,
                                          Long categoriaId, Long solicitanteId, Long tecnicoId,
                                          LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return chamadoRepository.findAll(FiltroSpecs.chamados(status, prioridade, setorId, categoriaId,
                solicitanteId, tecnicoId, dataInicio, dataFim), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Chamado> listarPorUsuario(Long usuarioId, StatusChamado status, Pageable pageable) {
        usuarioService.buscarPorId(usuarioId);
        return chamadoRepository.findAll(FiltroSpecs.chamadosDoUsuario(usuarioId, status), pageable);
    }

    // Regra 3
    public Chamado assumirChamado(Long id, Long tecnicoId) {
        Chamado chamado = buscarPorId(id);
        Usuario tecnico = usuarioService.buscarPorId(tecnicoId);

        validarNaoEncerrado(chamado, "Chamados já resolvidos ou cancelados não podem ser alterados.");

        if (tecnico.getTipo() != TipoUsuario.TECNICO) {
            throw new RegraNegocioException("Apenas usuários do tipo TECNICO podem assumir chamados.");
        }
        if (chamado.getStatus() != StatusChamado.ABERTO) {
            throw new RegraNegocioException("O chamado precisa estar com status ABERTO para ser assumido.");
        }
        if (tecnico.getSetor() == null
                || !chamado.getCategoria().getSetor().getId().equals(tecnico.getSetor().getId())) {
            throw new RegraNegocioException("O técnico pertence a um setor diferente da categoria deste chamado.");
        }

        chamado.setTecnico(tecnico);
        chamado.setStatus(StatusChamado.EM_ATENDIMENTO);
        return chamadoRepository.save(chamado);
    }

    // Regra 4
    public Chamado resolverChamado(Long id, ResolverChamadoDTO dto) {
        Chamado chamado = buscarPorId(id);

        validarNaoEncerrado(chamado, "Chamados já resolvidos ou cancelados não podem ser alterados.");

        if (chamado.getTecnico() == null || !chamado.getTecnico().getId().equals(dto.tecnicoId())) {
            throw new RegraNegocioException("Somente o técnico atribuído a este chamado pode resolvê-lo.");
        }
        if (chamado.getStatus() != StatusChamado.EM_ATENDIMENTO) {
            throw new RegraNegocioException("O chamado precisa estar EM_ATENDIMENTO para ser resolvido.");
        }
        if (dto.solucao() == null || dto.solucao().isBlank()) {
            throw new RegraNegocioException("A solução é obrigatória para resolver o chamado.");
        }

        chamado.setStatus(StatusChamado.RESOLVIDO);
        chamado.setSolucao(dto.solucao().trim());
        chamado.setDataFechamento(LocalDateTime.now());
        return chamadoRepository.save(chamado);
    }

    // Regra 5
    public Chamado cancelarChamado(Long id, Long solicitanteId) {
        Chamado chamado = buscarPorId(id);

        validarNaoEncerrado(chamado, "Chamados já resolvidos ou cancelados não podem ser alterados.");

        if (chamado.getStatus() != StatusChamado.ABERTO) {
            throw new RegraNegocioException("O chamado só pode ser cancelado enquanto estiver ABERTO.");
        }
        if (!chamado.getSolicitante().getId().equals(solicitanteId)) {
            throw new RegraNegocioException("Somente o próprio solicitante pode cancelar este chamado.");
        }

        chamado.setStatus(StatusChamado.CANCELADO);
        chamado.setDataFechamento(LocalDateTime.now());
        return chamadoRepository.save(chamado);
    }

    // Regras 6 e 7
    public Interacao adicionarInteracao(Long chamadoId, InteracaoRequestDTO dto) {
        Chamado chamado = buscarPorId(chamadoId);

        validarNaoEncerrado(chamado, "Este chamado está encerrado e não aceita novas interações.");

        Usuario autor = usuarioService.buscarPorId(dto.autorId());

        boolean isSolicitante = chamado.getSolicitante().getId().equals(autor.getId());
        boolean isTecnicoAtribuido = chamado.getTecnico() != null
                && chamado.getTecnico().getId().equals(autor.getId());

        if (!isSolicitante && !isTecnicoAtribuido) {
            throw new RegraNegocioException("Apenas o solicitante e o técnico atribuído podem interagir neste chamado.");
        }

        Interacao interacao = Interacao.builder()
                .texto(dto.texto().trim())
                .dataHora(LocalDateTime.now())
                .chamado(chamado)
                .autor(autor)
                .build();

        return interacaoRepository.save(interacao);
    }

    private void validarNaoEncerrado(Chamado chamado, String mensagem) {
        if (chamado.getStatus() == StatusChamado.RESOLVIDO || chamado.getStatus() == StatusChamado.CANCELADO) {
            throw new RegraNegocioException(mensagem);
        }
    }
}