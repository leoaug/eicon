package br.com.eicon.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.eicon.bean.PedidoResponse;
import br.com.eicon.bean.PedidoBean;
import br.com.eicon.exception.EiconException;
import br.com.eicon.model.Cliente;
import br.com.eicon.service.ClienteService;
import br.com.eicon.service.PedidoService;
import javax.xml.bind.JAXBException;

@RestController
@RequestMapping("/api/xml/pedido/")
public class PedidoXmlResource {

	@Autowired
	private PedidoService pedidoService;
	
	@Autowired
	private ClienteService clienteService;
	
	@GetMapping(produces = "application/xml", consumes = "application/xml")
	public ResponseEntity<?> consultarPedidosPorCliente(@RequestParam Long idCliente){
		
		return ResponseEntity.ok("ok");
	}
	
	@PostMapping(value = "salvarPedidos/{idCliente}", consumes = "application/xml")
	public ResponseEntity<?> salvarPedidos(@PathVariable Long idCliente,@RequestBody String pedidosXml)
			throws JsonMappingException, JsonProcessingException, JAXBException {

		Cliente cliente = clienteService.getEntidade(idCliente);
		if( cliente == null) {
			return new ResponseEntity<>(new EiconException("Usuário inexistente para fazer o pedido,informe um válido"),
					HttpStatus.BAD_REQUEST);
		} else {

			XmlMapper xmlMapper = new XmlMapper();

			List<PedidoBean> pedidos = xmlMapper.readValue(pedidosXml, new TypeReference<List<PedidoBean>>() {
			});

			PedidoResponse response = pedidoService.validarDadosPedidos(pedidos);
			
			if (response.isValido()) {
				
				pedidoService.salvarPedidos(cliente,pedidos);
				
				return ResponseEntity.ok(pedidosXml);				
			}  else {
				 return new ResponseEntity<>(new EiconException(response.getMensagem()),
							HttpStatus.BAD_REQUEST);
			}
		}

	}

}
