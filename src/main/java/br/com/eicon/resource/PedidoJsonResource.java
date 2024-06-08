package br.com.eicon.resource;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.eicon.bean.Pedidos;
import br.com.eicon.exception.EiconException;
import jakarta.xml.bind.JAXBException;

@RestController
@RequestMapping("/api/json/pedido/")
public class PedidoJsonResource {

	@GetMapping("teste")
	public ResponseEntity<?> teste() {
		return ResponseEntity.ok("Ok");
	}

	@PostMapping(value = "salvarPedidos", produces = "application/json", consumes = "application/json")
	public ResponseEntity<?> salvarPedidos(@RequestBody String pedidosJson)
			throws JsonMappingException, JsonProcessingException, JAXBException {

		ObjectMapper jsonMapper = new ObjectMapper();
		
		List<Pedidos> pedidosWrapper = jsonMapper.readValue(pedidosJson, new TypeReference<List<Pedidos>>() {
		});
	
		
		if(pedidosWrapper.size() > 10) {
			 return new ResponseEntity<>(new EiconException("Quantidade Máxima excedida de pedidos, máximo 10"),
					 HttpStatus.BAD_REQUEST);
		} else {
			return ResponseEntity.ok(pedidosJson);

		}

	}

}
