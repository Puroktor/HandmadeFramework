package ru.vsu.csf.framework.frontend;

public record UIField(String name, Type type) {

    public enum Type {
        TEXT
    }
}
