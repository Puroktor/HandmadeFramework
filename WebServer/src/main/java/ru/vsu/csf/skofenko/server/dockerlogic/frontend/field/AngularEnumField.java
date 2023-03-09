package ru.vsu.csf.skofenko.server.dockerlogic.frontend.field;

import lombok.Getter;
import ru.vsu.csf.framework.frontend.field.EnumField;

import java.util.Map;

@Getter
public class AngularEnumField extends AngularUIField implements EnumField {
    private final Map<String, String> submitToDisplayValues;

    public AngularEnumField(String displayName, String submitName, boolean required, Map<String, String> submitToDisplayValues) {
        super(displayName, submitName, FieldType.ENUM, required);
        this.submitToDisplayValues = submitToDisplayValues;
    }
}
