package ru.vsu.csf.framework.frontend;

public record UIField(String displayName, String submitName, Type type) {

    public enum Type {
        TEXT
    }
}
