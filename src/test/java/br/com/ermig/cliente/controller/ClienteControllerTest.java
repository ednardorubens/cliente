package br.com.ermig.cliente.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ermig.cliente.model.Cliente;
import br.com.ermig.cliente.repository.ClienteRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("deprecation")
class ClienteControllerTest {

	private final Cliente cliente = new Cliente(1L, "Felizberto Soares", "12345678901", LocalDate.of(1960, 5, 14));

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ClienteRepository clienteRepository;

	@Test
	void testErroSalvarCliente() throws Exception {
		this.mvc.perform(
			post("/clientes")
			.content("{\"cpf\": 123}")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
		)
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.errors").isNotEmpty())
		.andExpect(jsonPath("$.errors[0].name").value("cpf"))
		.andExpect(jsonPath("$.errors[0].error").value("O cpf deve conter 11 dígitos"))
		.andExpect(jsonPath("$.errors[1].name").value("nome"))
		.andExpect(jsonPath("$.errors[1].error").value("O nome não deve estar em branco"));
	}

	@Test
	void testSalvarCliente() throws Exception {
		when(this.clienteRepository.save(any(Cliente.class))).thenReturn(this.cliente);

		this.mvc.perform(
			post("/clientes")
			.content("{ \"nome\": \"" + this.cliente.getNome() + "\", \"cpf\": \"" + this.cliente.getCpf() + "\", \"dataNascimento\": \"" + this.cliente.getDataNascimento() + "\" }")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
		)
		.andExpect(status().isCreated())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(header().string("Location", "http://localhost/clientes/" + this.cliente.getId().intValue()))
		.andExpect(jsonPath("$.id").value(this.cliente.getId()))
        .andExpect(jsonPath("$.nome").value(this.cliente.getNome()))
        .andExpect(jsonPath("$.cpf").value(this.cliente.getCpf()))
        .andExpect(jsonPath("$.dataNascimento").value(this.cliente.getDataNascimento().toString()))
        .andExpect(jsonPath("$.idade").value(this.cliente.getIdade()));
	}

	@Test
	void testListarCliente() throws Exception {
		when(this.clienteRepository.findByCpfContainsAndNomeContains(anyString(), anyString(), any(Pageable.class))).then(
			invocation -> {
				final Pageable pageable = (Pageable) invocation.getArguments()[2];
				return new PageImpl<>(List.of(this.cliente), pageable, 1);
			}
		);

		this.mvc.perform(
			get("/clientes")
		)
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.content[0].id").value(this.cliente.getId()))
		.andExpect(jsonPath("$.content[0].nome").value(this.cliente.getNome()));
	}

	@Test
	void testBuscarCliente() throws Exception {
		when(this.clienteRepository.findById(any(Long.class))).thenReturn(Optional.of(this.cliente));

		this.mvc.perform(
			get("/clientes/1")
		)
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.id").value(this.cliente.getId()))
		.andExpect(jsonPath("$.nome").value(this.cliente.getNome()));
	}

	@Test
	void testAtualizarClienteComPut() throws Exception {
		when(this.clienteRepository.findById(any(Long.class))).thenReturn(Optional.of(this.cliente));
		when(this.clienteRepository.save(any(Cliente.class))).then(
			invocation -> (Cliente) invocation.getArguments()[0]
		);

		this.mvc.perform(
			put("/clientes/1")
			.content("{ \"nome\": \"Felizberto Soares de Almeida Sá\" }")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
		)
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.id").value(this.cliente.getId()))
		.andExpect(jsonPath("$.nome").value("Felizberto Soares de Almeida Sá"));
	}

	@Test
	void testAtualizarClienteComPatch() throws Exception {
		when(this.clienteRepository.findById(any(Long.class))).thenReturn(Optional.of(this.cliente));
		when(this.clienteRepository.save(any(Cliente.class))).then(
			invocation -> (Cliente) invocation.getArguments()[0]
		);

		this.mvc.perform(
			patch("/clientes/1")
			.content("{ \"nome\": \"\" }")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
		)
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.errors").isNotEmpty())
		.andExpect(jsonPath("$.errors[0].name").value("nome"))
		.andExpect(jsonPath("$.errors[0].error").value("O nome não deve estar em branco"));
	}

	@Test
	void testRemoverCliente() throws Exception {
		when(this.clienteRepository.findById(any(Long.class))).thenReturn(Optional.of(this.cliente));

		this.mvc.perform(
			delete("/clientes/1")
		)
		.andExpect(status().isNoContent());
	}

}
