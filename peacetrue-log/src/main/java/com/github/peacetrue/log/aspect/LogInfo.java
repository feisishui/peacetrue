package com.github.peacetrue.log.aspect;

import com.github.peacetrue.log.service.AbstractLog;

import java.lang.annotation.*;

/**
 * 日志信息
 *
 * @author xiayx
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogInfo {

    /** 记录主键表达式，用于从{@link AfterMethodBasedEvaluationContext}中获取{@link AbstractLog#recordId} */
    String recordId() default "";

    /** 描述表达式，用于从{@link AfterMethodBasedEvaluationContext}中获取{@link AbstractLog#description} */
    String description();
}