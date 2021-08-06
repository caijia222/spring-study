package com.caijia.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.caijia.bean.User;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MailService {
	@Autowired(required = false)
	@Qualifier("utc8")
	private ZoneId zoneId = ZoneId.systemDefault();

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

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

	public void sendRegistrationMail(User user) {
		log.info(String.format("Welcome, %s!", user.getName()));
	}
}
