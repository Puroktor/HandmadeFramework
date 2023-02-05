package ru.vsu.csf.skofenko.server.dockerlogic.di.configuration;

import ru.vsu.csf.skofenko.server.dockerlogic.di.factory.EntityManagerFactory;
import ru.vsu.csf.skofenko.server.dockerlogic.di.factory.ResourceManagerFactory;
import ru.vsu.csf.skofenko.server.dockerlogic.di.factory.StandardBeanFactory;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.initialision.ComponentProcessor;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.initialision.ConfigProcessor;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.initialision.InitialisationProcessor;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post.ControllerPostProcessor;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post.ExceptionHandlerPostProcessor;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post.InjectPostProcessor;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post.PostProcessor;

import java.util.List;

public class WebBeanConfiguration implements BeanConfiguration {
    @Override
    public List<InitialisationProcessor> getInitialisationProcessors() {
        return List.of(new ComponentProcessor(), new ConfigProcessor());
    }

    @Override
    public List<StandardBeanFactory> getStandardBeanFactories() {
        return List.of(new EntityManagerFactory(), new ResourceManagerFactory());
    }

    @Override
    public List<PostProcessor> getPostProcessors() {
        return List.of(new InjectPostProcessor(), new ControllerPostProcessor(), new ExceptionHandlerPostProcessor());
    }
}
