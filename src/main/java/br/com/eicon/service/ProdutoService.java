package br.com.eicon.service;

import org.springframework.stereotype.Service;

import br.com.eicon.model.Produto;
import br.com.eicon.repository.EiconRepository;

@Service
public class ProdutoService extends EiconRepository <Produto> {

	private static final long serialVersionUID = 1L;

}
