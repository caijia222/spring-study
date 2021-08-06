package com.caijia.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class MetricAspect {
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
