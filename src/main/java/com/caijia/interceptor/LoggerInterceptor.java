package com.caijia.interceptor;

import java.io.PrintWriter;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Order(1)
@Component
public class LoggerInterceptor implements HandlerInterceptor {
	private static final Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("preHandle {}...", request.getRequestURI());
		if (request.getParameter("debug") != null) {
			PrintWriter pw = response.getWriter();
			pw.write("<p>DEBUG MODE</p>");
			pw.flush();
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("postHandle {}.", request.getRequestURI());
		if (modelAndView != null) {
			modelAndView.addObject("__time__", LocalDateTime.now());
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.info("afterCompletion {}: exception = {}", request.getRequestURI(), ex);
	}

}
