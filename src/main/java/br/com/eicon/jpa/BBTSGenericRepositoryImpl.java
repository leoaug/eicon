package br.com.eicon.jpa;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import br.com.eicon.constants.Constantes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.transaction.Transactional;



/**
 * @author Leonardo Silva
 * @version 3.0
 * @param <T> generics do java para fazer cast com as Entidades
 */
public abstract class BBTSGenericRepositoryImpl <T extends Serializable> implements EICONInterfaceGenericRepository<T>  {

	private EntityManager em;
	
	private CriteriaBuilder cb;
	
	private CriteriaQuery <T> criteriaQuery;
	
	private Root<T> root;
	
	private Path<T> path;
		
	private static final String GENERETADED_ALIAS = "generatedAlias0";
	
	private static final String SELECT_TABLE_ENTIDADE = "SELECT tableEntidade FROM ";
	
	private static final String TABLE_ENTIDADE = " tableEntidade ";
	
	
	@Override
	/**
	 * @param entidade generica para ser persistido
	 * @return entidade persistida
	 */
	@Transactional
	public T salvar(T bean) {		
		try {	
			em.persist(bean);
		} catch (Exception e) {
			e.printStackTrace();
		
			throw e;
		}
		return bean;
	}
	
	
	
	/**
	 * @param lista de entidades genericas para ser persistido
	 * @return Lista Genérica de entidades persistidas
	 */
	@Transactional
	protected List<T> salvarEntidades(List<T> beans)  {		
		try {
			for(Serializable bean : beans){
				em.persist(bean);
			}		
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return beans;
	}
	
	
	@Override
	/**
	 * @param entidade generica para ser alterada
	 * @return entidade alterada
	 */
	@Transactional
	public T alterar(T bean)  {
		try {			
			em.merge(bean);			
		} catch (Exception e) {	
			e.printStackTrace();			
			throw e;
		} 		
		return bean;
	}
	
	@Override
	/**
	 * @param entidade generica para ser atualizada no contexto da sessão
	 * @return entidade atualizada
	 */
	@Transactional
	public T atualizar(T bean)  {
		try {			
			em.refresh(bean);			
		} catch (Exception e) {		
			e.printStackTrace();		
			throw e;
		} 		
		return bean;
	} 

	@Override
	/**
	 * @param entidade generica para ser excluída
	 */
	@Transactional
	public void excluir(T bean)  {
		try {		
			bean = em.merge(bean);
			em.remove(bean);			
		} catch (Exception e) {
			e.printStackTrace();			
			throw e;
		} 	
	}
	
	@Override
	/**
	 * @param Lista entidades genericas para serem excluídas
	 */
	@Transactional
	public void excluirEntidades(List<T> beans) {
		try {		
			if(beans != null) {
				for(T bean : beans){
					bean = em.merge(bean);
					em.flush();
					em.remove(bean);	
					em.flush();
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();		
			throw e;
		} 
	}
	

	@SuppressWarnings("unchecked")
	/**
	 * @param objeto tipo Class da entidade
	 * @return Lista de todas as entidades do referido Class
	 */
	protected List <T> getEntidades(Class <?> clazz){
		if(clazz != null && clazz.getCanonicalName() != null && em.createQuery(SELECT_TABLE_ENTIDADE+clazz.getCanonicalName()+TABLE_ENTIDADE) != null) {
			return em.createQuery(SELECT_TABLE_ENTIDADE+clazz.getCanonicalName()+TABLE_ENTIDADE).getResultList();
		} else {
			return Collections.emptyList();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	/**
	 * @param class da entidade para consultar
	 * @param List <String> de campos pra orders, Ex nome,chave
	 * @return todas entidades realcionadas com o tipo de class da entidade
	 */
	protected List <T> getEntidades(Class <?> clazz,List <String> orders){
		String orderby = "";
		
		if(orders != null && !orders.isEmpty()){
			StringBuilder builder = new StringBuilder();
			builder.append("ORDER BY ");
			String virgula = ",";
			int i = 0;
			
			for(String order : orders){
				if(i == orders.size() - 1){
					virgula = "";
				}
				
				builder.append(order + virgula);
				
				i++;
			}
			orderby = builder.toString();
		}
		if(clazz != null && clazz.getCanonicalName() != null && em.createQuery(SELECT_TABLE_ENTIDADE+ clazz.getCanonicalName()+TABLE_ENTIDADE) != null) {
			return em.createQuery(SELECT_TABLE_ENTIDADE+clazz.getCanonicalName()+TABLE_ENTIDADE + orderby).getResultList();
		} else {
			return Collections.emptyList();
		} 
	}
	

	
	@SuppressWarnings("unchecked")
	/**
	 * @param class da entidade para consultar
	 * @param first primeira indice para consultar o intervalo (equivalente ao limit) Exemplo (limit 0,5)
	 * @param pageSize tamanho do retorno de registros (equivalente ao limit) Exemplo (limit 0,5)
	 * @return todas entidades relacionadas com o tipo de class da entidade paginadas pelo tamanho limit (first,pageSize)
	 */
	protected List <T> getEntidades(Class <?> clazz,int first ,int pageSize)  {
		try {
			Query query = em.createQuery(SELECT_TABLE_ENTIDADE+clazz.getCanonicalName()+TABLE_ENTIDADE);
			
			query = montarPaginacaoQuery(query,first,pageSize);
					
			if(query != null) {
				return query.getResultList();
			} else {
				return Collections.emptyList();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	

	private Query montarPaginacaoQuery(Query query, int first, int pageSize) {
		try {
			if(query != null) {
				if(first == 0) {
					query.setFirstResult(0); 
				} else {
					query.setFirstResult(first - 1); 
				}			
				query.setMaxResults(pageSize);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return query;
	}



	protected Long getEntidadesCOUNT(Class <?> clazz){
		return (Long) em.createQuery("SELECT COUNT(tableEntidade) FROM "+clazz.getCanonicalName()+TABLE_ENTIDADE).getSingleResult();
	}
	
	
	@SuppressWarnings("unchecked")
	protected T getEntidade(Serializable pk, Class<?> clazz)  {
		return (T) em.find(clazz, pk);
	}

	@SuppressWarnings("unchecked")
	public List<T> consultarPorNamedQueryList(EICONQuery<T> EICONQuery)  {
	
		Query namedQuery = null;
		List <T> lista = null;
		try {
		
			namedQuery = em.createNamedQuery(EICONQuery.getNomeConsultaNamedQuery());
	
			lista = montarConsultarPorNamedQueryList(EICONQuery,namedQuery);
			
		} catch (Exception e) {	
			e.printStackTrace();
		}
		
		return lista;
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List montarConsultarPorNamedQueryList(EICONQuery<T> EICONQuery,Query namedQuery) {
		List <T> lista = null;
		
		try {
			
			if(EICONQuery.getFiltrosNamedQuery() == null && EICONQuery.getTipoClausulaNamedQuery() == null){

				lista = namedQuery.getResultList();

			} else {

				Set<String> keys = EICONQuery.getFiltrosNamedQuery().keySet();				
				for (String key : keys) {				
					namedQuery.setParameter(key, EICONQuery.getFiltrosNamedQuery().get(key));									
				}
				
				lista = namedQuery.getResultList();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}



	@SuppressWarnings({ "unchecked"})
	public List<T> consultarPorQueryHQLList(EICONQuery<T> EICONQuery,String query)  {
	
		Query namedQuery = null;
		List <T> lista = null;
		try {
		
			namedQuery = em.createQuery(query);
	
			lista = montarConsultarPorNamedQueryList(EICONQuery,namedQuery);
		} catch (Exception e) {	
			e.printStackTrace();
		}
		
		return lista;
	}
	
	public Long consultarPorQueryHQLCOUNT(EICONQuery<T> EICONQuery,String query)  {
	
		Query namedQuery = null;
		try {
			
			namedQuery = em.createQuery(query);
	
			if(EICONQuery.getFiltrosNamedQuery() == null && EICONQuery.getTipoClausulaNamedQuery() == null){

				return (Long) namedQuery.getSingleResult();

			} else {

				Set<String> keys = EICONQuery.getFiltrosNamedQuery().keySet();				
				for (String key : keys) {				
					namedQuery.setParameter(key, EICONQuery.getFiltrosNamedQuery().get(key));									
				}
				
				return (Long)namedQuery.getSingleResult();
				
			}
		} catch (Exception e) {	
			e.printStackTrace();
		}
		return null;
		
	}

	@SuppressWarnings("unchecked")
	public List<T> consultarPorQueryHQLIntervaloList(EICONQuery<T> EICONQuery,String query,int first,int pageSize)  {
	
		Query namedQuery = null;
		List <T> lista = null;
		try {
		
			namedQuery = em.createQuery(query);
			
			namedQuery = montarPaginacaoQuery(namedQuery,first,pageSize);
	
			lista = montarConsultarPorNamedQueryList(EICONQuery,namedQuery);
		} catch (Exception e) {	
			e.printStackTrace();
		}
		
		return lista;
	}
	

	@SuppressWarnings("unchecked")
	public List<Object[]> consultarPorQueryHQLIntervaloListObjects(EICONQuery<T> EICONQuery,String query,int first,int pageSize)  {
	
		Query namedQuery = null;
		List <Object[]> lista = null;
		try {
		
			namedQuery = em.createQuery(query,Object[].class);
			
			namedQuery = montarPaginacaoQuery(namedQuery,first,pageSize);
	
			lista = montarConsultarPorNamedQueryList(EICONQuery,namedQuery);
		} catch (Exception e) {	
			e.printStackTrace();
		}
		
		return lista;
	}
	

	@SuppressWarnings("unchecked")
	protected List<Serializable> consultarPorNamedQueryListMaxResults(EICONQuery<T> EICONQuery,int maxResult)  {
	
		Query namedQuery = null;
		List <Serializable> lista = null;
		try {
		
			namedQuery = em.createNamedQuery(EICONQuery.getNomeConsultaNamedQuery());
			namedQuery.setMaxResults(maxResult);
			
			lista = montarConsultarPorNamedQueryList(EICONQuery,namedQuery);
		} catch (Exception e) {	
			e.printStackTrace();
			throw e;
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	protected List<Serializable> consultarIntervalPorNamedQueryList(EICONQuery<T> EICONQuery,int first,int pageSize)  {
	
		Query namedQuery = null;
		List <Serializable> lista = null;
		try {
		
			namedQuery = em.createNamedQuery(EICONQuery.getNomeConsultaNamedQuery());
	
			namedQuery = montarPaginacaoQuery(namedQuery,first,pageSize);
			
			lista = montarConsultarPorNamedQueryList(EICONQuery,namedQuery);
			
		} catch (Exception e) {	
			e.printStackTrace();
			throw e;
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public T consultarPorNamedQuery(EICONQuery<T> EICONQuery)  {
	
		Query namedQuery = null;
		List <T> lista = null;
		try {
	
			namedQuery = em.createNamedQuery(EICONQuery.getNomeConsultaNamedQuery());
				
			if(EICONQuery.getFiltrosNamedQuery() == null && EICONQuery.getTipoClausulaNamedQuery() == null){

				lista = namedQuery.getResultList();
				
				return retornarListaTernario(lista);
				
			} else {

				Set<String> keys = EICONQuery.getFiltrosNamedQuery().keySet();				
				for (String key : keys) {				
					namedQuery.setParameter(key, EICONQuery.getFiltrosNamedQuery().get(key));									
				}
	
				lista = namedQuery.getResultList();
				
				return retornarListaTernario(lista);
			}
		} catch (Exception e) {	
			e.printStackTrace();
			
			throw e;
		} 
	}

	
	@SuppressWarnings("unchecked")
	protected List <Object[]> consultarPorNativeQueryList(String sql)  {
		
		Query query = null;	
		List <Object[]> lista = null;
		try {
			query = em.createNativeQuery(sql);
			if(query != null) {
				lista = query.getResultList();
			}
		} catch(Exception e) {
			e.printStackTrace();

			throw e;
		} 
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	protected List <Object[]> consultarPorNativeQueryIntervaloList(String sql,int first,int pageSize)  {
		
		Query query = null;	
		List <Object[]> lista = null;
		try {
			query = em.createNativeQuery(sql);	
			if(query != null) {
				query = montarPaginacaoQuery(query,first,pageSize);
			
				lista = query.getResultList();
			} else {
				lista = Collections.emptyList();
			}
		} catch(Exception e) {
			e.printStackTrace();

			throw e;
		} 
		return lista;
	}
	
	
	protected Object consultarPorNativeQuery(String sql)  {
		
		Query query = null;	
		try {
			query = em.createNativeQuery(sql);	
			if(query != null) {
				return query.getSingleResult();
			} else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();

			throw e;
		} 
		
	}
	
	@SuppressWarnings("unchecked")
	protected List <Object[]> consultarPorNativeQueryList(String sql,List <String> params)  {
		
		Query query = null;	
		List <Object[]> lista = null;
		try {
			query = em.createNativeQuery(sql);	
			int i = 1;
			for(String param : params) {
				query.setParameter(i, param);
				i++;
			}
			lista = query.getResultList();
			
		} catch(Exception e) {
			e.printStackTrace();

			throw e;
		} 
		return lista;
	}
	

	protected void executarComandoSQL(String sql)  {
		
	
		try {
			
			 
			Query query =  em.createNativeQuery(sql);		
			query.executeUpdate();
		
			
			
		} catch(Exception e) {
			e.printStackTrace();

			throw e;
		} 
	}
	
	protected int executarComandoSQLRetornoLinhas(String sql)  {
		int linhas = 0;
		
		try {
			
			 
			Query query =  em.createNativeQuery(sql);		
			linhas = query.executeUpdate();
			
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} 
		return linhas;
	}

	
	private void executarProcedureConn(Connection conn,String sql) {
		try (CallableStatement callable = conn.prepareCall(sql)) {
			callable.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();

		}
		
	}


	public Long consultarPorFiltroCOUNT(EICONQuery<T> EICONQuery)  {
		try {									
			return this.consultarPorCriteriaCOUNT(EICONQuery);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	protected List <T> consultarPorEntidadeList(T entidade,EICONQuery<T> EICONQuery)   {
		
		try {
			
			T entidadeAux = entidade;
			
			
			Set<SingularAttribute<T, ?>>  attrs =	EICONQuery.criarRootDeCanonicalPath((Class<T>) entidade.getClass()).getModel().getDeclaredSingularAttributes();
		
			List <String> atributos = new ArrayList<>();
			for (SingularAttribute<T, ?> attr : attrs) {
				atributos.add(attr.getName());
			}
			
			Map <String,Object> params = new EICONObjetoUtil <T> ().varrerAtributosObjeto(entidade, 
																					      entidadeAux ,
																					     new StringBuilder(),
																					     new HashMap <>(),
																					     atributos,
																					     0,
																					     true);
			
			List <Predicate> predicados = new ArrayList<>();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				predicados.add(EICONQuery.adicionarFiltro(param.getKey(), param.getValue(), Constantes.OPERACAO_IGUAL));	
			}
		
			EICONQuery.adicionarQuery(EICONQuery.agruparFiltroAND(predicados.toArray(new Predicate[]{})));
			
			return consultarPorFiltroList(EICONQuery);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
		
	}
	
	
	//@Override
	/**
	 * @return T entidade
	 * @param EICONQuery @see EICONQuery 
	 */
	public T consultarPorFiltro(EICONQuery <T> EICONQuery)  {
		try {					
			return this.consultarPorCriteria(EICONQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * @return List<T> entidades
	 * @param EICONQuery @see EICONQuery 
	 */
	public List<T> consultarPorFiltroList(EICONQuery<T> EICONQuery)  {
		try {			
					
			Map <Path<Object>, Boolean> mapPathOrderBY = this.montarOrderBy(EICONQuery);		
			
			return (List<T>) this.consultarPorCriteriaList(EICONQuery,mapPathOrderBY);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	/**
	 * @return Set<T> entidades
	 * @param EICONQuery @see EICONQuery 
	 */
	@SuppressWarnings("unchecked")
	public Set<T> consultarPorFiltroSet(EICONQuery<T> EICONQuery)  {
		try {			
					
			Map <Path<Object>, Boolean> mapPathOrderBY = this.montarOrderBy(EICONQuery);		
			
			return (Set<T>) this.consultarPorCriteriaSet(EICONQuery,mapPathOrderBY);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptySet();
	}
	

	@SuppressWarnings("unchecked")
	/**
	 * @return List<T> entidades
	 * @param EICONQuery @see EICONQuery 
	 * @param first primeiro índice do intervalo
	 * @param pageSize total de quantos registros para retornar semelhante limit (0 (first),5 (pageSize))
	 */
	public List<T> consultarPorFiltroListIntervaloList(EICONQuery <T> EICONQuery,int first,int pageSize)  {
		try {			
		
			Map <Path<Object>, Boolean> mapPathOrderBY = this.montarOrderBy(EICONQuery);			
			
			return (List<T>) this.consultarPorCriteriaList(EICONQuery,mapPathOrderBY, first , pageSize);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	


	/**
	 * @return List<T> entidades
	 * @param EICONQuery @see EICONQuery 
	 * @param first primeiro índice do intervalo
	 * @param pageSize total de quantos registros para retornar semelhante limit (0 (first),5 (pageSize))
	 */
	public T consultarPorFiltroListIntervalo(EICONQuery <T> EICONQuery,int first,int pageSize) {
		try {			
		
			Map <Path<Object>, Boolean> mapPathOrderBY = this.montarOrderBy(EICONQuery);			
			
			return this.consultarPorCriteria(EICONQuery,mapPathOrderBY, first , pageSize);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * @return List<?> entidades com a sua contagem
	 * @param EICONQuery @see EICONQuery 
	 */
	@SuppressWarnings("unchecked")
	public List<T> consultarPorFiltroListCOUNT(EICONQuery <T> EICONQuery) {
		try {			
							
			Map <Path<Object>, Boolean> mapPathOrderBY = this.montarOrderBy(EICONQuery);
					
			return  (List<T>) this.consultarPorCriteriaListCOUNT(EICONQuery , mapPathOrderBY);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * @return  List<T> de entidades junto com o count (select count,entidade from entidade)
	 * @param EICONQuery parametors da consulta
	 * @param first primeiro indice do intervalo
	 * @param pageSize total de quantos registros para retornar semelhante limit (0 (first),5 (pageSize))
	 */
	public List<T> consultarPorFiltroListCOUNTInterval(EICONQuery <T> EICONQuery,int first)  {
		try {			
						
		
			Map <Path<Object>, Boolean> mapPathOrderBY = this.montarOrderBy(EICONQuery);
					
			return (List<T>) this.consultarPorCriteriaListCOUNTInterval(EICONQuery , mapPathOrderBY , first );
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	

	private Map <Path<Object>, Boolean> montarOrderBy(EICONQuery<T> EICONQuery)  {
		try {

			Map <Path<Object>, Boolean> listPathOrderBY = new LinkedHashMap<> ();
			
			for (Map.Entry<String, Boolean> filtroOrder : EICONQuery.getFiltroOrder().entrySet()) {
				
				Path<Object> pathOrder = EICONJpaUtil.getPath(EICONQuery.getRoot(), filtroOrder.getKey());
				listPathOrderBY.put(pathOrder,filtroOrder.getValue());
			}
			
			return listPathOrderBY;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}
	
	/**
	 * @return List<?> entidades com a sua contagem
	 * @param EICONQuery @see EICONQuery 
	 * @param mapPathOrderBY 
	 */
	private List <?> consultarPorCriteriaListCOUNT(EICONQuery <T> EICONQuery , Map <Path<Object>, Boolean> mapPathOrderBY) {
		
		try {
		
			criteriaQuery = montarMultiSelectCriteria(EICONQuery);
		
			if (criteriaQuery != null && !EICONQuery.getPredicados().isEmpty()) {

				
				Query query = criarQueryGroupByOrderBy(EICONQuery,criteriaQuery,mapPathOrderBY);
				
				return query == null ? null : query.getResultList();
				
			} 
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Collections.emptyList();
	}
	
	
	private Query criarQueryGroupByOrderBy(EICONQuery <T> EICONQuery,CriteriaQuery<T> criteriaQuery, Map<Path<Object>, Boolean> mapPathOrderBY) {
		try {
			

			criteriaQuery.where(EICONQuery.getPredicados().toArray(new Predicate[]{}));

			this.criarGroupBy(criteriaQuery,EICONQuery.getRoot(),EICONQuery);
			
			this.criarOrderBy(criteriaQuery,mapPathOrderBY);
							
			return em.createQuery(criteriaQuery);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}



	private List <?> consultarPorCriteriaListCOUNTInterval(EICONQuery <T> EICONQuery , Map <Path<Object>, Boolean> mapPathOrderBY ,int first) {
		
		List<?> lista =  null;
		try {
			
			criteriaQuery = montarMultiSelectCriteria(EICONQuery);
			
			if (criteriaQuery != null && !EICONQuery.getPredicados().isEmpty()) {

				Query query = criarQueryGroupByOrderBy(EICONQuery,criteriaQuery,mapPathOrderBY);
				
				if(query != null) {
					if(first == 0) {
						query.setFirstResult(0); 
					} else {
						query.setFirstResult(first - 1); 
					}

					lista = query.getResultList();
				}
			
				return  lista;
			} else {
				return lista;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	

	
	@SuppressWarnings("unchecked")
	private CriteriaQuery<T> montarMultiSelectCriteria(EICONQuery<T> EICONQuery) {
		
		try {
			
			criteriaQuery = getCriteriaQuery(this.getCriteriaBuilder());

			criteriaQuery.from(EICONQuery.getClazzCanonicalName());
		
			
			if(!EICONJpaUtil.isEclipseLink(criteriaQuery)){
				EICONQuery.getRoot().alias(GENERETADED_ALIAS);
			}
			
			criteriaQuery.multiselect(this.getCriteriaBuilder().count(EICONQuery.getRoot().get(EICONQuery.getCampoCOUNT())), EICONQuery.getRoot());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return criteriaQuery;
	}



	@SuppressWarnings("unchecked")
	private List <?> consultarPorCriteriaList(EICONQuery<T> EICONQuery, Map <Path<Object>, Boolean> mapPathOrderBY)  {
		
		List<?> lista =  null;
		try {
		
			criteriaQuery =  getCriteriaQuery(this.getCriteriaBuilder());

			criteriaQuery.from(EICONQuery.getClazzCanonicalName());
			if(EICONQuery.getDistinct().equals(Boolean.TRUE)){
				this.criarDistinct(criteriaQuery, EICONQuery.getRoot(), EICONQuery);				
			} else {
				criteriaQuery.select(EICONQuery.getRoot());
			}
			
			if(!EICONJpaUtil.isEclipseLink(criteriaQuery)){
				EICONQuery.getRoot().alias(GENERETADED_ALIAS);
			}
				
			criteriaQuery.where(EICONQuery.getPredicados().toArray(new Predicate[]{}));

			
			this.criarOrderBy(criteriaQuery, mapPathOrderBY);
			
			
			Query query = em.createQuery(criteriaQuery);
		
			
			lista = query == null ? Collections.emptyList() : query.getResultList();
			
			
			return  lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	private Set <?> consultarPorCriteriaSet(EICONQuery<T> EICONQuery, Map <Path<Object>, Boolean> mapPathOrderBY)  {
		
		try {
		
			return  Sets.newHashSet(this.consultarPorCriteriaList(EICONQuery,mapPathOrderBY));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptySet();	
	}

	@SuppressWarnings("unchecked")
	private List <?> consultarPorCriteriaList(EICONQuery<T> EICONQuery, Map <Path<Object>, Boolean> mapPathOrderBY,int first,int pageSize)  {
		
		List<?> lista =  null;
		try {
		
			criteriaQuery = getCriteriaQuery(this.getCriteriaBuilder());

			criteriaQuery.from(EICONQuery.getClazzCanonicalName());
			
			if(!EICONJpaUtil.isEclipseLink(criteriaQuery)){
				EICONQuery.getRoot().alias(GENERETADED_ALIAS);
			}
			
			

			criteriaQuery.select(EICONQuery.getRoot());
			criteriaQuery.where(EICONQuery.getPredicados().toArray(new Predicate[]{}));

			this.criarOrderBy(criteriaQuery,mapPathOrderBY);
			
			Query query = em.createQuery(criteriaQuery);
			
			query = montarPaginacaoQuery(query,first,pageSize);

			if(query != null) {
				lista = query.getResultList();
			} else {
				lista = Collections.emptyList();
			}
			
			return  lista;
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	@SuppressWarnings("unchecked")
	private T consultarPorCriteria(EICONQuery<T> EICONQuery, Map <Path<Object>, Boolean> mapPathOrderBY,int first,int pageSize)  {
		
		List<?> lista =  null;
		try {
		
			criteriaQuery = getCriteriaQuery(this.getCriteriaBuilder());

			criteriaQuery.from(EICONQuery.getClazzCanonicalName());
			
			if(!EICONJpaUtil.isEclipseLink(criteriaQuery)){
				EICONQuery.getRoot().alias(GENERETADED_ALIAS);
			}
			
			if (!EICONQuery.getPredicados().isEmpty()) {

				criteriaQuery.select(EICONQuery.getRoot());
				criteriaQuery.where(EICONQuery.getPredicados().toArray(new Predicate[]{}));

				this.criarOrderBy(criteriaQuery,mapPathOrderBY);
				
				Query query = em.createQuery(criteriaQuery);
				
				query = montarPaginacaoQuery(query,first,pageSize);
				
				if(query != null) {
					lista = query.getResultList();
					return retornarListaTernario(lista);
				} else {
					return null;
				}
				
			} else {
				return null;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	@SuppressWarnings("unchecked")
	private T retornarListaTernario(List<?> lista) {
		return !lista.isEmpty() ? (T) lista.get(0) : null;
	}



	@SuppressWarnings("unchecked")
	private T consultarPorCriteria(EICONQuery <T> EICONQuery)  {
		
		List<?> lista =  null;
		try {
		
			
			criteriaQuery =  getCriteriaQuery(this.getCriteriaBuilder());

			criteriaQuery.from(EICONQuery.getClazzCanonicalName());
			
			if(!EICONJpaUtil.isEclipseLink(criteriaQuery)){
				EICONQuery.getRoot().alias(GENERETADED_ALIAS);
			}
			
			if (!EICONQuery.getPredicados().isEmpty()) {

				criteriaQuery.select(EICONQuery.getRoot());
				criteriaQuery.where(EICONQuery.getPredicados().toArray(new Predicate[]{}));

				Query query = em.createQuery(criteriaQuery);
				if(EICONQuery.getMaxResult() != 0){
					query.setMaxResults(EICONQuery.getMaxResult());
				}
				lista = query == null ? Collections.emptyList() : query.getResultList();
				
				return retornarListaTernario(lista);
				
			} else {
				return null;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private void criarGroupBy(CriteriaQuery<?> criteriaQuery, Root<?> root ,EICONQuery<T> EICONQuery)  {
		try {
			List <Expression<?>> listaExpres = new ArrayList <>();
			for(String groupBy : EICONQuery.getGroupsBy()){
				listaExpres.add(root.get(groupBy));
			}
			if(!EICONQuery.getGroupsBy().isEmpty()){
				criteriaQuery.groupBy(listaExpres.toArray(new Expression[listaExpres.size()]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected void criarDistinct(CriteriaQuery<?> criteriaQuery, Root<?> root ,EICONQuery<T> EICONQuery)  {
		try {
			if(EICONQuery.getListDistincts() != null){
				for(String groupBy : EICONQuery.getListDistincts()){					
					criteriaQuery.select(root.get(groupBy)).distinct(EICONQuery.getDistinct());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void criarOrderBy(CriteriaQuery <?> criteriaQuery, Map <Path<Object>, Boolean> mapPathOrderBY)  {
		try {
			
			List <Order> orders = new ArrayList<>();
			
			for (Map.Entry<Path<Object>, Boolean> filtroOrder : mapPathOrderBY.entrySet()) {
				if(Boolean.TRUE.equals(filtroOrder.getValue())){
					orders.add(getCb().asc(filtroOrder.getKey()));	
				} else {
					orders.add(getCb().desc(filtroOrder.getKey()));	
				}
			}
			
			if(!orders.isEmpty()){
				criteriaQuery.orderBy(orders);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	
	
	public Long consultarPorCriteriaCOUNT(EICONQuery<T> EICONQuery)  {
	
		Long totalCriteriaCount = (long) 0 ;
		try {
		
			CriteriaQuery <Long> criteriaQueryLong = this.getCriteriaBuilder().createQuery(Long.class);

			if(!EICONJpaUtil.isEclipseLink(criteriaQueryLong)){
				EICONQuery.getRoot().alias(GENERETADED_ALIAS);
			}
			if (!EICONQuery.getPredicados().isEmpty()) {

				criteriaQueryLong.select(getCb().count(criteriaQueryLong.from(EICONQuery.getClazzCanonicalName())));	
				criteriaQueryLong.where(EICONQuery.getPredicados().toArray(new Predicate[]{}));		
				return em.createQuery(criteriaQueryLong).getSingleResult();
				
				
			} 
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return totalCriteriaCount;
		
	}

	
	@SuppressWarnings("rawtypes")
	public CriteriaQuery getCriteriaQuery(CriteriaBuilder criteriaBuilder) {
		try {
			if(criteriaBuilder == null) {
				return getCriteriaQuery();
			}
			return criteriaBuilder.createQuery();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	public CriteriaBuilder getCriteriaBuilder()  {
		try {
			if(em == null) {
				return getCb();
			}
			return em.getCriteriaBuilder();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes"})
	public Root<T> construirRoot(Class <T> clazz)  {
		try {
			if(em.getCriteriaBuilder() == null) {
				return getRoot();
			}
			criteriaQuery = (CriteriaQuery) em.getCriteriaBuilder().createQuery();
			return  criteriaQuery.from(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * 
	 * @param param parametros de entrada definidas no @see spring.xml <property name="jpaProperties"> <prop key="param">retorno </prop>
	 * @return retorno dessa propriedade
	 * @throws Exception
	 */
	public String getPropriedades(String param)  {
		try {
			return em.getEntityManagerFactory().getProperties().get(param).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return param;
	}
	



	public EntityManager getEntityManager() {
		return em;
	}



	protected BBTSGenericRepositoryImpl <T> getBbtsGenericDAOImpl() {
		return this;
	}



	public CriteriaBuilder getCb() {
		if(cb == null && (em != null && em.getCriteriaBuilder() != null)) {
			return em.getCriteriaBuilder();
		}
		return cb;
	}



	public void setCb(CriteriaBuilder cb) {
		this.cb = cb;
	}



	public CriteriaQuery<T> getCriteriaQuery() {
		return criteriaQuery;
	}



	public void setCriteriaQuery(CriteriaQuery<T> criteriaQuery) {
		this.criteriaQuery = criteriaQuery;
	}



	public Root<T> getRoot() {
		return root;
	}



	public void setRoot(Root<T> root) {
		this.root = root;
	}



	public Path<T> getPath() {
		return path;
	}



	public void setPath(Path<T> path) {
		this.path = path;
	}

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	

	
}
