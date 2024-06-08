package br.com.eicon.jpa;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class EICONClassUtil implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String CLAZZ = "class ";
	private static final String SERVICE =  "Service";

	public static String getCanonicalNamePorTipoGenericsSadc(Class <?> clazz,String pacote) {
		try {
					
			Type tipo = clazz.getGenericSuperclass();
		 	
			if(tipo instanceof ParameterizedType) {
				ParameterizedType ptT = (ParameterizedType) clazz.getGenericSuperclass();
				Type[] type = ptT.getActualTypeArguments();
				return type[0].toString().replaceFirst(CLAZZ, "");
			} else {

				Type type = clazz.getGenericSuperclass();
				String classe = "";
				if(type.getTypeName().indexOf(SERVICE) != -1) {
					classe = type.getTypeName().substring(type.getTypeName().lastIndexOf(".") + 1, type.getTypeName().indexOf(SERVICE));
				} 

				return pacote + "." + classe;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
}
