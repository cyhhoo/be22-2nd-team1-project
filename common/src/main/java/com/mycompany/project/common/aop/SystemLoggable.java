package com.mycompany.project.common.aop;

import com.mycompany.project.common.entity.ChangeType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemLoggable {
    ChangeType type(); // CREATE, UPDATE, DELETE 등

    int tableCodeId() default 0; // 대상 테이블 코드 (선택)
}