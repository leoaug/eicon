package br.com.eicon.model;

import java.io.Serializable;
import java.util.List;

import br.com.eicon.constants.Constantes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
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
@XmlRootElement(name = "Produto")
@XmlType(propOrder = { "id", "nome", "valor", "quantidade" })
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
	/*
	@OneToMany(mappedBy = "produto",  fetch = FetchType.LAZY)
	@XmlTransient
	private List <Pedido> listaPedidos;
*/
}
