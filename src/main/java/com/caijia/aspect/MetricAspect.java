package com.caijia.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetricAspect {
	private static final Logger log = LoggerFactory.getLogger(MetricAspect.class);

	@Around("@annotation(metricTime)")
	public Object metric(ProceedingJoinPoint pjp, MetricTime metricTime) {
		String name = metricTime.value();
		long start = System.currentTimeMillis();
		try {
			return pjp.proceed();
		} catch (Throwable e) {
			log.error("aspect执行目标方法异常", e);
		} finally {
			long t = System.currentTimeMillis() - start;
			log.info("[Metric] " + name + ": 用时 " + t + "ms");
		}
		return null;
	}
}
