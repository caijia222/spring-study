package com.caijia.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caijia.framework.ViewEngine;

@WebListener
public class AppListener implements ServletContextListener{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("初始化模板引擎-开始");
		ViewEngine.init(sce.getServletContext());
		logger.info("初始化模板引擎-结束");
	}
}
