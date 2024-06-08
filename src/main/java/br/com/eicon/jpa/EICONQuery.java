/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package br.com.eicon.jpa;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.eicon.constants.Constantes;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;




/**
* <h1>BBTSQuery para adicionar operações de Banco de dados (query)</h1>
* @author  Leonardo Silva
* @version 3.0
* @since   07/12/2016
*/

public class EICONQuery <T extends Serializable> {
	private Root<T> root;
	private List <Predicate> predicados;
	private LinkedHashMap <String,Object> filtrosNamedQuery;
	private List<Integer> tipoClausulaNamedQuery;
	private String nomeConsultaNamedQuery;
	private List <Integer> tipoClausulas;
	private List <String> groupsBy;
	private List <String> listDistincts;
	private LinkedHashMap <String,Boolean> filtroOrder;
	private String campoCOUNT;
	private Boolean distinct;
	private Set <String> listaAgrupadorOR;
	private int maxResult;
	private CriteriaBuilder cb;
	private String canonicalName;
	private Class <T> clazzCanonicalName;
	private BBTSGenericRepositoryImpl <T> bbtsGenericDAOImpl;
	private List <Predicate> predicadosFilter;
	private Object valueCorrigido;
	
	
	public EICONQuery(){		
		this.inicializar();	
	}
	
	
	private void inicializar() {
		
		predicados = new ArrayList<>();
		predicadosFilter = new ArrayList<>();
		filtrosNamedQuery = new LinkedHashMap<>();
		tipoClausulaNamedQuery = new ArrayList<>();	
		tipoClausulas = new ArrayList<>();
		filtroOrder = new LinkedHashMap <> ();
		groupsBy = new ArrayList<>();
		listDistincts = new ArrayList<>();
		listaAgrupadorOR = new LinkedHashSet<>(); 
		
		setDistinct(false);
		
	}


