package com.caijia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.caijia.aspect.MetricTime;
import com.caijia.entity.User;
import com.caijia.mapper.UserMapper;

@Component
public class UserService {

	@Autowired
	private UserMapper userMapper;

	@MetricTime("register")
	@Transactional
	public User register(User user) {
		userMapper.insert(user);
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
