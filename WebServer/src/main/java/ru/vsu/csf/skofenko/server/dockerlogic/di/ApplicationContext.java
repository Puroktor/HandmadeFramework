package ru.vsu.csf.skofenko.server.dockerlogic.di;

import ru.vsu.csf.framework.di.ResourceManager;
import ru.vsu.csf.framework.frontend.UI;
import ru.vsu.csf.skofenko.server.dockerlogic.EndpointManager;
import ru.vsu.csf.skofenko.server.dockerlogic.di.configuration.BeanConfiguration;
import ru.vsu.csf.skofenko.server.dockerlogic.di.configuration.WebBeanConfiguration;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.initialision.InitialisationProcessor;
import ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post.PostProcessor;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.AngularUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

public class ApplicationContext {
    private final JarFile jarFile;
    private final EndpointManager endpointManager;
    private final BeanConfiguration beanConfiguration;
    private final Properties properties;
    private final UI ui;
    private Collection<Object> beans;

    public ApplicationContext(JarFile jarFile) {
        this.jarFile = jarFile;
        endpointManager = new EndpointManager();
        beanConfiguration = new WebBeanConfiguration();
        properties = new Properties();
        ui = new AngularUI(ContextLoader.getResourceFile(jarFile));

        Collection<Class<?>> classes = ContextLoader.getAllClasses(jarFile);
        beans = new ArrayList<>();
        for (Class<?> clazz : classes) {
            for (InitialisationProcessor processor : beanConfiguration.getInitialisationProcessors()) {
                beans.addAll(processor.initialise(clazz));
            }
        }

        beanConfiguration.getStandardBeanFactories()
                .stream()
                .map(factory -> factory.createBean(this))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(beans::add);

        beans = beans.stream().map(this::runPostProcessors).toList();
        loadProperties();
    }

    private void loadProperties() {
        try {
            String propertiesPath = getBean(ResourceManager.class).getPath("application.properties");
            properties.load(new FileInputStream(propertiesPath));
        } catch (IOException ignored) {
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    private <T> T runPostProcessors(T initialBean) {
        T bean = initialBean;
        for (PostProcessor processor : beanConfiguration.getPostProcessors()) {
            bean = processor.process(bean, this);
        }
        return bean;
    }

    public <T> T getBean(Class<T> clazz) {
        List<Object> suitableBeans = beans.stream()
                .filter(object -> clazz.isAssignableFrom(object.getClass()))
                .toList();
        if (suitableBeans.size() != 1) {
            throw new IllegalStateException("0 or > 1 bean(s) of class" + clazz);
        }
        return (T) suitableBeans.get(0);
    }


    public EndpointManager getEndpointManager() {
        return endpointManager;
    }

    public Collection<Object> getBeans() {
        return beans;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public UI getUI() {
        return ui;
    }
}