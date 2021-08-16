package com.caijia.web;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.caijia.entity.SignInBean;
import com.caijia.entity.User;
import com.caijia.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {
	@Autowired
	UserService userService;
	
	@GetMapping("/usersByPage")
	public List<User> users(@RequestParam("pageSize") int pageSize, @RequestParam("pageIndex") int pageIndex){
		return userService.getUsersByPage(pageSize, pageIndex);
	}

	@GetMapping("/usersAsync")
	public Callable<List<User>> usersAsync(){
		return ()->{
			try {
				Thread.sleep(3000);
			}catch (Exception e) {
			}
			return userService.getAllUsers();
		};
	}

	@GetMapping("/usersAsync2")
	public DeferredResult<List<User>> usersAsync2(){
		DeferredResult<List<User>> result = new DeferredResult<>(3000L);
		new Thread(()->{
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
			}
			try {
				List<User> users = userService.getAllUsers();
				result.setResult(users);
			}catch (Exception e) {
				result.setErrorResult(Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage()));
			}
		}).start();
		return result;
	}
	
	@GetMapping("/users/{id}")
	public User user(@PathVariable("id") int id) {
		User user = userService.getUser(id);
		log.info("reponse=" + user);
		return user;
	}
	
	@PostMapping("/signin")
	public Map<String,Object> signin(@RequestBody SignInBean signInBean){
		try {
			User user = userService.signin(signInBean.email, signInBean.password);
			log.info("reponse=" + user);
			return Map.of("user", user);
		} catch (Exception e) {
			return Map.of("error", "SIGNIN_FAILED");
		}
	}
	
}
