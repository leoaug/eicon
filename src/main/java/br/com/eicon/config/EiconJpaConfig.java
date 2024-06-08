package br.com.eicon.config;

import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import br.com.eicon.constants.Constantes;


@Configuration
@EnableAutoConfiguration
@ComponentScan({ "br.com.eicon.*" })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource("classpath:application.properties")
public class EiconJpaConfig {

	@Value("${jndi-name}")
	private String jndiName;

	@Bean(name = Constantes.TRANSACTION_MANAGER_EICON)
	public PlatformTransactionManager transactionManager() throws Exception {
		try {
			return new JpaTransactionManager(entityManagerFactory().getObject());
		} catch (Exception e) {
			throw e;
		}
	}

	
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
		try {
			 
			LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();	
			factoryBean.setPersistenceUnitName(Constantes.PERSISTENCE_UNIT_EICON);
			factoryBean.setDataSource(dataSource());
			factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
			factoryBean.setPackagesToScan(Constantes.PACKAGE_TO_SCAN_EICON);
			factoryBean.setJpaProperties(getProperties());
			
			return factoryBean;
		} catch (Exception e) {
			throw e;
		}
	}

	private Properties getProperties() {

		
		Properties prop = new Properties();		
		prop.setProperty("hibernate.show_sql","true");
		prop.setProperty("hibernate.use_sql_comments" ,"true");
		prop.setProperty("hibernate.type", "trace"); 
		prop.setProperty("hibernate.format_sql","true");
		prop.setProperty("hibernate.archive.autodetection","class");
		prop.setProperty("hibernate.enable_lazy_load_no_trans","true");
	
		//"update" caso queira para atualizar as tabelas
		prop.setProperty("hibernate.hbm2ddl.auto","update");
		prop.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		return prop;
	}

	/*
	@Bean
	public AnnotationMBeanExporter annotationMBeanExporter() {
	    AnnotationMBeanExporter annotationMBeanExporter = new AnnotationMBeanExporter();
	    annotationMBeanExporter.addExcludedBean(Constantes.DATA_SOURCE_BEAN_EICON);
	    annotationMBeanExporter.setRegistrationPolicy(RegistrationPolicy.IGNORE_EXISTING);
	    return annotationMBeanExporter;
	}
*/
	@Bean(name = Constantes.DATA_SOURCE_BEAN_EICON)
	//@Primary
	public DataSource dataSource() throws NamingException {
		return (DataSource) new JndiTemplate().lookup("java:comp/env/" + jndiName);
    }
}
