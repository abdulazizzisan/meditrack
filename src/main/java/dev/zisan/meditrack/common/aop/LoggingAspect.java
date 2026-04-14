package dev.zisan.meditrack.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

	@Around("@annotation(dev.zisan.meditrack.common.aop.Loggable)")
	public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		String methodName = joinPoint.getSignature().toShortString();

		log.info("Entering {}", methodName);

		try {
			Object result = joinPoint.proceed();
			long duration = System.currentTimeMillis() - startTime;
			log.info("Completed {} in {} ms", methodName, duration);
			return result;
		} catch (Throwable throwable) {
			long duration = System.currentTimeMillis() - startTime;
			log.warn("Failed {} in {} ms: {}", methodName, duration, throwable.getMessage());
			throw throwable;
		}
	}
}
