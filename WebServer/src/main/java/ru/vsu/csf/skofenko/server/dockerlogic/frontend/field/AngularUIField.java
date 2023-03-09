package ru.vsu.csf.skofenko.server.dockerlogic.frontend.field;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.vsu.csf.framework.frontend.field.UIField;

@RequiredArgsConstructor
@Getter
public class AngularUIField implements UIField {
    private final String displayName;
    private final String submitName;
    private final FieldType fieldType;
    private final boolean required;
}
