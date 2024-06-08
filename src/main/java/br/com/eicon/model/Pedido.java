package br.com.eicon.model;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import br.com.eicon.constants.Constantes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PEDIDO", schema = Constantes.EICON_SCHEMA)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@JacksonXmlRootElement(localName = "pedido")
public class Pedido implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PEDIDO", nullable = false)
	@JacksonXmlProperty(localName = "id")
	private Long id;
	
	@Column(name = "NUMERO_CONTROLE")
	@JacksonXmlProperty(localName = "numeroControle")
	private Integer numeroControle;
	
	@ManyToOne
	@JoinColumn(name = "ID_CLIENTE", referencedColumnName = "ID_CLIENTE")
	@JacksonXmlProperty(localName = "cliente")
	private Cliente cliente;
	 
	@ManyToOne
	@JoinColumn(name = "ID_PRODUTO", referencedColumnName = "ID_PRODUTO")
	@JacksonXmlProperty(localName = "produto")
	private Produto produto;
	
}

