package br.com.eicon.repository;

import java.io.Serializable;

import org.springframework.transaction.annotation.Transactional;

import br.com.eicon.constants.Constantes;
import br.com.eicon.jpa.EICONClassUtil;
import br.com.eicon.jpa.EICONGenericRepository;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Transactional( value = Constantes.TRANSACTION_MANAGER_EICON)
public class EiconRepository <T extends Serializable> extends EICONGenericRepository <T> implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = Constantes.PERSISTENCE_UNIT_EICON)
	private EntityManager entityManager;
	
	@PostConstruct
	public void init() throws ClassNotFoundException {
		super.setEntityManager(this.entityManager);
		super.setClazzForName (Class.forName(EICONClassUtil.
				getCanonicalNamePorTipoGenericsSadc(this.getClass(), Constantes.PACKAGE_TO_SCAN_EICON)));	

	}


}
