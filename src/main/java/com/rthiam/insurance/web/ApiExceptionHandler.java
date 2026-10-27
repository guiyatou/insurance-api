package com.rthiam.insurance.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.rthiam.insurance.service.ContratNotFoundException;
import com.rthiam.insurance.service.NumeroContratDejaUtiliseException;

/**
 * Translates exceptions into RFC 9457 "problem detail" JSON responses.
 * Standard Spring MVC errors (malformed JSON, bad parameter type...) are handled by the parent class.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ContratNotFoundException.class)
	public ProblemDetail handleNotFound(ContratNotFoundException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(NumeroContratDejaUtiliseException.class)
	public ProblemDetail handleNumeroDejaUtilise(NumeroContratDejaUtiliseException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
	}

	/** Safety net when two concurrent requests pass the uniqueness check. */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Data integrity violation");
	}

	/** Adds an "errors" map (field name to message) to validation error responses. */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			String name = (error instanceof FieldError fieldError) ? fieldError.getField() : error.getObjectName();
			errors.putIfAbsent(name, error.getDefaultMessage());
		}
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, "Validation failed");
		problem.setProperty("errors", errors);
		return handleExceptionInternal(ex, problem, headers, status, request);
	}

}
