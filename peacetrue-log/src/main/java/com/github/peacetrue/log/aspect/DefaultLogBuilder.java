package com.github.peacetrue.log.aspect;

import com.github.peacetrue.log.LogProperties;
import com.github.peacetrue.log.service.AbstractLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;

/**
 * 默认的日志构建器
 *
 * @author xiayx
 */
public class DefaultLogBuilder extends AbstractLogBuilder {

    @Autowired
    private ExpressionParser expressionParser;
    @Autowired
    private CreatorIdProvider creatorIdProvider;
    private LogProperties logProperties;
    private Class<?> recordIdType;

    @Autowired
    public void setLogProperties(LogProperties logProperties) {
        this.logProperties = logProperties;
        this.recordIdType = BeanUtils.getPropertyDescriptor(logProperties.getConcreteClass(), "recordId").getPropertyType();
    }

    @Override
    protected AbstractLog instance(AfterMethodBasedEvaluationContext context) {
        return BeanUtils.instantiate(logProperties.getConcreteClass());
    }

    @Override
    protected String parseModuleCode(AfterMethodBasedEvaluationContext context) {
        String code = context.getMethod().getAnnotation(Operate.class).module().code();
        return code.equals("") ? context.getTarget().getClass().getAnnotation(Module.class).code() : code;
    }

    @Override
    protected Object parseRecordId(AfterMethodBasedEvaluationContext context) {
        LogInfo logInfo = context.getMethod().getAnnotation(LogInfo.class);
        if (logInfo.recordId().equals("")) return null;
        Expression expression = expressionParser.parseExpression(logInfo.recordId());
        return expression.getValue(context, recordIdType);
    }

    @Override
    protected String parseOperateCode(AfterMethodBasedEvaluationContext context) {
        return context.getMethod().getAnnotation(Operate.class).code();
    }

    @Override
    protected String parseDescription(AfterMethodBasedEvaluationContext context) {
        LogInfo logInfo = context.getMethod().getAnnotation(LogInfo.class);
        Expression expression = expressionParser.parseExpression(logInfo.description(), ParserContext.TEMPLATE_EXPRESSION);
        return expression.getValue(context, String.class);
    }

    @Override
    protected Object parseCreatorId(AfterMethodBasedEvaluationContext context) {
        return creatorIdProvider.getCreatorId(context);
    }
}
