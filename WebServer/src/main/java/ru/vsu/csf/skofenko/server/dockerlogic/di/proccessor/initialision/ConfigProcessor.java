package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.initialision;

import ru.vsu.csf.framework.di.Bean;
import ru.vsu.csf.framework.di.Config;
import ru.vsu.csf.skofenko.server.dockerlogic.di.BeanUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigProcessor implements InitialisationProcessor {
    @Override
    public List<Object> initialise(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Config.class)) {
            return Collections.emptyList();
        }
        List<Object> beans = new ArrayList<>();
        Object config = BeanUtils.initialise(clazz);
        beans.add(config);
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                if (method.getReturnType().equals(Void.TYPE)) {
                    throw new IllegalStateException("%s Bean method is void".formatted(method));
                }
                Object bean = BeanUtils.createBean(method, config);
                beans.add(bean);
            }
        }
        return beans;
    }
}
