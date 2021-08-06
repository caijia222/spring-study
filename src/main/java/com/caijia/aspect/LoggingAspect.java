package com.caijia.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Before("execution(public * com.caijia.service.UserService.*(..))")
	public void doAccessCheck() {
		log.info("[before] do access check");
	}
	
	@Around("execution(public * com.caijia.service.MailService.*(..))")
	public Object doLogging(ProceedingJoinPoint pjp) throws Throwable {
		log.info("[Around] start " + pjp.getSignature());
		Object retVal = pjp.proceed();
		log.info("[Around] done " + pjp.getSignature());
		return retVal;
	}
}
