package ru.vsu.csf.framework.frontend;

import java.util.List;

public interface FrontComponent {
    String getName();
    String getFileName();
    String getScriptName();
    boolean addEndpoint(FrontEndpoint frontEndpoint);

    List<FrontEndpoint> getEndpoints();
}
