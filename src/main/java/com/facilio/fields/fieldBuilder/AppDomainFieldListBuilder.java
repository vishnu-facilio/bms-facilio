package com.facilio.fields.fieldBuilder;

import java.util.List;

public interface  AppDomainFieldListBuilder<T extends FieldListBuilder<T>> {
    AppDomainFieldListBuilder<T> addFields(List<String> fieldNames);

    AppDomainFieldListBuilder<T> skipFields(List<String> fieldNames);

    AppDomainFieldListBuilder<T> skipOnelevelFields(List<String> fieldNames);

    T domainFieldConfigDone();
}
