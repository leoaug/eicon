package br.com.eicon.resource;

import java.util.List;

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

import br.com.eicon.model.Pedido;

@RestController
@RequestMapping("/api/xml/pedido/")
public class PedidoXmlResource {

	@GetMapping("teste")
	public ResponseEntity<?> teste (){
		return ResponseEntity.ok("Ok");
	}
	
	@PostMapping(path="salvarPedidos")
	public ResponseEntity<?> salvarPedidos(@RequestBody String pedidosXml) throws JsonMappingException, JsonProcessingException {
      
		XmlMapper xmlMapper = new XmlMapper();
        
        List<Pedido> pedidosWrapper = xmlMapper.readValue(pedidosXml, new TypeReference<List<Pedido>>() {});
        //PedidosWrapper pedidosWrapper = xmlMapper.readValue(pedidosXml, PedidosWrapper.class);


        // Access the list of pedidos
        //List<Pedido> pedidos = pedidosWrapper.getPedidos();
		
		return ResponseEntity.ok(pedidosWrapper);
	}
	
	
}
