package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import ru.vsu.csf.framework.frontend.UIComponent;
import ru.vsu.csf.framework.frontend.UIEndpoint;

import java.util.ArrayList;
import java.util.List;

public class AngularComponent implements UIComponent {
    private final List<UIEndpoint> uiEndpoint = new ArrayList<>();
    private final String name;
    private final String fileName;
    private final String scriptName;

    public AngularComponent(String name) {
        this.name = name;
        this.fileName = name.toLowerCase();
        this.scriptName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getScriptName() {
        return scriptName;
    }

    @Override
    public boolean addEndpoint(UIEndpoint uiEndpoint) {
        return this.uiEndpoint.add(uiEndpoint);
    }

    @Override
    public List<UIEndpoint> getEndpoints() {
        return List.copyOf(uiEndpoint);
    }
}
