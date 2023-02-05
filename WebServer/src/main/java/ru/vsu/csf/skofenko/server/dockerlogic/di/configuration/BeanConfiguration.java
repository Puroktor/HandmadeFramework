package ru.vsu.csf.skofenko.server.dockerlogic.di.configuration;

import ru.vsu.csf.skofenko.server.dockerlogic.di.factory.StandardBeanFactory;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post.PostProcessor;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.initialision.InitialisationProcessor;

import java.util.List;

public interface BeanConfiguration {
    List<InitialisationProcessor> getInitialisationProcessors();

    List<StandardBeanFactory> getStandardBeanFactories();

    List<PostProcessor> getPostProcessors();
}
