package br.com.eicon.resource;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.eicon.model.Pedido;

@RestController
@RequestMapping("/api/xml/pedido")
public class PedidoXmlResource {

	@PostMapping(consumes = "application/xml", produces = "application/xml")
	public ResponseEntity<List<Pedido>> salvarPedidos(@RequestBody List<Pedido> pedidos) {
		
		return ResponseEntity.ok(pedidos);
	}
	
	@PostMapping(value = "/api/users", consumes = "application/xml", produces = "application/xml")
    public ResponseEntity<List<Pedido>> recuperarPedidos(@RequestBody List<Pedido> pedidos) {
        // Process the user object as needed
        // For demonstration, we just return the received user object
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidos);
    }
}
