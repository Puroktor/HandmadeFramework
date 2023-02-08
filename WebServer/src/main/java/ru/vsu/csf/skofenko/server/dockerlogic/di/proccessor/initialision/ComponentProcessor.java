package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.initialision;

import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.di.ExceptionHandler;
import ru.vsu.csf.framework.di.Service;
import ru.vsu.csf.skofenko.server.dockerlogic.di.BeanUtils;

import java.util.Collections;
import java.util.List;

public class ComponentProcessor implements InitialisationProcessor {
    @Override
    public List<Object> initialise(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(ExceptionHandler.class) || clazz.isAnnotationPresent(Service.class)) {
            return List.of(BeanUtils.initialise(clazz));
        }
        return Collections.emptyList();
    }
}
