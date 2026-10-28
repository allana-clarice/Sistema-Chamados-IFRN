package com.example.sistemaschamadosifrn.repository;

import com.example.sistemaschamadosifrn.model.Categoria;
import com.example.sistemaschamadosifrn.model.Chamado;
import com.example.sistemaschamadosifrn.model.Setor;
import com.example.sistemaschamadosifrn.model.Usuario;
import com.example.sistemaschamadosifrn.model.enums.PrioridadeChamado;
import com.example.sistemaschamadosifrn.model.enums.StatusChamado;
import com.example.sistemaschamadosifrn.model.enums.TipoUsuario;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Filtros opcionais e combináveis: filtro nulo é ignorado. */
public final class FiltroSpecs {

    private FiltroSpecs() {
    }

    private static boolean temTexto(String s) {
        return s != null && !s.isBlank();
    }

    private static Predicate contem(CriteriaBuilder cb, Expression<String> campo, String valor) {
        return cb.like(cb.lower(campo), "%" + valor.trim().toLowerCase() + "%");
    }

    public static Specification<Setor> setores(String nome) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (temTexto(nome)) p.add(contem(cb, root.get("nome"), nome));
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    public static Specification<Categoria> categorias(String nome, Long setorId) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (temTexto(nome)) p.add(contem(cb, root.get("nome"), nome));
            if (setorId != null) p.add(cb.equal(root.join("setor").get("id"), setorId));
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    public static Specification<Usuario> usuarios(String nome, TipoUsuario tipo, Long setorId) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (temTexto(nome)) p.add(contem(cb, root.get("nome"), nome));
            if (tipo != null) p.add(cb.equal(root.get("tipo"), tipo));
            if (setorId != null) p.add(cb.equal(root.join("setor").get("id"), setorId));
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    public static Specification<Chamado> chamados(StatusChamado status, PrioridadeChamado prioridade,
                                                  Long setorId, Long categoriaId, Long solicitanteId,
                                                  Long tecnicoId, LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (status != null) p.add(cb.equal(root.get("status"), status));
            if (prioridade != null) p.add(cb.equal(root.get("prioridade"), prioridade));
            if (setorId != null) p.add(cb.equal(root.join("categoria").get("setor").get("id"), setorId));
            if (categoriaId != null) p.add(cb.equal(root.get("categoria").get("id"), categoriaId));
            if (solicitanteId != null) p.add(cb.equal(root.get("solicitante").get("id"), solicitanteId));
            if (tecnicoId != null) p.add(cb.equal(root.join("tecnico").get("id"), tecnicoId));
            if (dataInicio != null) {
                p.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("dataAbertura"), dataInicio.atStartOfDay()));
            }
            if (dataFim != null) {
                p.add(cb.lessThan(root.<LocalDateTime>get("dataAbertura"), dataFim.plusDays(1).atStartOfDay()));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    /** Chamados abertos pelo usuário (solicitante) ou atribuídos a ele (técnico). */
    public static Specification<Chamado> chamadosDoUsuario(Long usuarioId, StatusChamado status) {
        return (root, query, cb) -> {
            var tecnico = root.join("tecnico", JoinType.LEFT);
            List<Predicate> p = new ArrayList<>();
            p.add(cb.or(
                    cb.equal(root.get("solicitante").get("id"), usuarioId),
                    cb.equal(tecnico.get("id"), usuarioId)));
            if (status != null) p.add(cb.equal(root.get("status"), status));
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}