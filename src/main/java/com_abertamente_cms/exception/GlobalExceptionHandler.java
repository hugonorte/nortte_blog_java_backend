package com_abertamente_cms.exception;

import io.sentry.Sentry;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Entidade Não Encontrada");
        problemDetail.setType(URI.create("https://abertamente.net/erros/entidade-nao-encontrada"));
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "A validação dos dados falhou. Verifique os campos informados.");
        problemDetail.setTitle("Erro de Validação");
        problemDetail.setType(URI.create("https://abertamente.net/erros/validacao-falhou"));

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("invalid_params", errors);

        return createResponseEntity(problemDetail, headers, status, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso.");
        problemDetail.setTitle("Acesso Proibido");
        problemDetail.setType(URI.create("https://abertamente.net/erros/acesso-proibido"));
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Autenticação necessária ou token inválido.");
        problemDetail.setTitle("Não Autorizado");
        problemDetail.setType(URI.create("https://abertamente.net/erros/nao-autorizado"));
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflito / Parâmetro Inválido");
        problemDetail.setType(URI.create("https://abertamente.net/erros/conflito"));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        Sentry.captureException(ex);
        ex.printStackTrace();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado. Detalhes: " + ex.getMessage());
        problemDetail.setTitle("Erro Interno do Servidor");
        problemDetail.setType(URI.create("https://abertamente.net/erros/erro-interno"));
        return problemDetail;
    }
}
