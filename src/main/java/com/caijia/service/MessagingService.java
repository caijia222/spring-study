package com.caijia.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.caijia.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MessagingService {
	final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	JmsTemplate jmsTemplate;
	
	public void sendMialMessage(User user) throws Exception {
		String text = objectMapper.writeValueAsString(user);
		logger.info("jmsTemplate send mailMsg");
		jmsTemplate.send("jms/queue/mail", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(text);
			}
		});
	}
}
