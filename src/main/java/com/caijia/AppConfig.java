package com.caijia;

import java.io.IOException;
import java.time.ZoneId;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.caijia.bean.Book;
import com.caijia.bean.User;
import com.caijia.service.BookService;
import com.caijia.service.UserService;
import com.caijia.validator.Validators;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ComponentScan
@Slf4j
@PropertySource("app.properties")
@EnableAspectJAutoProxy
public class AppConfig {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		UserService userService = context.getBean(UserService.class);
		User user = userService.login("999@gamil.com", "123445");
		System.out.println(user);
		Validators validators = context.getBean(Validators.class);
		validators.validate(user.getEmail(), user.getPassword(), user.getName());
		BookService bookService = context.getBean(BookService.class);
		Book book = bookService.getBook(1);
		System.out.println(book);
		userService.register("caijia222@agree.com", "123445", "蔡佳");
	}

	@Bean
	DataSource getMasterDataSource(@Value("${db.url}") String jdbcUrl, @Value("${db.username}") String username,
			@Value("${db.password}") String password, @Value("${db.connectionTimeout}") long connectionTimeout,
			@Value("${db.idleTimeout}") long idleTimeout, @Value("${db.maximumPoolSize}") int maximumPoolSize)
			throws IOException {
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

	@Bean
	@Primary
	@Qualifier("hsqldb")
	DataSource getSlaveDataSource(@Value("${jdbc.url}") String jdbcUrl,@Value("${jdbc.username}") String jdbcUsername,@Value("${jdbc.password}") String jdbcPassword) {
		log.info("获取hsqldb数据源");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(jdbcUsername);
        config.setPassword(jdbcPassword);
        config.addDataSourceProperty("autoCommit", "true");
        config.addDataSourceProperty("connectionTimeout", "5");
        config.addDataSourceProperty("idleTimeout", "60");
        return new HikariDataSource(config);
	}

	@Bean
	JdbcTemplate createJdbcTemplate(@Autowired DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean("z")
	ZoneId createZoneOfZ() {
		return ZoneId.of("Z");
	}

	@Bean("utc8")
	ZoneId createZoneOfUTC8(@Value("${app.zone:Z}") String zoneId) {
		return ZoneId.of(zoneId);
	}
}
