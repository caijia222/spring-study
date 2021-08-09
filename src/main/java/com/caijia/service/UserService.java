package com.caijia.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.caijia.aspect.MetricTime;
import com.caijia.bean.User;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserService {
	@Autowired
	private JdbcTemplate template;
	@Autowired
	private MailService mailService;

	public void setTemplate(JdbcTemplate template) {
		log.info("注入了template");
		this.template = template;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	@MetricTime("register")
	public User register(String email, String password, String name) {
		return template.execute((Connection conn) -> {
			try (var pstmt = conn.prepareStatement("insert into user(email,password,name) values (?,?,?)",
					Statement.RETURN_GENERATED_KEYS);) {
				pstmt.setString(1, email);
				pstmt.setString(2, password);
				pstmt.setString(3, name);
				int ret = pstmt.executeUpdate();
				if (ret == 1) {
					log.info("注册成功");
					try (var rs = pstmt.getGeneratedKeys()) {
						while (rs.next()) {
							int id = rs.getInt(1);
							User user = User.builder().id(id).email(email).password(password).name(name).build();
							mailService.sendRegistrationMail(user);
							return user;
						}
					}
				}
				throw new RuntimeException("注册异常");
			}
		});
	}

	public User login(String email, String password) {
		return template.execute("select * from user where email = ? and password = ?", (PreparedStatement pstmt) -> {
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			try (var rs = pstmt.executeQuery()) {
				while (rs.next()) {
					int id = rs.getInt("id");
					String name = rs.getString("name");
					User user = User.builder().id(id).email(email).password(password).name(name).build();
					mailService.sendLoginMail(user);
					return user;
				}
				throw new RuntimeException("登陆失败");
			}
		});
	}

	public User getUserByEmail(String email) {
		return template.queryForObject("select * from user where email = ?", new Object[] { email }, (rs, rowNum) -> {
			return User.builder().id(rs.getInt("id")).email(rs.getString("email")).password(rs.getString("password"))
					.name(rs.getString("name")).build();
		});
	}

	public User getUser(int id) {
		return template.execute((Connection conn) -> {
			try (var pstmt = conn.prepareStatement("select * from user where id = ?")) {
				pstmt.setInt(1, id);
				try (var rs = pstmt.executeQuery()) {
					while (rs.next()) {
						String name = rs.getString("name");
						String password = rs.getString("password");
						String email = rs.getString("email");
						User user = User.builder().id(id).email(email).password(password).name(name).build();
						return user;
					}
					throw new RuntimeException("查询失败");
				}
			}
		});
	}
}
