package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ClassUtils;
import ru.vsu.csf.framework.frontend.DisplayName;
import ru.vsu.csf.framework.frontend.Nullable;
import ru.vsu.csf.framework.frontend.field.UIField;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.field.AngularClassField;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.field.AngularEnumField;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.field.AngularListField;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.field.AngularUIField;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class UIFieldFactory {

    public UIField createUIField(AnnotatedElement field, Type filedType, String submitName) {
        return createUIField(field, filedType, submitName, new HashSet<>(), true);
    }

    public UIField createUIField(AnnotatedElement field, Type filedType, String submitName,
                                 Set<Class<?>> parsedClassesSet, boolean isParentRequired) {
        Class<?> filedClass = getClassFromType(filedType);
        String displayName = getFieldDisplayName(field, filedClass, submitName);
        boolean isRequired = isParentRequired && !field.isAnnotationPresent(Nullable.class);
        if (Number.class.isAssignableFrom(filedClass)) {
            return new AngularUIField(displayName, submitName, UIField.FieldType.NUMBER, isRequired);
        } else if (String.class.isAssignableFrom(filedClass) || parsedClassesSet.contains(filedClass)) {
            return new AngularUIField(displayName, submitName, UIField.FieldType.TEXT, isRequired);
        } else if (filedClass.isEnum()) {
            Map<String, String> submitToDisplayValues = Arrays.stream(filedClass.getFields()).collect(
                    Collectors.toMap(
                            Field::getName, enumField -> getFieldDisplayName(enumField, enumField.getType(), enumField.getName())
                    ));
            return new AngularEnumField(displayName, submitName, isRequired, submitToDisplayValues);
        } else if (Boolean.class.isAssignableFrom(filedClass)) {
            return new AngularUIField(displayName, submitName, UIField.FieldType.BOOL, isRequired);
        } else if (filedClass.isArray()) {
            Class<?> elementClass = filedClass.arrayType();
            UIField.FieldType elementType = getSimpleUIType(elementClass);
            return new AngularListField(displayName, submitName, isRequired, elementType);
        } else if (Iterable.class.isAssignableFrom(filedClass)) {
            Type genericType = ((ParameterizedType) filedType).getActualTypeArguments()[0];
            UIField.FieldType elementType = getSimpleUIType((Class<?>) genericType);
            return new AngularListField(displayName, submitName, isRequired, elementType);
        } else {
            List<Field> innerFields = getAllFields(filedClass);
            List<UIField> uiInnerFields = new ArrayList<>();
            parsedClassesSet.add(filedClass);
            for (Field innerField : innerFields) {
                UIField uiInnerField = createUIField(innerField, innerField.getGenericType(), innerField.getName(),
                        parsedClassesSet, isRequired);
                uiInnerFields.add(uiInnerField);
            }
            return new AngularClassField(displayName, submitName, isRequired, uiInnerFields);
        }
    }

    public String getFieldDisplayName(AnnotatedElement element, Type type, String submitName) {
        DisplayName nameAnnotation = element.getDeclaredAnnotation(DisplayName.class);
        if (nameAnnotation == null) {
            DisplayName classNameAnnotation = ((Class<?>) type).getDeclaredAnnotation(DisplayName.class);
            return classNameAnnotation == null ? submitName : classNameAnnotation.value();
        } else {
            return nameAnnotation.value();
        }
    }

    private UIField.FieldType getSimpleUIType(Class<?> elementClass) {
        elementClass = ClassUtils.primitiveToWrapper(elementClass);
        if (Number.class.isAssignableFrom(elementClass)) {
            return UIField.FieldType.NUMBER;
        } else if (elementClass.isEnum()) {
            return UIField.FieldType.ENUM;
        } else if (Boolean.class.isAssignableFrom(elementClass)) {
            return UIField.FieldType.BOOL;
        } else {
            return UIField.FieldType.TEXT;
        }
    }

    private Class<?> getClassFromType(Type filedType) {
        return filedType instanceof ParameterizedType
                ? (Class<?>) ((ParameterizedType) filedType).getRawType()
                : ClassUtils.primitiveToWrapper((Class<?>) filedType);
    }

    private List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields()).toList();
        result.addAll(filteredFields);
        return result;
    }
}
