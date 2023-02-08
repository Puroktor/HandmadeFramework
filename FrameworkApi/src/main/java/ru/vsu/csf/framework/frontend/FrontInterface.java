package ru.vsu.csf.framework.frontend;

public interface FrontInterface extends Runnable{
    boolean addComponent(FrontComponent frontComponent);

    boolean createProject();
}
