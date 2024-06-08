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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.eicon.bean.Pedidos;
import br.com.eicon.exception.EiconException;
import jakarta.xml.bind.JAXBException;

@RestController
@RequestMapping("/api/xml/pedido/")
public class PedidoXmlResource {

	@GetMapping("teste")
	public ResponseEntity<?> teste() {
		return ResponseEntity.ok("Ok");
	}

	@PostMapping(path = "salvarPedidos", produces = "application/xml", consumes = "application/xml")
	public ResponseEntity<?> salvarPedidos(@RequestBody String pedidosXml)
			throws JsonMappingException, JsonProcessingException, JAXBException {

		XmlMapper xmlMapper = new XmlMapper();

		List<Pedidos> pedidosWrapper = xmlMapper.readValue(pedidosXml, new TypeReference<List<Pedidos>>() {
		});
	
		
		if(pedidosWrapper.size() > 10) {
			 return new ResponseEntity<>(new EiconException("Quantidade Máxima excedida de pedidos, máximo 10"),
					 HttpStatus.BAD_REQUEST);
		} else {
			return ResponseEntity.ok(pedidosWrapper);

		}

	}

}
