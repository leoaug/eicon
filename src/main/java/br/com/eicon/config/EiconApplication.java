package br.com.eicon.config;

import javax.sql.DataSource;

import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
  

@SpringBootApplication
@EnableAutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan({ "br.com.eicon.*" })
@PropertySource("classpath:application.properties")
public class EiconApplication {

	@Value("${spring.datasource.jndi-name}")
	private String jndiName;
	
	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;
	 
	@Value("${spring.datasource.username}")
	private String usuario;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Bean
	public TomcatServletWebServerFactory tomcatFactory() {
	    return new TomcatServletWebServerFactory() {
	        @Override
	        protected TomcatWebServer getTomcatWebServer(org.apache.catalina.startup.Tomcat tomcat) {
	            tomcat.enableNaming(); 
	            return super.getTomcatWebServer(tomcat);
	        }

	        @Override 
	        protected void postProcessContext(org.apache.catalina.Context context) {

	            // context
	            ContextResource resource = new ContextResource();
	            resource.setName(jndiName);
	            resource.setType(DataSource.class.getName());
	            resource.setProperty("driverClassName", driverClassName);

	            resource.setProperty("url", url);
	            resource.setProperty("username", usuario);
	            resource.setProperty("password", password);
	            context.getNamingResources()
	                   .addResource(resource);          
	        }
	    };
	}
	
	public static void main(String[] args) {
		SpringApplication.run(EiconApplication.class, args);
	}

}
