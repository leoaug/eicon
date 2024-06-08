package br.com.eicon.service;

import org.springframework.stereotype.Service;

import br.com.eicon.model.Cliente;
import br.com.eicon.repository.EiconRepository;

@Service
public class ClienteService extends EiconRepository <Cliente> {

	private static final long serialVersionUID = 1L;

}
