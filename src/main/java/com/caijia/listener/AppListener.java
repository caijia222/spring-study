package com.caijia.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class AppListener implements ServletContextListener{
	private static final Logger log = LoggerFactory.getLogger(AppListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("执行AppListener");
	}
}
