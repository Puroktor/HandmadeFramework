package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.initialision;

import java.util.List;

public interface InitialisationProcessor {
    List<Object> initialise(Class<?> clazz);
}
