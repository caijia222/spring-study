package com.caijia.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.caijia.entity.User;
import com.caijia.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UserController {
	@Autowired
	UserService userService;

	@GetMapping("/")
	public ModelAndView index() {
		return new ModelAndView("index.html");
	}

	@GetMapping("/signin")
	public ModelAndView signin() {
		return new ModelAndView("signin.html");
	}
	
	@GetMapping("/signout")
	public ModelAndView signout(HttpSession session) {
		session.removeAttribute("user");
		return new ModelAndView("index.html");
	}
	
	@PostMapping("/signin")
	public ModelAndView signin(@RequestParam String email, @RequestParam String password, HttpSession session) {
		User user = userService.signin(email, password);
		session.setAttribute("user", user);
		return new ModelAndView("index.html", "user", user);
	}

	@GetMapping("/user/profile")
	public ModelAndView profile(HttpSession session) {
		User user = (User)session.getAttribute("user");
		if(user != null) {
			return new ModelAndView("profile.html","user",user);
		}else {
			return new ModelAndView("index.html");
		}
	}

	@PostMapping(value = "/rest", consumes = "application/json;charset=utf-8", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String rest(@RequestBody User user) {
		log.info("REST接口响应收到参数user={}", user);
		return "{\"restSupport\":true}";
	}

	@ExceptionHandler(RuntimeException.class)
	public ModelAndView handleUnknowException(Exception ex) {
		return new ModelAndView("500.html", Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
	}
}
