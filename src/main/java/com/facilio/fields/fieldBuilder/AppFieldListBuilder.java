package com.facilio.fields.fieldBuilder;

import java.util.List;

public interface AppFieldListBuilder<T extends FieldListBuilder<T>> {

    AppFieldListBuilder<T> skipFields(List<String> fieldNames);

    AppFieldListBuilder<T> skipOnelevelFields(List<String> fieldNames);

    T appFieldConfigDone();

}
