package br.com.ermig.cliente.config.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

/**
 * Classe que representa a exceção de regra de negócio.
 *
 */
@JsonIgnoreProperties({ "cause", "stackTrace", "suppressed", "localizedMessage" })
public class RegraNegocioException extends RuntimeException {

	private static final long serialVersionUID = 6649753476227679878L;
	
	@Getter
	private final Integer code;

	public RegraNegocioException(final Integer code, final String message) {
        super(message);
        this.code = code;
    }

    public RegraNegocioException(final Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
