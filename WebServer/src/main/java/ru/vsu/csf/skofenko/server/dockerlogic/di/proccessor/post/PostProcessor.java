package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post;

import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;

public interface PostProcessor {
    <T> T process(T bean, ApplicationContext applicationContext);
}
