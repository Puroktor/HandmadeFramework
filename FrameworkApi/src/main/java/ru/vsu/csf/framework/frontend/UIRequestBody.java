package ru.vsu.csf.framework.frontend;

import ru.vsu.csf.framework.frontend.field.UIField;

import java.util.List;

public interface UIRequestBody {
    String getEntityName();
    List<UIField> getFields();
}
