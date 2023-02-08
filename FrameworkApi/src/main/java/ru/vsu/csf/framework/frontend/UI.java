package ru.vsu.csf.framework.frontend;

public interface UI extends Runnable {
    boolean addComponent(UIComponent uiComponent);

    boolean create();
}
