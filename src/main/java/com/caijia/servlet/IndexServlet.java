package com.caijia.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caijia.framework.ModelAndView;
import com.caijia.framework.ViewEngine;

@WebServlet("/")
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 4395086442995508028L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ViewEngine.render(new ModelAndView("/index.html"), resp);
	}
}
