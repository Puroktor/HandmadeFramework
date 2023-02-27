package ru.vsu.csf.framework.frontend;

import java.util.List;

public record UIRequestBody(String entityName, List<UIField> fields) {
}
