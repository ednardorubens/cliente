package br.com.ermig.cliente.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.ermig.cliente.model.Cliente;

public interface ClienteRepository extends PagingAndSortingRepository<Cliente, Long> {

    Page<Cliente> findByCpfContainsAndNomeContains(final String cpf, final String nome, final Pageable pageable);
    
}
