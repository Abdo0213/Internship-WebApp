package com.example.user_service.aop;
import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.config.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.example.user_service.exception.JwtAuthenticationException;


import java.lang.reflect.Method;

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
        if (requestURI.startsWith("/auth/")) {
            return joinPoint.proceed();  // Skip token validation and proceed with the method
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            throw new JwtAuthenticationException("Missing or malformed Authorization header");
        }

        String token = authHeader.substring(7);  // Remove "Bearer "
        if (!jwtUtil.validateToken(token)) {
            throw new JwtAuthenticationException("Invalid token");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (!isValidRole(role)) {
            throw new JwtAuthenticationException("Invalid role in token");
        }
        request.setAttribute("username", username); // Pass username to the controller
        request.setAttribute("role", role);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        if (jwtValidation.requiredRoles().length > 0) {
            boolean hasRequiredRole = false;
            for (String requiredRole : jwtValidation.requiredRoles()) {
                if (requiredRole.equalsIgnoreCase(role)) {
                    hasRequiredRole = true;
                    break;
                }
            }
            if (!hasRequiredRole) {
                throw new JwtAuthenticationException("Insufficient privileges");
            }
        }

        return joinPoint.proceed();
    }
    private boolean isValidRole(String role) {
        return role != null &&
                (role.equalsIgnoreCase("admin") ||
                        role.equalsIgnoreCase("hr") ||
                        role.equalsIgnoreCase("student"));
    }
}
