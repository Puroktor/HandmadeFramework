package ru.vsu.csf.skofenko.server.dockerlogic.di;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanUtils {

    public static Object initialise(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException("Couldn't create instance of class", e);
        }
    }

    public static Object createBean(Method method, Object config) {
        try {
            return method.invoke(config);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("%s Could not create a bean!".formatted(method), e);
        }
    }
}
