package br.com.eicon.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pedidos {
	
	 private Integer numeroControle;
	 
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
     private Date dataCadastro;
	 
     private String nomeProduto;
     private Double valor;
     private Integer quantidade;
     private Integer codigoCliente;
}
