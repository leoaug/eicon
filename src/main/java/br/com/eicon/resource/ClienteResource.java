package br.com.eicon.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.eicon.model.Cliente;
import br.com.eicon.service.ClienteService;

@RestController
@RequestMapping("/api/json/cliente/")
public class ClienteResource {

	@Autowired
	private ClienteService clienteService;
	
	@PostMapping
	public ResponseEntity<?> salvarPedidos(@RequestBody String clienteString) throws JsonMappingException, JsonProcessingException{		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		Cliente cliente = jsonMapper.readValue(clienteString,Cliente.class);
		
		return ResponseEntity.ok(clienteService.salvar(cliente));
	}
}
