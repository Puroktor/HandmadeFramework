package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import ru.vsu.csf.framework.frontend.FrontComponent;
import ru.vsu.csf.framework.frontend.FrontEndpoint;

import java.util.ArrayList;
import java.util.List;

public class AngularComponent implements FrontComponent {
    private final List<FrontEndpoint> frontEndpoints = new ArrayList<>();
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
    public boolean addEndpoint(FrontEndpoint frontEndpoint) {
        return frontEndpoints.add(frontEndpoint);
    }

    @Override
    public List<FrontEndpoint> getEndpoints() {
        return List.copyOf(frontEndpoints);
    }
}
