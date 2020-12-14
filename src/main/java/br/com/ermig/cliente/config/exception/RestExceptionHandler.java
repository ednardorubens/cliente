package br.com.ermig.cliente.config.exception;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Object> handleRegraNegocioException(final RegraNegocioException ex, final WebRequest request) {
		final Map<String, Object> mapErrors = Collections.singletonMap("response", ex);
        return handleExceptionInternal(ex, mapErrors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenException(final Exception ex, final WebRequest request) {
    	String message = ex.getMessage();
		log.error(message, ex);
		if (ex instanceof SQLException || ex instanceof DataAccessException) {
			message = this.messageSource.getMessage("erro.banco.dados", null, LocaleContextHolder.getLocale());
		}
    	final Map<String, String> mapErrors = Collections.singletonMap("error", message);
    	return super.handleExceptionInternal(ex, mapErrors, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> invalidParamsExceptionHandler(final ConstraintViolationException ex, final WebRequest request) {
        final List<FieldError> errors = this.resolveConstraintViolations(ex);
        final Map<String, List<FieldError>> mapErrors = Collections.singletonMap("errors", errors);
		return handleExceptionInternal(ex, mapErrors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		final List<FieldError> errors = resolveBindingResultErrors(ex);
		final Map<String, List<FieldError>> mapErrors = Collections.singletonMap("errors", errors);
		return handleExceptionInternal(ex, mapErrors, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body,
	final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		log.error("Body: {} - Error: {}", body, ex.getMessage());
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
	
	private List<FieldError> resolveConstraintViolations(final ConstraintViolationException ex) {
		return ex.getConstraintViolations().stream().map(cv -> {
			final String name = cv.getPropertyPath().toString();
			final String error = String.format("O %s %s", name, cv.getMessage());
			return new FieldError(name, error);
		}).sorted().collect(Collectors.toList());
	}

	private List<FieldError> resolveBindingResultErrors(final MethodArgumentNotValidException ex) {
		return ex.getBindingResult().getFieldErrors().stream().map(
			e -> new FieldError(e.getField(), this.messageSource.getMessage(e, LocaleContextHolder.getLocale()))
		).sorted().collect(Collectors.toList());
	}

	@Getter
	@ToString
	@RequiredArgsConstructor
	private class FieldError implements Comparable<FieldError> {

		private final String name;
		private final String error;

		@Override
		public int compareTo(FieldError other) {
			return this.name.compareTo(other.name);
		}

	}

}
