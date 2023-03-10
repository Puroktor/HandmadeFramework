package ru.vsu.csf.framework.frontend.field;

public interface UIField {
    String getDisplayName();

    String getSubmitName();

    FieldType getFieldType();

    boolean isRequired();

    enum FieldType {
        TEXT,
        NUMBER,
        BOOL,
        ENUM,
        LIST,
        CLASS
    }
}
