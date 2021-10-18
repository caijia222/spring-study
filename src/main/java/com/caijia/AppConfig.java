package com.caijia;

import java.io.File;
import java.time.ZoneId;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;
import com.mitchellbosecke.pebble.spring.servlet.PebbleViewResolver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan
@PropertySource({"classpath:/app.properties","classpath:/smtp.properties","classpath:/jms.properties","classpath:/task.properties"})
@EnableAspectJAutoProxy
@EnableTransactionManagement
@MapperScan("com.caijia.mapper")
@EnableWebMvc
@EnableJms
@EnableScheduling
@EnableMBeanExport
public class AppConfig {

	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
	public static void main(String[] args) throws Exception {
		logger.info("开始启动内嵌tmocat");
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(Integer.getInteger("port", 8080));
		tomcat.getConnector();
		Context ctx = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(
				new DirResourceSet(resources, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/"));
		ctx.setResources(resources);
		tomcat.start();
		tomcat.getServer().await();
		logger.info("内嵌tmocat启动完成");
	}

	@Bean
	DataSource createDataSource(
			// properties:
			@Value("${jdbc.url}") String jdbcUrl, @Value("${jdbc.username}") String jdbcUsername,
			@Value("${jdbc.password}") String jdbcPassword) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(jdbcUsername);
		config.setPassword(jdbcPassword);
		config.addDataSourceProperty("autoCommit", "false");
		config.addDataSourceProperty("connectionTimeout", "5");
		config.addDataSourceProperty("idleTimeout", "60");
		return new HikariDataSource(config);
	}

	@Bean("z")
	ZoneId createZoneOfZ() {
		return ZoneId.of("Z");
	}

	@Bean("utc8")
	ZoneId createZoneOfUTC8(@Value("${app.zone:Z}") String zoneId) {
		return ZoneId.of(zoneId);
	}

	// spring jdbc
	@Bean
	JdbcTemplate createJdbcTemplate(@Autowired DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	// mybatis
	@Bean
	SqlSessionFactoryBean createSqlSessionFactoryBean(@Autowired DataSource dataSource) {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		return sqlSessionFactoryBean;
	}

	// spring 事务
	@Bean
	PlatformTransactionManager createTxManager(@Autowired DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	WebMvcConfigurer createConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/static/**").addResourceLocations("/static/");
			}
		};
	}

	@Bean
	ViewResolver createResolver(@Autowired ServletContext servletContext) {
		PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(true).cacheActive(false)
				.loader(new ServletLoader(servletContext)).extension(new SpringExtension()).build();
		PebbleViewResolver pebbleViewResolver = new PebbleViewResolver();
		pebbleViewResolver.setPrefix("/WEB-INF/templates/");
		pebbleViewResolver.setSuffix("");
		pebbleViewResolver.setPebbleEngine(engine);
		return pebbleViewResolver;
	}

	// 注册拦截器
	@Bean
	WebMvcConfigurer createMvcConfigurer(@Autowired HandlerInterceptor[] interceptors) {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				for (HandlerInterceptor handlerInterceptor : interceptors) {
					registry.addInterceptor(handlerInterceptor);
				}
			}

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOrigins("http://local.liaoxuefeng.com:8080")
						.allowedMethods("GET", "POST")
						.maxAge(3600);
			}
		};
	}
	
	// -- javamail configuration ----------------------------------------------

	@Bean
	JavaMailSender createJavaMailSender(
			// properties:
			@Value("${smtp.host}") String host, @Value("${smtp.port}") int port, @Value("${smtp.auth}") String auth,
			@Value("${smtp.username}") String username, @Value("${smtp.password}") String password,
			@Value("${smtp.debug:true}") String debug) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);

		mailSender.setUsername(username);
		mailSender.setPassword(password);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", auth);
		if (port == 587) {
			props.put("mail.smtp.starttls.enable", "true");
		}
		if (port == 465) {
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		props.put("mail.debug", debug);
		return mailSender;
	}
	
	@Bean
	ConnectionFactory createConnectionFactory(
			@Value("${jms.uri:tcp://localhost:61616}") String uri,
			@Value("${jms.username:admin}") String username,
			@Value("${jms.password:admin}") String password
			) 
	{
		return new ActiveMQConnectionFactory(uri, username, password);
	}
	
	@Bean
	JmsTemplate createJmsTemplate(@Autowired ConnectionFactory connectionFactory) {
		return new JmsTemplate(connectionFactory);
	}
	
	@Bean("jmsListenerContainerFactory")
	DefaultJmsListenerContainerFactory createDefaultJmsListenerContainerFactory(@Autowired ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		return factory;
	}
	
	@Bean
	ObjectMapper createObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return om;
	}
}
