package br.com.eicon.model;

import java.io.Serializable;

import br.com.eicon.constants.Constantes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
public class Pedido implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PEDIDO", nullable = false)
	private Long id;
	
	@Column(name = "NUMERO_CONTROLE")
	private Integer numeroControle;
	
	@ManyToOne
	@JoinColumn(name = "ID_CLIENTE", referencedColumnName = "ID_CLIENTE")
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "ID_PRODUTO", referencedColumnName = "ID_PRODUTO")
	private Produto produto;
	
}