	public void adicionarFiltroNamedQuery(String filtro,Object valueObject,int tipoOperacao) {
		try {
			getFiltrosNamedQuery().put(filtro, valueObject);
			getTipoClausulaNamedQuery().add(tipoOperacao);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param filtro da entidade Ex: SadcUsuario, filtros: chave,nome, ativo e etc
	 * @param valueObject valor do filtro Ex: valor da chave: C1234567, nome: João da Silva
	 * @param tipoOperacao se a operação será Like, Igual, Between, Definida na Classe BBTSConstantes @see bbts.constantes.BBTSConstantes
	 * @param funcaoComParametros função definida no banco de dados, nome da funcao e o tipo do seu retorno
	 * @return o predicado dos filtros definidos com sesus respectivos valores e tipoOperação
	 * @throws Exception quando algum filtro informado não exista na entidade
	 */
	public Predicate adicionarFiltro(String filtro,Object valueObject,int tipoOperacao,Object[] funcaoComParametros)  {

		try {

			Predicate filterCondition = montarFiltro(filtro,valueObject,tipoOperacao,funcaoComParametros);
			
			predicadosFilter.add(filterCondition);
			
			return filterCondition;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * 
	 * @param filtro da entidade Ex: SadcUsuario, filtros: chave,nome, ativo e etc
	 * @param valueObject valor do filtro Ex: valor da chave: C1234567, nome: João da Silva
	 * @param tipoOperacao se a operação será Like, Igual, Between, Definida na Classe BBTSConstantes @see bbts.constantes.BBTSConstantes
	 * @return o predicado dos filtros definidos com sesus respectivos valores e tipoOperação
	 */
	public Predicate adicionarFiltro(String filtro,Object valueObject,int tipoOperacao)  {

		try {

			Predicate filterCondition = montarFiltro(filtro,valueObject,tipoOperacao,null);
			
			predicadosFilter.add(filterCondition);
			
			return filterCondition;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void adicionarFiltroHQL(String filtro,Object valueObject)  {
		try {
			getFiltrosNamedQuery().put(filtro, valueObject);								
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	
	private Predicate montarFiltro(String filtro, Object valueObject, int tipoOperacao, Object[] funcaoComParametros)  {
		try {
		
			Expression<String> expression;

			Path<T> path = EICONJpaUtil.getPath(getRoot(), filtro);
			
			if(path == null) {
				path = bbtsGenericDAOImpl.getPath();
			}
			
			if(path.getJavaType() != null && Date.class.isAssignableFrom(path.getJavaType())){					
				Expression<String> dateStringExpr = null;
							
				dateStringExpr =  bbtsGenericDAOImpl.getCriteriaBuilder().function("DATE_FORMAT", String.class, path , (bbtsGenericDAOImpl).getCriteriaBuilder().literal("'%d/%m/%Y %r'"));
								
				expression = bbtsGenericDAOImpl.getCriteriaBuilder().lower(dateStringExpr);					
			} else {
				expression = path.as(String.class);
			}

		
			Expression<String> function = this.montarFunction(funcaoComParametros,valueObject,expression);			
			Predicate filterCondition = null;
			
		
			filterCondition = this.montarFiltroLikes(tipoOperacao, funcaoComParametros,
					valueObject, function,expression );
						
			filterCondition = this.montarFiltroIgualIn(tipoOperacao, funcaoComParametros,
					valueObject, function,expression,filterCondition );
			
			filterCondition = this.montarFiltroNotIn(tipoOperacao, funcaoComParametros,
						valueObject, function,expression,filterCondition );
			
			
			filterCondition = this.montarFiltroBetween(tipoOperacao, valueObject, filterCondition, path );
			
			filterCondition = this.montarFiltroMaiorIgual(tipoOperacao, valueObject, filterCondition, path );
			
			filterCondition = this.montarFiltroMenorIgual(tipoOperacao, valueObject, filterCondition, path );
			
			filterCondition = this.montarFiltroNullIsNotNull(tipoOperacao, valueObject, expression ,filterCondition);
			
			return filterCondition;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	private Predicate montarFiltroNullIsNotNull(int tipoOperacao, Object valueObject,Expression<String> expression, Predicate filterCondition) {
		
		try {
					
			if(tipoOperacao == Constantes.OPERACAO_IS_NOT_NULL){
				if(valueObject == null) {
					filterCondition = getCb().isNotNull(expression);
				}
			}  else if(tipoOperacao == Constantes.OPERACAO_IS_NULL && valueObject == null){
				filterCondition = getCb().isNull(expression);
	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return filterCondition;
	}


	private Predicate montarFiltroMenorIgual(int tipoOperacao, Object valueObject, Predicate filterCondition,
			Path<T> path) {
		try {
			
			if(tipoOperacao == Constantes.OPERACAO_MENOR){
				if (valueObject instanceof Date){
					Date data = (Date) valueObject;
					filterCondition =  getCb().lessThan(path.as(Date.class), data);
				} else {
					String value = String.valueOf(valueObject);
					filterCondition =  getCb().lessThan(path.as(String.class), value);
				}

			} else if(tipoOperacao == Constantes.OPERACAO_MENOR_IGUAL){
				if (valueObject instanceof Date){
					Date data = (Date) valueObject;
					filterCondition =  getCb().lessThanOrEqualTo(path.as(Date.class), data);
				} else {
					String value = String.valueOf(valueObject);
					filterCondition =  getCb().lessThanOrEqualTo(path.as(String.class), value);
				}

			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	private Predicate montarFiltroMaiorIgual(int tipoOperacao, Object valueObject, Predicate filterCondition,
			Path<T> path) {
		try {
			
			 if(tipoOperacao == Constantes.OPERACAO_MAIOR){
					if (valueObject instanceof Date){
						Date data = (Date) valueObject;
						filterCondition = getCb().greaterThan(path.as(Date.class), data);
					} else {
						String value = String.valueOf(valueObject);
						filterCondition = getCb().greaterThan(path.as(String.class), value);
					}

				} else if(tipoOperacao == Constantes.OPERACAO_MAIOR_IGUAL){
					if (valueObject instanceof Date){
						Date data = (Date) valueObject;
						filterCondition = getCb().greaterThanOrEqualTo(path.as(Date.class), data);
					} else {
						String value = String.valueOf(valueObject);
						filterCondition = getCb().greaterThanOrEqualTo(path.as(String.class), value);
					}

				} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	private Predicate montarFiltroBetween(int tipoOperacao, Object valueObject, Predicate filterCondition,
			Path<T> path) {
		try {
			
			if(tipoOperacao == Constantes.OPERACAO_BETWEEN){
				Date[] values = (Date[]) valueObject;
				Date dataInicio = values[0];
				Date dataFim = values[1];				

				filterCondition = getCb().between(path.as(Date.class),dataInicio,dataFim);
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	private Predicate montarFiltroNotIn(int tipoOperacao, Object[] funcaoComParametros,
			Object valueObject, Expression<String> function, Expression<String> expression, Predicate filterCondition) {
		
		try {			
			 if(tipoOperacao == Constantes.OPERACAO_NOT_IN){ 				
				
				if(funcaoComParametros != null && function != null) {
					
					filterCondition = montarFiltroNotInFuncaoComParametros(function,filterCondition);
					
				} else {
					
					
					filterCondition = montarFiltroNotInFuncaoSemParametros(expression,valueObject,filterCondition);
					
				}			
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	private Predicate montarFiltroNotInFuncaoSemParametros(Expression<String> expression,Object valueObject, Predicate filterCondition) {
		try {
			
			String[] values = null;
			
			if(valueObject.getClass().isArray()){
				values = (String[]) valueObject;
			} else {
				values = new String[1];
				values[0] = (String) valueObject;
			}
			if(values.length > 0 && getCb().trim(expression) != null){
				filterCondition = getCb().not(getCb().trim(expression).in(Arrays.asList(values)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	private Predicate montarFiltroNotInFuncaoComParametros(Expression<String> function, Predicate filterCondition) {
		try {
			
			String[] values = null;
			
			if(valueCorrigido.getClass().isArray()){
				values = (String[]) valueCorrigido;
			} else {
				values = new String[1];
				values[0] = (String) valueCorrigido;
			}
			if(values.length > 0){
				filterCondition = getCb().not(getCb().trim(function).in(Arrays.asList(values)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	@SuppressWarnings("unchecked")
	private Expression<String> montarFunction(Object[] funcaoComParametros, Object valueObject,Expression<String> expression) {
		
		Expression<String> function = null;
		
		try {
			
			String nomeFuncao = null;
			
			Class <?> clazz = null;
			
			if(funcaoComParametros != null) {
				
				valueCorrigido = Normalizer.normalize((String) valueObject, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();	

				
				nomeFuncao = (String) funcaoComParametros[0];
				clazz = (Class <?>) funcaoComParametros[1];
				
				
				function = (Expression<String>) getCb().function(nomeFuncao, clazz,getCb().upper(expression));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return function;
	}


	private Predicate montarFiltroIgualIn(int tipoOperacao, Object[] funcaoComParametros, 
			Object valueObject, Expression<String> function, Expression<String> expression,Predicate filterCondition) {
		
		try {
			
			if(tipoOperacao == Constantes.OPERACAO_IGUAL){
								
				filterCondition = montarFiltroIgual(funcaoComParametros,valueObject,function,expression,filterCondition);
	
			} else if(tipoOperacao == Constantes.OPERACAO_IN){ 
				
				
				filterCondition = montarFiltroIn(funcaoComParametros,valueObject,function,expression,filterCondition);
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	private Predicate montarFiltroIn(Object[] funcaoComParametros, Object valueObject, Expression<String> function,
			Expression<String> expression, Predicate filterCondition) {
		try {
			
			String[] values = null;
			
			if(funcaoComParametros != null && function != null) {
				if(valueCorrigido.getClass().isArray()){
					values = (String[]) valueCorrigido;
				} else {
					values = new String[1];
					values[0] = (String) valueCorrigido;
				}
				if(values.length > 0){
					filterCondition = getCb().trim(function).in(Arrays.asList(values));
				}
			
			} else {
			
				if(valueObject.getClass().isArray()){
					values = (String[]) valueObject;
				} else {
					values = new String[1];
					values[0] = (String) valueObject;
				}
				if(values.length > 0 && getCb().trim(expression) != null){
					filterCondition = getCb().trim(expression).in(Arrays.asList(values));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	private Predicate montarFiltroIgual(Object[] funcaoComParametros,
			Object valueObject,Expression<String> function, Expression<String> expression,Predicate filterCondition) {
		try {
			
			if(valueObject == null) {
				filterCondition = getCb().isNull(expression);
			} else {
				
				if(funcaoComParametros != null && function != null) {
					
					String value = String.valueOf(valueCorrigido);
					filterCondition = getCb().equal(getCb().trim(function), value);
					
				} else {
					String value = String.valueOf(valueObject);
					filterCondition = getCb().equal(expression, value);
				}			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	private Predicate montarFiltroLikes(int tipoOperacao,  Object[] funcaoComParametros,
			Object valueObject,Expression<String> function,Expression<String> expression ) {
		
		Predicate filterCondition = null;
		
		String wildCard = "%";
		
		String value = "";
		
		try {

			if(tipoOperacao == Constantes.OPERACAO_LIKE_AMBOS){				
				
				if(funcaoComParametros != null && function != null) {
					value = wildCard + valueCorrigido + wildCard;					
					filterCondition = getCb().like(getCb().trim(function), value);
				} else {
					value = wildCard + valueObject + wildCard;
					filterCondition = getCb().like(getCb().trim(expression), value);
				}
				

			} else if (tipoOperacao == Constantes.OPERACAO_LIKE_ESQUERDO){
				
				if(funcaoComParametros != null && function != null) {		
					value = wildCard + valueCorrigido;
					filterCondition = getCb().like(getCb().trim(function), value);				
				} else {					
					value = wildCard + valueObject;
					filterCondition = getCb().like(getCb().trim(expression), value);					
				}
		
			} else if (tipoOperacao == Constantes.OPERACAO_LIKE_DIREITO){
				
				if(funcaoComParametros != null && function != null) {
					value = valueCorrigido + wildCard;
					filterCondition = getCb().like(getCb().trim(function), value);
				} else {
					value = valueObject + wildCard;
					filterCondition = getCb().like(getCb().trim(expression), value);
				}
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterCondition;
	}


	/**
	 * 
	 * @param preds adicionados no método adicionarFiltro(..) e agrupados por clausulas OR
	 * @return o predicado agrupador por OR
	 * @throws Exception quando a condição de filtro nao for da entidade
	 */
	public Predicate agruparFiltroOR(Predicate... preds) {		
		return getCb().or(preds);
	}
	
	/**
	 * 
	 * @param preds  adicionados no método adicionarFiltro(..) e agrupados por clausulas AND
	 * @return  o predicado agrupador por AND
	 * @throws Exception quando a condição de filtro nao for da entidade
	 */
	public Predicate agruparFiltroAND(Predicate... preds) {		
		if(getCb() != null) {
			return getCb().and(preds);
		} else {
			return null;
		}
	}
	
	/**
	 * @param orderBy campos para o orderby
	 * @param asc true se for ascendente e false se for descendente
	 */
	public void adicionarOrderBy(String orderBy,boolean asc)  {
		try {
			filtroOrder.put(orderBy , asc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void adicionarGroupBy(String groubBy)  {
		try {
			groupsBy.add(groubBy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void adicionarDistinct(String campoDistinct) {
		listDistincts.add(campoDistinct);
		setDistinct(true);
	}


	public void adicionarQuery(Predicate agrupador) {
		getPredicados().add(agrupador);
		
	}


	public void acidionarConsultaNamedQuery(String nomeConsulta){
		setNomeConsultaNamedQuery(nomeConsulta);
	}
			

	public void acidionarConsultaEntidadeBase(Class <T> clazz) {
		
		try {
			
			inicializar();
			
			cb = bbtsGenericDAOImpl.getCriteriaBuilder();			
			root = bbtsGenericDAOImpl.construirRoot(clazz);
			setCanonicalName(canonicalName);  
			setClazzCanonicalName(clazz);
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
		
	}
	
	
	public Root<T> criarRootDeCanonicalPath(Class <T> clazz)  {
		try {
			return bbtsGenericDAOImpl.construirRoot(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	public List<Predicate> getPredicadosFilter() {
		return predicadosFilter;
	}


	public void adicionarMaxResult(int maxResult){
		setMaxResult(maxResult);
	}

	public List<Integer> getTipoClausulas() {
		return tipoClausulas;
	}

	public Map<String, Boolean> getFiltroOrder() {
		return filtroOrder;
	}


	public Boolean getDistinct() {
		return distinct;
	}


	public void setDistinct(Boolean distinct) {
		this.distinct = distinct;
	}


	public List<String> getGroupsBy() {
		return groupsBy;
	}


	public String getCampoCOUNT() {
		return campoCOUNT;
	}


	public void setCampoCOUNT(String campoCOUNT) {
		this.campoCOUNT = campoCOUNT;
	}


	public Set<String> getListaAgrupadorOR() {
		return listaAgrupadorOR;
	}


	public int getMaxResult() {
		return maxResult;
	}


	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}


	public List<String> getListDistincts() {
		return listDistincts;
	}


	public void setListDistincts(List<String> listDistincts) {
		this.listDistincts = listDistincts;
	}


	public Root<T> getRoot() {
		if(root == null && (bbtsGenericDAOImpl != null && bbtsGenericDAOImpl.getRoot() != null)) {
			return bbtsGenericDAOImpl.getRoot();
		}
		return root;
	}
	

	public void setRoot(Root<T> root) {
		this.root = root;
	}


	public List<Predicate> getPredicados() {
		return predicados;
	}


	public String getCanonicalName() {
		return canonicalName;
	}


	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	
	public Class<T> getClazzCanonicalName() {
		return clazzCanonicalName;
	}


	public void setClazzCanonicalName(Class<T> clazzCanonicalName) {
		this.clazzCanonicalName = clazzCanonicalName;
	}


	public Map<String, Object> getFiltrosNamedQuery() {
		return filtrosNamedQuery;
	}


	public List<Integer> getTipoClausulaNamedQuery() {
		return tipoClausulaNamedQuery;
	}


	public String getNomeConsultaNamedQuery() {
		return nomeConsultaNamedQuery;
	}


	public void setNomeConsultaNamedQuery(String nomeConsultaNamedQuery) {
		this.nomeConsultaNamedQuery = nomeConsultaNamedQuery;
	}


	public void setBbtsGenericDAOImpl(BBTSGenericRepositoryImpl<T> bbtsGenericDAOImpl) {
		this.bbtsGenericDAOImpl = bbtsGenericDAOImpl;
	}

	public void setCb(CriteriaBuilder cb) {
		this.cb = cb;
	}
	public CriteriaBuilder getCb() {
		if(cb == null) {
			return bbtsGenericDAOImpl.getCb();
		}
		return cb;
	}



	
}
