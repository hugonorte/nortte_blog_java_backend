package com_abertamente_cms.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("User not found");

        ProblemDetail problemDetail = exceptionHandler.handleEntityNotFoundException(ex);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Entidade Não Encontrada", problemDetail.getTitle());
        assertEquals("User not found", problemDetail.getDetail());
        assertEquals(URI.create("https://abertamente.net/erros/entidade-nao-encontrada"), problemDetail.getType());
    }

    @Test
    void shouldHandleValidationExceptions() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "email", "Email cannot be blank"));

        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethod("setUp"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(ex, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, null);
        ProblemDetail problemDetail = (ProblemDetail) response.getBody();

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Erro de Validação", problemDetail.getTitle());
        assertEquals("A validação dos dados falhou. Verifique os campos informados.", problemDetail.getDetail());
        assertEquals(URI.create("https://abertamente.net/erros/validacao-falhou"), problemDetail.getType());

        Map<String, String> invalidParams = (Map<String, String>) problemDetail.getProperties().get("invalid_params");
        assertNotNull(invalidParams);
        assertEquals("Email cannot be blank", invalidParams.get("email"));
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new Exception("Some unexpected error");

        ProblemDetail problemDetail = exceptionHandler.handleGenericException(ex);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals("Erro Interno do Servidor", problemDetail.getTitle());
        assertEquals("Ocorreu um erro interno inesperado. Detalhes: Some unexpected error", problemDetail.getDetail());
        assertEquals(URI.create("https://abertamente.net/erros/erro-interno"), problemDetail.getType());
    }
}
