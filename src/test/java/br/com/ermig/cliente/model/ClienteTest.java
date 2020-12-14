package br.com.ermig.cliente.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClienteTest {

    @Test
    public void validarCliente() {
        final List<String> erros = Arrays.asList("O Nome não pode estar vazio", "O CPF deve conter 11 dígitos");
        validate(new Cliente()).stream().map(ConstraintViolation::getMessage).allMatch(erros::contains);
    }

    @Test
    public void calcularIdadeCliente() {
        final Cliente cliente = new Cliente();
        cliente.setDataNascimento(LocalDate.of(1980, 06, 01));
        Assertions.assertEquals(40, cliente.getIdade());
    }

    private <T> Set<ConstraintViolation<T>> validate(final T type) {
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		return validator.validate(type);
	}
    
}
