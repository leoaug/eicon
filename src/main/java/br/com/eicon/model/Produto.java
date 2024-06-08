package br.com.eicon.model;

import java.io.Serializable;
import java.util.List;

import br.com.eicon.constants.Constantes;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PRODUTO", schema = Constantes.EICON_SCHEMA)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Produto implements Serializable {

	private static final long serialVersionUID = 1L;
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PRODUTO", nullable = false)
	private Long id;
	
	@Column(name = "NOME_PRODUTO")
	private String nome;
	
	@Column(name = "VALOR_PRODUTO")
	private Double valor;
	
	@Column(name = "QTDE_PRODUTO")
	private Integer quantidade;
	
	@OneToMany(mappedBy = "produto",  fetch = FetchType.LAZY)
	@JsonIgnore
	private List <Pedido> listaPedidos;

}
