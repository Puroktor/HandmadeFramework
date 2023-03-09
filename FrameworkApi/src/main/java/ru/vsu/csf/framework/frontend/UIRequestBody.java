package ru.vsu.csf.framework.frontend;

import ru.vsu.csf.framework.frontend.field.UIField;

import java.util.List;

public record UIRequestBody(String entityName, List<UIField> fields) {
}
