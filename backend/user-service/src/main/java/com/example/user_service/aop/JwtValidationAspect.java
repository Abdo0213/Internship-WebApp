package com.example.user_service.aop;
import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.config.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/*
@Aspect
@Component
public class JwtValidationAspect{
    private final JwtUtil jwtUtil;

    public JwtValidationAspect(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Around("@annotation(jwtValidation)")
    public Object validateJwt(ProceedingJoinPoint joinPoint, JwtValidation jwtValidation) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Missing token");
        }

        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        request.setAttribute("username", username); // Pass to controller

        return joinPoint.proceed();
    }
}
*/
@Aspect
@Component
public class JwtValidationAspect {
    private final JwtUtil jwtUtil;

    public JwtValidationAspect(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Around("@annotation(jwtValidation)")  // This will intercept methods annotated with @JwtValidation
    public Object validateJwt(ProceedingJoinPoint joinPoint, JwtValidation jwtValidation) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        String requestURI = request.getRequestURI();

        // Skip validation for registration endpoint
        if (requestURI.startsWith("/api/auth/")) {
            return joinPoint.proceed();  // Skip token validation and proceed with the method
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Missing token");
        }

        token = token.substring(7);  // Extract the token part
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        request.setAttribute("username", username); // Pass username to the controller

        return joinPoint.proceed();  // Proceed with method execution
    }
}
