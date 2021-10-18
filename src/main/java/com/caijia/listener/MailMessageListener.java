package com.caijia.listener;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.caijia.entity.User;
import com.caijia.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MailMessageListener {

	 final Logger logger = LoggerFactory.getLogger(getClass());
	 @Autowired ObjectMapper objectMapper;
	 @Autowired MailService mailService;
	 
	 @JmsListener(destination = "jms/queue/mail" , concurrency = "10")
	 public void onMailMessageReceived(Message msg) throws Exception {
		 logger.info("received message :" + msg);
		 if(msg instanceof TextMessage) {
			 String text = ((TextMessage)msg).getText();
			 User user = objectMapper.readValue(text, User.class);
			 mailService.sendRegistrationMail(user);
		 }else {
			 logger.error("unable to process non-text message!");
		 }
	 }
	 
}
