package com.caijia.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import lombok.extern.slf4j.Slf4j;

@WebListener
@Slf4j
public class AppListener implements ServletContextListener{
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("执行AppListener");
	}
}
