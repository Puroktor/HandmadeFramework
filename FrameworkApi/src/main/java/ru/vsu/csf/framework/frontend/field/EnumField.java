package ru.vsu.csf.framework.frontend.field;

import java.util.Map;

public interface EnumField extends UIField {
    Map<String, String> getSubmitToDisplayValues();
}
