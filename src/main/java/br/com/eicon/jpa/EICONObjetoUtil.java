package br.com.eicon.jpa;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.text.WordUtils;
import org.reflections.ReflectionUtils;

import com.google.common.collect.Lists;

import javax.persistence.Transient;


public class EICONObjetoUtil <T extends Serializable> {

	@SuppressWarnings("rawtypes")
	private static HashMap descriptorCache = new HashMap();	
	private static final String BOOLEAN =  "boolean";
	private T entidadeFinal;
	private Method[] metodos;

	@SuppressWarnings("unchecked")
	public Map <String,Object> varrerAtributosObjeto(T entidade,T entidadeAux ,StringBuilder builderCampos,Map <String,Object> params,List <String> atributos ,int index, boolean isSuperClass)  {
		try {
			
			if(isSuperClass){
				metodos = new Method[1];
				
				String comecoMetodo = "";
				 
				if(entidade.getClass().getDeclaredField(atributos.get(index)).getType().toString().equals(BOOLEAN)) {
					comecoMetodo = "is";
				} else {
					comecoMetodo = "get";
				}
				
				metodos[0] = entidade.getClass().getDeclaredMethod(comecoMetodo + WordUtils.capitalize(atributos.get(index)));

				entidadeFinal = entidade;
			} else {
				
				Set<Method> getters = ReflectionUtils.getAllMethods(entidadeAux.getClass(),
					      ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix("get"));
				metodos = getters.toArray(new Method[getters.size()]);
				entidadeFinal = entidadeAux;
			}
						
			for (Method metodo : metodos) {								
				params = this.montarEvarrerAtributosObjeto(metodo,params,builderCampos,atributos,entidade,index);				
			}
			
			
			for (Method metodo : metodos) {
				
				params = this.montarEvarrerAtributosObjetoFinal(metodo,params,builderCampos,atributos,entidade,index);

			}

			if(index == atributos.size() - 1){
				return params;
			} else if(index + 1 <= atributos.size() - 1){
				builderCampos.delete(0, builderCampos.toString().length());
				varrerAtributosObjeto(entidade, entidade, builderCampos, params, atributos, index + 1, true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
		
		
	}

	

	@SuppressWarnings("unchecked")
	private Map <String,Object>  montarEvarrerAtributosObjetoFinal(Method metodo, Map<String, Object> params,
			StringBuilder builderCampos, List<String> atributos, T entidade, int index) {
		try {
			
			if(!Collection.class.isAssignableFrom(metodo.getClass()) && !metodo.isAnnotationPresent(Transient.class) && !org.springframework.beans.BeanUtils.isSimpleValueType(metodo.getReturnType())){

				String comecoMetodo = "";

				if(entidadeFinal.getClass().getDeclaredField(atributos.get(index)).getType().toString().equals(BOOLEAN)) {
					comecoMetodo = "is";
				} else {
					comecoMetodo = "get";
				}

				if(!entidadeFinal.getClass().getDeclaredField(WordUtils.uncapitalize(metodo.getName().replaceFirst(comecoMetodo, ""))).isAnnotationPresent(Transient.class)){


					Object obj = metodo.invoke(entidadeFinal);

					if(obj != null){

						builderCampos.append(WordUtils.uncapitalize(metodo.getName().replaceFirst(comecoMetodo, "")) + ".");

						varrerAtributosObjeto(entidade, (T) obj, builderCampos, params, atributos, index, false);
					}
				}

			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
		
	}


	private Map <String,Object> montarEvarrerAtributosObjeto(Method metodo, Map<String, Object> params,StringBuilder builderCampos,List <String> atributos,T entidade,int index) {
			
		try {
			if(org.springframework.beans.BeanUtils.isSimpleValueType(metodo.getReturnType())){
				if(!Collection.class.isAssignableFrom(metodo.getClass()) &&  !metodo.isAnnotationPresent(Transient.class) ){							
					String comecoMetodo = metodoComecadoPor(atributos,index);
					if(!entidadeFinal.getClass().getDeclaredField(WordUtils.uncapitalize(metodo.getName().replaceFirst(comecoMetodo, ""))).isAnnotationPresent(Transient.class)){						
						this.adicionarParamsObjeto(builderCampos,metodo,comecoMetodo,params);
					}
				}

				if(metodos.length == 1){
					builderCampos.delete(0, builderCampos.toString().length());
					if(index + 1 <= atributos.size() - 1){
						varrerAtributosObjeto(entidade, entidade, builderCampos, params, atributos, index + 1, true);
					}
				} 

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return params;
	}



	private void adicionarParamsObjeto(StringBuilder builderCampos, Method metodo, String comecoMetodo,
			Map<String, Object> params) {
		try {
			
			Object obj = metodo.invoke(entidadeFinal);
			if(obj != null){
				params.put(builderCampos.toString() + WordUtils.uncapitalize(metodo.getName().replaceFirst(comecoMetodo, "")) , obj);							
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private String metodoComecadoPor(List<String> atributos, int index) {
		
		String comecoMetodo = "";
		
		try {
						
			if(entidadeFinal.getClass().getDeclaredField(atributos.get(index)).getType().toString().equals(BOOLEAN)) {
				comecoMetodo = "is";
			} else {
				comecoMetodo = "get";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return comecoMetodo;
	}



	@SuppressWarnings("unchecked")
	public T novaInstanciaComfilhos(T entidade)  {
		try {
		
			T instanciaEntidade = this.instanciar(entidade);
			
			Set<Method> getters = ReflectionUtils.getAllMethods(instanciaEntidade.getClass(),
				      ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix("get"));
			
			Map <String,Class<?>> atributos = new LinkedHashMap <> ()  ;
			
			for(Method metodo : getters){
				
				List <Field> campos = Lists.newArrayList(ReflectionUtils.getFields(metodo.getDeclaringClass(), ReflectionUtils.withModifier(Modifier.PRIVATE)));
				
				if(!org.springframework.beans.BeanUtils.isSimpleValueType(metodo.getReturnType()) && !campos.get(0).isAnnotationPresent(Transient.class) && 
				   !List.class.isAssignableFrom(metodo.getReturnType())){
					
					atributos.put(WordUtils.uncapitalize(metodo.getName().replaceFirst("get", "")), metodo.getReturnType());
				}
			}
			
		
			return this.atribuirNovasInstancias(instanciaEntidade,atributos);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private T atribuirNovasInstancias(T instanciaEntidade, Map <String,Class<?>> atributos)  {
		try {
		
			for (Map.Entry<String, Class<?>> parametro : atributos.entrySet()){
				
				boolean filtrar = this.filtrarTipo(parametro.getValue());
				
				if(!filtrar){
				
					/**
					 * aatribuindo uma nova instancia
					 */
					T instanciaGetter = (T) EICONObjetoUtil.instanciar(parametro.getValue());
					
					/**
					 * pegando o meto set do objeto entidadeFinal
					 */							
					Method metodoSetter = instanciaEntidade.getClass().getDeclaredMethod("set"+WordUtils.capitalize(parametro.getKey().replace("get", "")),instanciaGetter.getClass());
					
					/**
					 * invocando o metodo set
					 */
					metodoSetter.invoke(instanciaEntidade, instanciaGetter);

				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instanciaEntidade;
	}


	private boolean filtrarTipo(Class<?> value) {
		boolean filtrar = false;

		if(value.isInterface() || value.isArray()){
			filtrar = true;
		}

		return filtrar;
	}


	@SuppressWarnings("unchecked")
	public T instanciar(T entidade)  {
		try {
			Constructor <T> constructor = (Constructor<T>) entidade.getClass().getConstructor();			
			return constructor.newInstance();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Object instanciar(Class <?> clazz)  {
		try {
			Constructor <Object> constructor = (Constructor<Object>) clazz.getConstructor();			
			return constructor.newInstance();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}
	
	public static Object getValorCampoObjeto(Serializable obj, String atributo)  {
		try {
			return getNestedProperty(obj,atributo);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get specified nested property value
	 * @throws Exception 
	 */
	private static Object getNestedProperty(Object bean, String property)  {
		try {


			if (property.indexOf('.') >= 0) {
				String[] path = property.split("\\.");
				for (int i = 0; i < path.length && bean != null; i++) {
					bean = getProperty(bean, path[i]);
				}
				return bean;
			} else {
				return getProperty(bean, property);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return property;
	}
	/**
	 * Get specified property value
	 */
	private static Object getProperty(Object bean, String property)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), property);
		if (descriptor == null)
			throw new NoSuchMethodException("Cannot find property " + bean.getClass().getName() + "." + property);
		Method method = descriptor.getReadMethod();
		if (method == null)
			throw new NoSuchMethodException("Cannot find getter for " + bean.getClass().getName() + "." + property);
		return method.invoke(bean);
	}
	
	/**
	 * Get specified property descriptor
	 */
	@SuppressWarnings("rawtypes")
	private static PropertyDescriptor getPropertyDescriptor(Class clazz, String property) {
		return (PropertyDescriptor) getPropertyDescriptors(clazz).get(property);
	}
	/**
	 * Get map with property descriptors for the specified bean class
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map getPropertyDescriptors(Class clazz) {
		HashMap map = (HashMap) descriptorCache.get(clazz);
		if (map == null) {
			BeanInfo beanInfo = null;
			try {
				beanInfo = Introspector.getBeanInfo(clazz);
			} catch (IntrospectionException e) {
				return Collections.emptyMap();
			}
			PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
			if (descriptors == null)
				descriptors = new PropertyDescriptor[0];
			map = new HashMap(descriptors.length);
			for (int i = 0; i < descriptors.length; i++)
				map.put(descriptors[i].getName(), descriptors[i]);
			descriptorCache.put(clazz, map);
		}
		return map;
	}
}
