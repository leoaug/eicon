package br.com.eicon.constants;

public class Constantes {

	private Constantes() {}
	
	/**
	 * Operações das Queries do SADC
	 */
	public static final int OPERACAO_LIKE_AMBOS = 1;
	public static final int OPERACAO_LIKE_ESQUERDO = 2;
	public static final int OPERACAO_LIKE_DIREITO = 3;	
	public static final int OPERACAO_IGUAL = 4;
	public static final int OPERACAO_IN = 5;
	public static final int OPERACAO_BETWEEN = 6;
	public static final int OPERACAO_MAIOR = 7;
	public static final int OPERACAO_MENOR = 8;
	public static final int OPERACAO_MAIOR_IGUAL = 9;
	public static final int OPERACAO_MENOR_IGUAL = 10;
	public static final int OPERACAO_NOT_IN = 11;
	public static final int OPERACAO_IS_NOT_NULL = 12;
	public static final int OPERACAO_IS_NULL = 13;
	
	public static final String TRANSACTION_MANAGER_EICON = "transactionManagerEicon";
	public static final String DATA_SOURCE_BEAN_EICON = "dataSourceEicon";
	public static final String PERSISTENCE_UNIT_EICON = "eicon";
	public static final String ENTITY_MANAGER_EICON = "entityManagerEicon";
	public static final String PACKAGE_TO_SCAN_EICON = "br.com.eicon.model";
	public static final String EICON_SCHEMA = "eicon";



	
	
}
