package br.com.eicon.jpa;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jdal.beans.PropertyUtils;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.EntityType;
/**
 * 
 * @author c1260311
 * @version 3.0
 * @since 29/11/2016
 */
public abstract class EICONJpaUtil {

	private EICONJpaUtil () {}

	private static volatile int aliasCount = 0;
	
	/**
	 * Result count from a CriteriaQuery
	 * @param em Entity Manager
	 * @param criteria Criteria Query to count results
	 * @param <T> for return count
	 * @return row count
	 */
	public static <T> Long count(EntityManager em, CriteriaQuery<T> criteria) {
	   
		CriteriaQuery<Long> cq = countCriteria(em, criteria);
		if(cq != null){
			return em.createQuery(cq).getSingleResult();
		} else {
			return (long) 0;
		}
	}
	
	/**
	 * Create a row count CriteriaQuery from a CriteriaQuery
	 * @param em entity manager
	 * @param criteria source criteria
	 * @param <T> return CriteriaQuery count
	 * @return row count CriteriaQuery
	 */
	public static <T> CriteriaQuery<Long> countCriteria(EntityManager em, CriteriaQuery<T> criteria) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		copyCriteriaWithoutSelectionAndOrder(criteria, countCriteria, false);
		
		Expression<Long> countExpression = null;
		
