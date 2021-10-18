package com.caijia.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.caijia.entity.BlackListMBean;

@Order(2)
@Component
public class BlackListInterceptor implements HandlerInterceptor{
	final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	BlackListMBean blackListMBean;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String ip = request.getRemoteAddr();
		logger.info("check ip address {}...", ip);
		if(blackListMBean.shouldBlock(ip)) {
			logger.warn("will block ip {} for it is in blocklist.", ip);
			response.sendError(403);
			return false;
		}
		return true;
	}

}
