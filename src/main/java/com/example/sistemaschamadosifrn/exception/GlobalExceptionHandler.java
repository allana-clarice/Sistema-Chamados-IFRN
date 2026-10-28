package com.example.sistemaschamadosifrn.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static ResponseEntity<ErroResposta> erro(HttpStatus status, String mensagem, String caminho) {
        return ResponseEntity.status(status)
                .body(new ErroResposta(LocalDateTime.now(), status.value(), mensagem, caminho));
    }

    private static String caminho(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> handleRegraNegocio(RegraNegocioException ex, HttpServletRequest req) {
        return erro(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return erro(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> handleIntegridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        return erro(HttpStatus.CONFLICT,
                "Violação de integridade: registro duplicado ou vinculado a outros registros.", req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> handleGenerico(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado em {}", req.getRequestURI(), ex);
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.", req.getRequestURI());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        if (mensagem.isBlank()) mensagem = "Dados inválidos";
        return ResponseEntity.status(status)
                .body(new ErroResposta(LocalDateTime.now(), status.value(), mensagem, caminho(request)));
    }

    /** Cobre os erros padrão do Spring MVC (JSON inválido, enum inválido, rota inexistente, método não suportado...). */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        String mensagem;
        if (ex instanceof HttpMessageNotReadableException) {
            mensagem = "Corpo da requisição inválido ou ilegível (verifique o JSON e os valores dos enums).";
        } else if (ex instanceof TypeMismatchException tm) {
            mensagem = "Valor inválido para o parâmetro '" + tm.getPropertyName() + "'.";
        } else if (body instanceof ProblemDetail pd && pd.getDetail() != null) {
            mensagem = pd.getDetail();
        } else {
            mensagem = ex.getMessage();
        }
        return ResponseEntity.status(statusCode).headers(headers)
                .body(new ErroResposta(LocalDateTime.now(), statusCode.value(), mensagem, caminho(request)));
    }
}