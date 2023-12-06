package com.facilio.fields.fieldBuilder;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppFieldListHandler<T extends FieldListBuilder<T>> implements AppFieldListBuilder<T>  {
    private final T fieldListBuilder;
    @Getter
    private List<String> onelevelFieldsToSkip;
    @Getter
    private List<String> fieldsToSkip;
    @Getter
    private List<String> fieldsToAdd;

    public AppFieldListHandler(T fieldListBuilder) {
        this.fieldListBuilder = fieldListBuilder;
    }

    @Override
    public AppFieldListBuilder<T> addFields(List<String> fieldNames) {
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            if (fieldsToAdd == null) {
                fieldsToAdd = new ArrayList<>();
            }
            fieldsToAdd.addAll(fieldNames);
        }
        return this;
    }

    @Override
    public AppFieldListBuilder<T> skipFields(List<String> fieldNames){
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            if (fieldsToSkip == null) {
                fieldsToSkip = new ArrayList<>();
            }
            fieldsToSkip.addAll(fieldNames);
        }
        return this;
    }

    @Override
    public AppFieldListBuilder<T> skipOnelevelFields(List<String> onelevelFieldNames){
        if (CollectionUtils.isNotEmpty(onelevelFieldNames)) {
            if (onelevelFieldsToSkip == null) {
                onelevelFieldsToSkip = new ArrayList<>();
            }
            onelevelFieldsToSkip.addAll(onelevelFieldNames);
        }
        return this;
    }

    @Override
    public T appFieldConfigDone() {
        return fieldListBuilder;
    }
}
