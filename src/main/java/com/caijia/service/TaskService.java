package com.caijia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskService {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Scheduled(initialDelay = 60_000,fixedRate = 60_000)
	public void checkSystemStatusEveryMinutes() {
		logger.info("Start check system status... ");
	}
	
	@Scheduled(initialDelay = 30_000,fixedDelayString = "${task.checkDiskSpace:30000}")
	public void checkDiskSpaceEveryMinute() {
		logger.info("Start check disk space...");
	}
	
	@Scheduled(cron = "${task.report:0 15 2 * * *}")
	public void cronDailyReport() {
		
	}
}
