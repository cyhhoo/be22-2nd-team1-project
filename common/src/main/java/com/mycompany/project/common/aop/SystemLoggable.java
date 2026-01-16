package com.mycompany.project.common.aop;

import com.mycompany.project.common.entity.ChangeType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemLoggable {
    ChangeType type(); // CREATE, UPDATE, DELETE ??

    int tableCodeId() default 0; // ????뚯씠釉?肄붾뱶 (?좏깮)
}