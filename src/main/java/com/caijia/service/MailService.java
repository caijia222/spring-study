package com.caijia.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.caijia.entity.User;

@Component
public class MailService {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	@Qualifier("utc8")
	private ZoneId zoneId = ZoneId.systemDefault();

	@PostConstruct
	public void init() {
		log.info("Init mail service with zoneId = " + this.zoneId);
	}

	@PreDestroy
	public void shutdown() {
		log.info("Shutdown mail service");
	}

	public String getTime() {
		return ZonedDateTime.now(zoneId).format(DateTimeFormatter.ISO_DATE_TIME);
	}

	public void sendLoginMail(User user) {
		log.info(String.format("Hi, %s! You are logged in at %s", user.getName(), getTime()));
	}

	@Transactional
	public void sendRegistrationMail(User user) {
		log.info(String.format("Welcome, %s!", user.getName()));
	}
}
