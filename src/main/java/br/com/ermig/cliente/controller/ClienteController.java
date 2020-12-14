package br.com.ermig.cliente.controller;

import java.net.URI;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ermig.cliente.model.Cliente;
import br.com.ermig.cliente.repository.ClienteRepository;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
@RequestMapping("/clientes")
public class ClienteController {
    
    @Autowired
	private ClienteRepository clienteRepository;

	@PostMapping
	@CacheEvict(value = "clientes", allEntries = true)
	@ApiOperation(value = "Cliente", notes = "Salvar um cliente")
	public ResponseEntity<Cliente> salvar(@RequestBody @Valid final Cliente cliente, final UriComponentsBuilder uriBuilder) {
		final Cliente clienteSalvo = this.clienteRepository.save(cliente);
		final URI uri = uriBuilder.path("/clientes/{id}").buildAndExpand(clienteSalvo.getId()).toUri();
		return ResponseEntity.created(uri).body(clienteSalvo);
	}

	@GetMapping
	@Cacheable(value = "clientes", key = "#id")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
				value = "Pagina a ser carregada", defaultValue = "0"),
		@ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
				value = "Quantidade de registros", defaultValue = "5"),
		@ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
				value = "Ordenacao dos registros")
	})
	public Page<Cliente> listar(
		@RequestParam(name = "cpf", required = false, defaultValue = "")
		final String cpf,
		@RequestParam(name = "nome", required = false, defaultValue = "")
		final String nome,
		@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10)
		@ApiIgnore final Pageable pageable) {
		return this.clienteRepository.findByCpfContainsAndNomeContains(cpf, nome, pageable);
	}

	@GetMapping("/{id}")
	@Cacheable(value = "clientes", key = "#id")
	public ResponseEntity<Cliente> buscar(@PathVariable final Long id) {
		return ResponseEntity.of(this.clienteRepository.findById(id));
	}

    @RequestMapping(value = "/{id}", method = { RequestMethod.PUT, RequestMethod.PATCH })
	@CacheEvict(value = "clientes", allEntries = true)
	public ResponseEntity<Cliente> atualizar(@PathVariable final Long id, @RequestBody final Cliente cliente) {
		return ResponseEntity.of(this.clienteRepository.findById(id)
			.map(clienteDB -> {
				copyProperties(cliente, clienteDB);
				final Set<ConstraintViolation<Cliente>> erros = this.validate(clienteDB);
				if (!erros.isEmpty()) {
					throw new ConstraintViolationException(erros);
				}
				return this.clienteRepository.save(clienteDB);
			})
		);
	}

	private void copyProperties(final Cliente cliente, Cliente clienteDB) {
		if (cliente.getNome() != null) {
			clienteDB.setNome(cliente.getNome());
		}
		if (cliente.getCpf() != null) {
			clienteDB.setCpf(cliente.getCpf());
		}
		if (cliente.getDataNascimento() != null) {
			clienteDB.setDataNascimento(cliente.getDataNascimento());
		}
	}

	@DeleteMapping("/{id}")
	@CacheEvict(value = "clientes", allEntries = true)
	public ResponseEntity<?> remover(@PathVariable final Long id) {
		return this.clienteRepository.findById(id).map(clienteDB -> {
			this.clienteRepository.delete(clienteDB);
			return ResponseEntity.noContent().build();
		}).orElse(ResponseEntity.notFound().build());
	}

    private <T> Set<ConstraintViolation<T>> validate(final T type) {
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		return validator.validate(type);
	}
}
