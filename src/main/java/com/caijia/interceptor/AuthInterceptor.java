package com.caijia.interceptor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.caijia.entity.User;
import com.caijia.service.UserService;

@Order(2)
@Component
public class AuthInterceptor implements HandlerInterceptor {
	private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

	@Autowired
	UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("pre authenticate {}...", request.getRequestURI());
		try {
			authenticateByHeader(request);
		} catch (RuntimeException e) {
			log.warn("login by authorization header failed.", e);
		}
		return true;
	}

	private void authenticateByHeader(HttpServletRequest req) {
		String authHeader = req.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Basic ")) {
			log.info("try authenticate by authorization header...");
			String up = new String(Base64.getDecoder().decode(authHeader.substring(6)), StandardCharsets.UTF_8);
			int pos = up.indexOf(':');
			if (pos > 0) {
				String email = "";
				String password = "";
				try {
					email = URLDecoder.decode(up.substring(0, pos), "UTF-8");
					password = URLDecoder.decode(up.substring(pos + 1), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				User user = userService.signin(email, password);
				req.getSession().setAttribute("user", user);
				log.info("user {} login by authorization header ok.", email);
			}
		}
	}
}
