package com.caijia.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

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
