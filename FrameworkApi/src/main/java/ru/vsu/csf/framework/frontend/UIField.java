package ru.vsu.csf.framework.frontend;

public record UIField(String displayName, String submitName, Type type, boolean isRequired) {

    public enum Type {
        TEXT,
        NUMBER,
        BOOL
    }
}
