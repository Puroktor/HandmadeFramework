package ru.vsu.csf.skofenko.server.dockerlogic.frontend.field;

import lombok.Getter;
import ru.vsu.csf.framework.frontend.field.ListField;
@Getter
public class AngularListField extends AngularUIField implements ListField {

    private final FieldType elementType;

    public AngularListField(String displayName, String submitName, boolean required, FieldType elementType) {
        super(displayName, submitName, FieldType.LIST, required);
        this.elementType = elementType;
    }
}
