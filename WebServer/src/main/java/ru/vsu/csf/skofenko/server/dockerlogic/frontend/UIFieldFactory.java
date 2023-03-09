package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import org.apache.commons.lang3.ClassUtils;
import ru.vsu.csf.framework.frontend.DisplayName;
import ru.vsu.csf.framework.frontend.field.UIField;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.field.AngularEnumField;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.field.AngularUIField;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UIFieldFactory {
    public static UIField createUIField(AnnotatedElement field, Class<?> typeClass, String submitName) {
        Class<?> filedClass = ClassUtils.primitiveToWrapper(typeClass);
        String displayName = getFieldDisplayName(field, filedClass, submitName);
        if (Number.class.isAssignableFrom(filedClass)) {
            return new AngularUIField(displayName, submitName, UIField.FieldType.NUMBER, true);
        } else if (filedClass.isEnum()) {
            Map<String, String> submitToDisplayValues = Arrays.stream(filedClass.getFields()).collect(
                    Collectors.toMap(
                            Field::getName, enumField -> getFieldDisplayName(enumField, enumField.getType(), enumField.getName())
                    ));
            return new AngularEnumField(displayName, submitName, true, submitToDisplayValues);
        } else if (Boolean.class.isAssignableFrom(filedClass)) {
            return new AngularUIField(displayName, submitName, UIField.FieldType.BOOL, true);
        } else {
            return new AngularUIField(displayName, submitName, UIField.FieldType.TEXT, true);
        }
    }

    public static String getFieldDisplayName(AnnotatedElement element, Class<?> typeClass, String submitName) {
        DisplayName nameAnnotation = element.getDeclaredAnnotation(DisplayName.class);
        if (nameAnnotation == null) {
            DisplayName classNameAnnotation = typeClass.getDeclaredAnnotation(DisplayName.class);
            return classNameAnnotation == null ? submitName : classNameAnnotation.value();
        } else {
            return nameAnnotation.value();
        }
    }
}
