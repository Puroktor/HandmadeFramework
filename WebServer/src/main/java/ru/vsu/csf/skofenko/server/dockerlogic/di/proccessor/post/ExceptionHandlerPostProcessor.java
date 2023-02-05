package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post;

import ru.vsu.csf.framework.di.ExceptionHandler;
import ru.vsu.csf.framework.http.ExceptionMapping;
import ru.vsu.csf.skofenko.server.dockerlogic.Endpoint;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;

import java.lang.reflect.Method;

public class ExceptionHandlerPostProcessor implements PostProcessor {
    @Override
    public <T> T process(T bean, ApplicationContext applicationContext) {
        Class<?> clazz = bean.getClass();
        ExceptionHandler annotation = clazz.getDeclaredAnnotation(ExceptionHandler.class);
        if (annotation != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                ExceptionMapping exceptionMapping = method.getAnnotation(ExceptionMapping.class);
                if (exceptionMapping != null) {
                    applicationContext.getEndpointManager().putExceptionPoint(exceptionMapping.value(), new Endpoint(bean, method));
                }
            }
        }
        return bean;
    }
}
