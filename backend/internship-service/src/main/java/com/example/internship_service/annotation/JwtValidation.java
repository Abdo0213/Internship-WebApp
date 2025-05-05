package com.example.internship_service.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JwtValidation {
    String[] requiredRoles() default {};
}