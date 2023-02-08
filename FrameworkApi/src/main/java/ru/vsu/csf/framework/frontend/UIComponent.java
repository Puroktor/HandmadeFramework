package ru.vsu.csf.framework.frontend;

import java.util.List;

public interface UIComponent {
    String getName();
    String getFileName();
    String getScriptName();
    boolean addEndpoint(UIEndpoint uiEndpoint);

    List<UIEndpoint> getEndpoints();
}
