package com.caijia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.caijia.aspect.MetricTime;
import com.caijia.entity.User;
import com.caijia.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserService {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private MailService mailService;

	@MetricTime("register")
	@Transactional
	public User register(User user) {
		log.info("开始插入一条新用户数据");
		userMapper.insert(user);
		log.info("插入结束了，发送注册成功邮件");
		mailService.sendRegistrationMail(user);
		return user;
	}

	public User signin(String email, String password) {
		return userMapper.getByEmailAndPassword(email, password);
	}

	public User getUser(int id) {
		return userMapper.getById(id);
	}

	public List<User> getUsersByPage(int pageSize, int pageIndex) {
		return userMapper.getByPage(pageSize, pageSize * (pageIndex - 1));
	}
	
	public List<User> getAllUsers() {
		return userMapper.getAll();
	}
	
	public void updateUser(User user) {
		userMapper.update(user);
	}
}
