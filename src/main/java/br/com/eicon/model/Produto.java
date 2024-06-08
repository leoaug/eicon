package br.com.eicon.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import br.com.eicon.constants.Constantes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @JacksonXmlProperty(localName = "id")
	private Long id;
	
	@Column(name = "NOME_PRODUTO")
	@JacksonXmlProperty(localName = "nome")
	private String nome;
	
	@Column(name = "VALOR_PRODUTO")
	@JacksonXmlProperty(localName = "valor")
	private Double valor;
	
	@Column(name = "QTDE_PRODUTO")
	@JacksonXmlProperty(localName = "quantidade")
	private Integer quantidade;
	
	@OneToMany(mappedBy = "produto",  fetch = FetchType.LAZY)
	private List <Pedido> listaPedidos;

}
