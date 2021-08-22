package com.caijia;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;
import com.mitchellbosecke.pebble.spring.servlet.PebbleViewResolver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan
@PropertySource("classpath:/app.properties")
@EnableAspectJAutoProxy
@EnableTransactionManagement
@MapperScan("com.caijia.mapper")
@EnableWebMvc
public class AppConfig {

	private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
	public static void main(String[] args) throws Exception {
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
	}

	@Value("${db.url}")
	private String jdbcUrl;
	@Value("${db.username}")
	private String username;
	@Value("${db.password}")
	private String password;
	@Value("${db.connectionTimeout}")
	private long connectionTimeout;
	@Value("${db.idleTimeout}")
	private long idleTimeout;
	@Value("${db.maximumPoolSize}")
	private int maximumPoolSize;

	@Bean
	@Primary
	DataSource getMasterDataSource() throws IOException {
		log.info("获取mysql数据源");
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(username);
		config.setPassword(password);
		config.setConnectionTimeout(connectionTimeout);
		config.setIdleTimeout(idleTimeout);
		config.setMaximumPoolSize(maximumPoolSize);
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
}
