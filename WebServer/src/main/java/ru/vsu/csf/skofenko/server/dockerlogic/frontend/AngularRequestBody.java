package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.vsu.csf.framework.frontend.UIRequestBody;
import ru.vsu.csf.framework.frontend.field.UIField;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class AngularRequestBody implements UIRequestBody {
    private final String entityName;
    private final List<UIField> fields;
}
