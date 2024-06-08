package br.com.eicon.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.eicon.bean.PedidoResponse;
import br.com.eicon.constants.Constantes;
import br.com.eicon.jpa.EICONQuery;
import br.com.eicon.bean.PedidoBean;
import br.com.eicon.model.Cliente;
import br.com.eicon.model.Pedido;
import br.com.eicon.model.Produto;
import br.com.eicon.repository.EiconRepository;

@Service
public class PedidoService extends EiconRepository <Pedido> {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ProdutoService produtoService;

	public PedidoResponse validarDadosPedidos(List<PedidoBean> pedidos) {
		PedidoResponse response = new PedidoResponse();
		response.setValido(true);
		
		if(this.consultarPedidosPeloNumeroControle(pedidos)) {
			response.setValido(false);
			response.setMensagem("Número de controle já existe");
		}
		
		if(pedidos.size() > 10) {
			response.setValido(false);
			response.setMensagem("Quantidade Máxima excedida de pedidos, máximo 10");
		}
		
		return response;
	}

	private boolean consultarPedidosPeloNumeroControle(List<PedidoBean> pedidos) {
		for(PedidoBean pedido : pedidos) {
			EICONQuery<Pedido> query = super.inicializaEICONQuery();
			query.adicionarFiltro("numeroControle", pedido.getNumeroControle(), Constantes.OPERACAO_IGUAL);
			if(super.consultarPorFiltro(query) != null) {
				return true;
			}
		}		
		return false;
	}

	public List <Pedido>  salvarPedidos(Cliente cliente, List<PedidoBean> pedidos) {
		
		List <Pedido> listaPEdidosParaSalvar = new ArrayList<>();
		
		Date dataHoje = new Date(System.currentTimeMillis());
		
		for(PedidoBean pedidoBean : pedidos) {
			Pedido pedido = new Pedido();
			pedido.setCliente(cliente);
			pedido.setDataCadastro(pedidoBean.getDataCadastro() == null ? dataHoje : pedidoBean.getDataCadastro());
			pedido.setNumeroControle(pedidoBean.getNumeroControle());
			
			Produto produto = new Produto();
			produto.setQuantidade(pedidoBean.getQuantidade() == null ? 1 : pedidoBean.getQuantidade());
			produto.setValor(this.calcularValorPelaQuantidade(produto.getQuantidade(),pedidoBean.getValor()) * produto.getQuantidade());
			produto.setNome(pedidoBean.getNomeProduto());
			pedido.setProduto(produtoService.salvar(produto));
			listaPEdidosParaSalvar.add(pedido);
		}
		
		return super.salvarEntidades(listaPEdidosParaSalvar);
		
		
	}

	private Double calcularValorPelaQuantidade(Integer quantidade, Double valor) {
		return quantidade > 5 ? 0.95 * valor : quantidade > 10 ? 0.9 * valor : valor;
	}

	public List <Pedido> consultarPedidosPorCliente(Long idCliente) {
		EICONQuery<Pedido> query = super.inicializaEICONQuery();
		query.adicionarFiltro("cliente.id", idCliente, Constantes.OPERACAO_IGUAL);
		return super.consultarPorFiltroList(query);
	}

}
