package com.caijia.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.caijia.bean.User;
import com.caijia.framework.ModelAndView;
import com.caijia.framework.ViewEngine;
import com.caijia.service.UserService;

@WebServlet("/signin")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = -8661542593934547239L;
	@Autowired
	private UserService userService;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ViewEngine.render(new ModelAndView("/login.html"), resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		User user = userService.login(email, password);
		ViewEngine.render(new ModelAndView("/","user",user), resp);
		
	}
}
