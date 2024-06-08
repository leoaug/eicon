package br.com.eicon.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.eicon.model.Produto;

public class ProdutoXmlResource {

	@PostMapping(consumes = "application/xml", produces = "application/xml")
	public ResponseEntity<Produto> salvarProduto(@RequestBody Produto produto) {
		
		return ResponseEntity.ok(produto);
	}
}
