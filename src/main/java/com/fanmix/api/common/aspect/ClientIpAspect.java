package com.fanmix.api.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class ClientIpAspect {

	private static final ThreadLocal<String> clientIpHolder = new ThreadLocal<>();

	// ip 필요한 api 면 Controller 에서 ClientIp 어노테이션 붙여서 aop 적용
	// ThreadLocal 에 IP 저장 그리고 로깅
	@Around("@annotation(ClientIp)")
	public Object logClientIp(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress != null && !ipAddress.isEmpty()) {
			ipAddress = ipAddress.split(",")[0].trim();
		} else {
			ipAddress = request.getRemoteAddr();
		}

		clientIpHolder.set(ipAddress);
		log.info("Client IP: {}", ipAddress);

		try {
			return joinPoint.proceed();
		} finally {
			// 쓰레드 로컬은 반드시 종료될 때 지워줘야됨
			clientIpHolder.remove();
		}
	}

	// ThreadLocal에서 IP 가져오기
	public static String getClientIp() {
		return clientIpHolder.get();
	}
}

