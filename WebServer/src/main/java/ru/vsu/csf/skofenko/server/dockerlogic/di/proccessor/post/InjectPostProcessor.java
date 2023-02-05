package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;

import java.util.Arrays;

public class InjectPostProcessor implements PostProcessor {
    @Override
    public <T> T process(T bean, ApplicationContext applicationContext) {
        Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        field.set(bean, applicationContext.getBean(field.getType()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("No access to field");
                    }
                });
        return bean;
    }
}