		if (criteria.isDistinct()) {
			Root<T> root = findRoot(countCriteria, criteria.getResultType());
			if(root != null){
				countExpression = builder.countDistinct(root);
			}
		} else {	
			Root<T> root = findRoot(countCriteria, criteria.getResultType());
			if(root != null){
				countExpression = builder.count(root);
			}
			
		}
		if(countExpression != null){
			return countCriteria.select(countExpression);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets The result alias, if none set a default one and return it
	 * @param selection para fazer o alias
	 * @param <T> create or get a alias from selection and return alias
	 * @return root alias or generated one
	 */
	public static synchronized <T> String getOrCreateAlias(Selection<T> selection) {
		// reset alias count
		if (aliasCount > 1000)
			aliasCount = 0;
			
		String alias = selection.getAlias();
		if (alias == null) {
			alias = "JDAL_generatedAlias" + aliasCount++;
			selection.alias(alias);
		}
		return alias;
		
	}

	/**
	 * Find Root of result type
	 * @param query criteria query
	 * @param <T> retturn root entity
	 * @return the root of result type or null if none
	 */
	public static  <T> Root<T> findRoot(CriteriaQuery<T> query) {
		return findRoot(query, query.getResultType());
	}
	
	/**
	 * Find the Root with type class on CriteriaQuery Root Set
	 * @param <T> root type
	 * @param query criteria query
	 * @param clazz root type
	 * @return root 
	 */
	@SuppressWarnings("unchecked")
	public static  <T> Root<T> findRoot(CriteriaQuery<?> query, Class<T> clazz) {

		for (Root<?> r : query.getRoots()) {
			if (clazz.equals(r.getJavaType())) {
				return (Root<T>) r.as(clazz);
			}
		}
		if(!query.getRoots().isEmpty()){
			return (Root<T>) query.getRoots().iterator().next();
		} else {
			return null;
		}
		
		
	}
	
	
	/**
	 * Gets a Path from Path using property path
	 * @param path the base path
	 * @param propertyPath property path String like "customer.order.price"
	 * @param <T> return path from property entity
	 * @return a new Path for property
	 */
	@SuppressWarnings("unchecked")
	public static <T> Path<T> getPath(Path<?> path, String propertyPath) {
		if (StringUtils.isEmpty(propertyPath))
			return (Path<T>) path;
		
		String name = StringUtils.substringBefore(propertyPath, PropertyUtils.PROPERTY_SEPARATOR);
		Path<?> p = path.get(name); 
		if(p != null) {
			return getPath(p, StringUtils.substringAfter(propertyPath, PropertyUtils.PROPERTY_SEPARATOR));
		}
		return null; 
	}
	
	
	/**
	 * Gets a Path from Path using property path
	 * @param path the base path
	 * @param propertyPath property path String like "customer.order.price"
	 * @param <T> return path from property entity
	 * @return a new Path for property
	 */
	@SuppressWarnings("unchecked")
	public static <T> Path<T> getPath2(Path<?> path, String propertyPath) {
		try {
			
			if (StringUtils.isEmpty(propertyPath))
				return (Path<T>) path;
			
			String name = StringUtils.substringBefore(propertyPath, PropertyUtils.PROPERTY_SEPARATOR);
			Path<?> p = path.get(name); 
			
			return getPath(p, StringUtils.substringAfter(propertyPath, PropertyUtils.PROPERTY_SEPARATOR));
		} catch (Exception e) {
			return null;
		}
	}

	

	/**
	 * Create a count query string from a query string
	 * @param queryString string to parse
	 * @return the count query string
	 */
	public static String createCountQueryString(String queryString) {
		return queryString.replaceFirst("^.*(?i)from", "select count (*) from ");
	}

	
	/**
	 * Copy Criteria without Selection.
	 * @param from source Criteria.
	 * @param to destination Criteria.
	 */
	public static void  copyCriteriaNoSelection(CriteriaQuery<?> from, CriteriaQuery<?> to) {
		copyCriteriaWithoutSelectionAndOrder(from, to, true);
		to.orderBy(from.getOrderList());
	}

	/**
	 * Copy criteria without selection and order.
	 * @param from source Criteria.
	 * @param to destination Criteria.
	 */
	private static void copyCriteriaWithoutSelectionAndOrder(
			CriteriaQuery<?> from, CriteriaQuery<?> to, boolean copyFetches) {
		if (isEclipseLink(from) && from.getRestriction() != null) {
			// EclipseLink adds roots from predicate paths to critera. Skip copying 
			// roots as workaround.
		}
		else {
			 // Copy Roots
			 for (Root<?> root : from.getRoots()) {
				 Root<?> dest = to.from(root.getJavaType());
				 dest.alias(getOrCreateAlias(root));
				 copyJoins(root, dest);
				 if (copyFetches)
					 copyFetches(root, dest);
			 }
		}
		
		to.groupBy(from.getGroupList());
		to.distinct(from.isDistinct());
		
		if (from.getGroupRestriction() != null)
			to.having(from.getGroupRestriction());
		
		Predicate predicate = from.getRestriction();
		if (predicate != null)
			to.where(predicate);
	}
	
	public static boolean isEclipseLink(CriteriaQuery<?> from) {
		return from.getClass().getName().contains("org.eclipse.persistence");
	}

	public static <T> void copyCriteria(CriteriaQuery<T> from, CriteriaQuery<T> to) {
		copyCriteriaNoSelection(from, to);
		to.select(from.getSelection());
	}
	
	/**
	 * Copy Joins
	 * @param from source Join
	 * @param to destination Join
	 */
	public static void copyJoins(From<?, ?> from, From<?, ?> to) {
		for (Join<?, ?> j : from.getJoins()) {
			Join<?, ?> toJoin = to.join(j.getAttribute().getName(), j.getJoinType());
			toJoin.alias(getOrCreateAlias(j));
		
			copyJoins(j, toJoin);
		}
	}
	
	/**
	 * Copy Fetches
	 * @param from source From
	 * @param to destination From
	 */
	public static void copyFetches(From<?, ?> from, From<?, ?> to) {
		for (Fetch<?, ?> f : from.getFetches()) {
			Fetch<?, ?> toFetch = to.fetch(f.getAttribute().getName());
			copyFetches(f, toFetch);
		}
	}

	/**
	 * Copy Fetches
	 * @param from source Fetch
	 * @param to dest Fetch
	 */
	public static void copyFetches(Fetch<?, ?> from, Fetch<?, ?> to) {
		for (Fetch<?, ?> f : from.getFetches()) {
			Fetch<?, ?> toFetch = to.fetch(f.getAttribute().getName());
			// recursively copy fetches
			copyFetches(f, toFetch);
		}
	}
	
	/**
	 * Test if the path exists
	 * @param path path to test on
	 * @param propertyPath path to test
	 * @return true if path exists
	 */
	public static boolean hasPath(Path<?> path, String propertyPath) {
		try {
			getPath(path, propertyPath);
			return true;
		}
		catch (Exception e) { // Hibernate throws NPE here.
			return false;
		}
	}
	
	/**
	 * Initialize a entity. 
	 * @param em entity manager to use
	 * @param entity entity to initialize
	 * @param depth max depth on recursion
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void initialize(EntityManager em, Object entity, int depth) {
		// return on nulls, depth = 0 or already initialized objects
		if (entity == null || depth == 0) { 
			return; 
		}
		
		PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		EntityType entityType = em.getMetamodel().entity(entity.getClass());
		Set<Attribute>  attributes = entityType.getDeclaredAttributes();
		
		Object id = unitUtil.getIdentifier(entity);
		
		if (id != null) {
			Object attached = em.find(entity.getClass(), unitUtil.getIdentifier(entity));

			for (Attribute a : attributes) {
				if (!unitUtil.isLoaded(entity, a.getName())) {
					if (a.isCollection()) {
						intializeCollection(em, entity, attached,  a, depth);
					}
					else if(a.isAssociation()) {
						intialize(em, entity, attached, a, depth);
					}
				}
			}
		}
	}
	
	/** 
	 * Initialize entity attribute
	 * @param em
	 * @param entity
	 * @param a
	 * @param depth
	 */
	@SuppressWarnings("rawtypes")
	private static void intialize(EntityManager em, Object entity, Object attached, Attribute a, int depth) {
		Object value = PropertyAccessorFactory.forDirectFieldAccess(attached).getPropertyValue(a.getName());
		if (!em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(value)) {
			em.refresh(value);
		}
		
		PropertyAccessorFactory.forDirectFieldAccess(entity).setPropertyValue(a.getName(), value);
		
		initialize(em, value, depth - 1);
	}

	/**
	 * Initialize collection
	 * @param em
	 * @param entity
	 * @param a
	 * @param i
	 */
	@SuppressWarnings("rawtypes")
	private static void intializeCollection(EntityManager em, Object entity, Object attached, 
			Attribute a, int depth) {
		PropertyAccessor accessor = PropertyAccessorFactory.forDirectFieldAccess(attached);
		Collection c = (Collection) accessor.getPropertyValue(a.getName());
		
		for (Object o : c)
			initialize(em, o, depth -1);
		
		PropertyAccessorFactory.forDirectFieldAccess(entity).setPropertyValue(a.getName(), c);
	}
	
	
	
	/**
	 * Test if attribute is type or in collections has element type
	 * @param attribute attribute to test
	 * @param clazz Class to test
	 * @return true if clazz is asignable from type or element type
	 */
	public static boolean isTypeOrElementType(Attribute<?, ?> attribute, Class<?> clazz) {
		if (attribute.isCollection()) {
			return clazz.isAssignableFrom(((CollectionAttribute<?, ?>) attribute).getBindableJavaType());
		}
		
		return clazz.isAssignableFrom(attribute.getJavaType());
	}
	
	
}
