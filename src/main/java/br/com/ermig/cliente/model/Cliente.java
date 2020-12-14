package br.com.ermig.cliente.model;

import java.time.LocalDate;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "id" })
@Table(name = "clientes", uniqueConstraints = @UniqueConstraint(name = "cliente_cpf_uk", columnNames = { "cpf" }))
public class Cliente {
    
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@NotBlank
	@Column(name = "nome", columnDefinition = "varchar(150)", nullable = false)
    private String nome;
    
    @NotBlank
    @Size(min = 11, max = 11)
	@Column(name = "cpf", columnDefinition = "varchar(11)", nullable = false)
    private String cpf;
    
    @Past
	@Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    public int getIdade() {
        return Period.between(this.dataNascimento, LocalDate.now()).getYears();
    }
}
