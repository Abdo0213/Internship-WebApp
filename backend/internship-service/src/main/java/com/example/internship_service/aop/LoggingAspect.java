package com.example.internship_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.example.internship_service.services..*(..)) || execution(* com.example.internship_service.controller..*(..))")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // Record start time
        long startTime = System.currentTimeMillis();

        // Get method signature
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Execute the method
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            // Log failure with execution time
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Method {}::{} failed after {} ms with exception: {}",
                    className, methodName, executionTime, ex.getMessage());
            throw ex;
        }

        // Calculate execution time
        long executionTime = System.currentTimeMillis() - startTime;

        // Log successful execution
        logger.info("Method {}::{} executed successfully in {} ms", className, methodName, executionTime);

        return result;
    }
}
