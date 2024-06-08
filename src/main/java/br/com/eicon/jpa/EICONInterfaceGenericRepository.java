/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package br.com.eicon.jpa;

import java.io.Serializable;
import java.util.List;



/**
* <h1>Generic Interface DAO (Data access object) com metodos genericos para ser sobrescritos (salvar,excluir,alterar,consultar) </h1>
* @author  Leonardo Silva
* @version 3.0
* @since   05/12/2016
*/
public interface EICONInterfaceGenericRepository<T extends Serializable>  {

	T salvar(T bean) ;
	T alterar(T bean) ;
	T atualizar(T bean) ;
	void excluir(T bean) ;
	void excluirEntidades(List<T> beans) ;
}