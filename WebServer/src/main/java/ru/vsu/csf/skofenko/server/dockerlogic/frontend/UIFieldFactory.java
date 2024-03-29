package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ClassUtils;
import ru.vsu.csf.framework.frontend.DisplayName;
import ru.vsu.csf.framework.frontend.Nullable;
import ru.vsu.csf.skofenko.ui.generator.api.field.UIField;
import ru.vsu.csf.skofenko.ui.generator.api.field.ClassField;
import ru.vsu.csf.skofenko.ui.generator.api.field.EnumField;
import ru.vsu.csf.skofenko.ui.generator.api.field.ListField;

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
        if (parsedClassesSet.contains(filedClass)) {
            throw new IllegalArgumentException("Circular element link: " + filedClass);
        }
        String displayName = getFieldDisplayName(field, filedClass, submitName);
        boolean isRequired = isParentRequired && (field == null || !field.isAnnotationPresent(Nullable.class));
        if (Number.class.isAssignableFrom(filedClass)) {
            return new UIField(displayName, submitName, UIField.FieldType.NUMBER, isRequired);
        } else if (String.class.isAssignableFrom(filedClass)) {
            return new UIField(displayName, submitName, UIField.FieldType.TEXT, isRequired);
        } else if (filedClass.isEnum()) {
            Map<String, String> submitToDisplayValues = Arrays.stream(filedClass.getFields()).collect(
                    Collectors.toMap(
                            Field::getName, enumField -> getFieldDisplayName(enumField, enumField.getType(), enumField.getName())
                    ));
            return new EnumField(displayName, submitName, isRequired, submitToDisplayValues);
        } else if (Boolean.class.isAssignableFrom(filedClass)) {
            return new UIField(displayName, submitName, UIField.FieldType.BOOL, isRequired);
        } else if (filedClass.isArray()) {
            Class<?> elementClass = filedClass.getComponentType();
            UIField element = createUIField(null, elementClass, elementClass.getSimpleName(), parsedClassesSet, isRequired);
            return new ListField(displayName, submitName, isRequired, element);
        } else if (Iterable.class.isAssignableFrom(filedClass)) {
            Type genericType = ((ParameterizedType) filedType).getActualTypeArguments()[0];
            UIField element = createUIField(null, genericType, genericType.getTypeName(), parsedClassesSet, isRequired);
            return new ListField(displayName, submitName, isRequired, element);
        } else {
            List<Field> innerFields = getAllFields(filedClass);
            List<UIField> uiInnerFields = new ArrayList<>();
            parsedClassesSet.add(filedClass);
            for (Field innerField : innerFields) {
                UIField uiInnerField = createUIField(innerField, innerField.getGenericType(), innerField.getName(),
                        parsedClassesSet, isRequired);
                uiInnerFields.add(uiInnerField);
            }
            return new ClassField(displayName, submitName, isRequired, uiInnerFields);
        }
    }

    public String getFieldDisplayName(AnnotatedElement element, Type type, String submitName) {
        DisplayName nameAnnotation = element != null ? element.getDeclaredAnnotation(DisplayName.class) : null;
        if (nameAnnotation == null) {
            DisplayName classNameAnnotation = ((Class<?>) type).getDeclaredAnnotation(DisplayName.class);
            return classNameAnnotation == null ? submitName : classNameAnnotation.value();
        } else {
            return nameAnnotation.value();
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
