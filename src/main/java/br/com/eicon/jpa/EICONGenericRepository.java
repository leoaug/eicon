package br.com.eicon.jpa;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.criteria.Predicate; 


@EnableTransactionManagement
@Repository
public abstract class EICONGenericRepository <T extends Serializable> extends BBTSGenericRepositoryImpl<T> {

	

	private EICONQuery<T> eiconQuery = new EICONQuery <> ();
	
	@SuppressWarnings("rawtypes")
	private Class clazzForName;

	public List <T> consultarPorEntidadeList(T entidade)  {
		return super.consultarPorEntidadeList(entidade, inicializaEICONQuery());
	}
		
	public List <T> consultarPorParametrosANDList(EICONQuery<T> EICONQuery)  {									
		Predicate[] preds = EICONQuery.getPredicadosFilter().toArray(new Predicate[EICONQuery.getPredicadosFilter().size()]);	
		EICONQuery.adicionarQuery(EICONQuery.agruparFiltroAND(preds));					
		return consultarPorFiltroList(EICONQuery);		
	}
	public Set <T> consultarPorParametrosANDSet(EICONQuery<T> EICONQuery)  {									
		Predicate[] preds = EICONQuery.getPredicadosFilter().toArray(new Predicate[EICONQuery.getPredicadosFilter().size()]);	
		EICONQuery.adicionarQuery(EICONQuery.agruparFiltroAND(preds));					
		return consultarPorFiltroSet(EICONQuery);		
	}
	
	public T consultarPorParametrosAND(EICONQuery<T> EICONQuery)  {									
		Predicate[] preds = EICONQuery.getPredicadosFilter().toArray(new Predicate[EICONQuery.getPredicadosFilter().size()]);	
		EICONQuery.adicionarQuery(EICONQuery.agruparFiltroAND(preds));					
		return consultarPorFiltro(EICONQuery);		
	}

	public List <T> consultarPorParametrosANDIntervaloList(EICONQuery<T> EICONQuery,int inicio, int fim)  {									
		Predicate[] preds = EICONQuery.getPredicadosFilter().toArray(new Predicate[EICONQuery.getPredicadosFilter().size()]);	
		EICONQuery.adicionarQuery(EICONQuery.agruparFiltroAND(preds));					
		return consultarPorFiltroListIntervaloList(EICONQuery, inicio, fim);		
	}
	
	public T consultarPorParametrosANDIntervalo(EICONQuery<T> EICONQuery,int inicio, int fim)  {									
		Predicate[] preds = EICONQuery.getPredicadosFilter().toArray(new Predicate[EICONQuery.getPredicadosFilter().size()]);	
		EICONQuery.adicionarQuery(EICONQuery.agruparFiltroAND(preds));					
		return consultarPorFiltroListIntervalo(EICONQuery, inicio, fim);		
	}
	
	
	public Long consultarPorParametrosANDCOUNT(EICONQuery<T> EICONQuery)  {
		try {												
			Predicate[] preds = EICONQuery.getPredicadosFilter().toArray(new Predicate[EICONQuery.getPredicadosFilter().size()]);	
			EICONQuery.adicionarQuery(EICONQuery.agruparFiltroAND(preds));				
			return this.consultarPorCriteriaCOUNT(EICONQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List <T> consultarPorParametrosORList(EICONQuery<T> EICONQuery)  {				
		Predicate[] preds = EICONQuery.getPredicadosFilter().toArray(new Predicate[EICONQuery.getPredicadosFilter().size()]);	
		EICONQuery.adicionarQuery(EICONQuery.agruparFiltroOR(preds));				
		return consultarPorFiltroList(EICONQuery);		
	}
	
	public T consultarPorParametrosOR(EICONQuery<T> EICONQuery)  {				
		Predicate[] preds = EICONQuery.getPredicadosFilter().toArray(new Predicate[EICONQuery.getPredicadosFilter().size()]);	
		EICONQuery.adicionarQuery(EICONQuery.agruparFiltroOR(preds));				
		return consultarPorFiltro(EICONQuery);		
	}
	
	public T getEntidade(Serializable id)  {
		return super.getEntidade(id, clazzForName);
	}
	
	public List <T> getEntidades() {		
		return super.getEntidades(clazzForName);
	}
	
	public List <T> getEntidades(List <String> orders)  {
		return super.getEntidades(clazzForName,orders);
	}
	public List <T> getEntidades(int first ,int pageSize)  {
		try {
			return super.getEntidades(clazzForName,first,pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}	
	public Long  getEntidadesCOUNT()  {
		return super.getEntidadesCOUNT(clazzForName);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public EICONQuery<T> inicializaEICONQuery()  {
		try {		
			if(eiconQuery != null && clazzForName != null) {
				eiconQuery.setBbtsGenericDAOImpl(super.getBbtsGenericDAOImpl());		
				eiconQuery.acidionarConsultaEntidadeBase(clazzForName);
				return eiconQuery;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eiconQuery;
	}	
	@SuppressWarnings("rawtypes")
	public void setClazzForName(Class clazzForName) {
		this.clazzForName = clazzForName;
	}	
}
