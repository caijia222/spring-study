package com.caijia.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.caijia.aspect.MetricTime;
import com.caijia.bean.User;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserService {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private MailService mailService;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	@MetricTime("register")
	public User register(String email, String password, String name) {
		try {
			@Cleanup
			Connection conn = dataSource.getConnection();
			@Cleanup
			PreparedStatement pstmt = conn.prepareStatement("insert into user(email,password,name) values (?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.setString(3, name);
			int ret = pstmt.executeUpdate();
			if (ret == 1) {
				log.info("注册成功");
				@Cleanup
				ResultSet rs = pstmt.getGeneratedKeys();
				while (rs.next()) {
					int id = rs.getInt(1);
					User user = User.builder().id(id).email(email).password(password).name(name).build();
					mailService.sendRegistrationMail(user);
					return user;
				}
			}
		} catch (Exception e) {
			log.error("注册失败", e);
		}
		return null;
	}

	public User login(String email, String password) {
		try (Connection conn = dataSource.getConnection()) {
			try (PreparedStatement pstmt = conn
					.prepareStatement("select * from user where email = ? and password = ?")) {
				pstmt.setString(1, email);
				pstmt.setString(2, password);
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						int id = rs.getInt("id");
						String name = rs.getString("name");
						User user = User.builder().id(id).email(email).password(password).name(name).build();
						mailService.sendLoginMail(user);
						return user;
					}
				}
			}
		} catch (Exception e) {
			log.error("登陆失败", e);
		}
		return null;
	}

	public User getUser(int id) {
		try (Connection conn = dataSource.getConnection()) {
			try (PreparedStatement pstmt = conn.prepareStatement("select * from user where id = ?")) {
				pstmt.setInt(1, id);
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						String name = rs.getString("name");
						String password = rs.getString("password");
						String email = rs.getString("email");
						User user = User.builder().id(id).email(email).password(password).name(name).build();
						return user;
					}
				}
			}
		} catch (Exception e) {
			log.error("查询失败", e);
		}
		return null;
	}
}
