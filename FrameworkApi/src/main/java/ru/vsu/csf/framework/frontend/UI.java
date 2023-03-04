package ru.vsu.csf.framework.frontend;

public interface UI extends Runnable {

    String getBaseUrl();

    boolean addComponent(UIComponent uiComponent);

    boolean create(boolean overrideUI);
}
