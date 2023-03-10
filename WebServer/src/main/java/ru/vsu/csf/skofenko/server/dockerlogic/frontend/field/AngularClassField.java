package ru.vsu.csf.skofenko.server.dockerlogic.frontend.field;

import lombok.Getter;
import ru.vsu.csf.framework.frontend.field.UIField;

import java.util.List;

@Getter
public class AngularClassField extends AngularUIField {
    private final List<UIField> innerFields;

    public AngularClassField(String displayName, String submitName, boolean required, List<UIField> innerFields) {
        super(displayName, submitName, FieldType.CLASS, required);
        this.innerFields = innerFields;
    }
}
