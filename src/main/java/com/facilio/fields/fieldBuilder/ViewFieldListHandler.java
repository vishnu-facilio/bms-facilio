package com.facilio.fields.fieldBuilder;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.chain.FacilioChain;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class ViewFieldListHandler implements ViewFieldListBuilder{

    private final FieldConfig parent;

    public ViewFieldListHandler(FieldConfig parent) {
        this.parent = parent;
    }

    @Getter
    private Command afterFetchCommand;

    public List<String> getFieldsToAdd() {
        return CollectionUtils.isNotEmpty(fieldsToAdd)?Collections.unmodifiableList(fieldsToAdd):null;
    }

    public List<String> getFieldsToSkip() {
        return CollectionUtils.isNotEmpty(fieldsToSkip)?Collections.unmodifiableList(fieldsToSkip):null;
    }

    public List<FieldType> getFieldTypesToSkip() {
        return CollectionUtils.isNotEmpty(fieldTypesToSkip)?Collections.unmodifiableList(fieldTypesToSkip):null;
    }

    public List<String> getFixedFields() {
        return CollectionUtils.isNotEmpty(fixedFields)?Collections.unmodifiableList(fixedFields):null;
    }

    public List<String> getFixedSelectableFields() {
        return CollectionUtils.isNotEmpty(fixedSelectableFields)?Collections.unmodifiableList(fixedSelectableFields):null;
    }

    public Map<String, JSONObject> getCustomization() {
        return MapUtils.isNotEmpty(customization)?Collections.unmodifiableMap(customization):null;
    }

    private List<String> fieldsToAdd;

    private List<String> fieldsToSkip;

    private List<FieldType> fieldTypesToSkip;

    private List<String> fixedFields;

    private List<String> fixedSelectableFields;

    private Map<String, JSONObject> customization;

    @Override
    public ViewFieldListBuilder fieldTypesToSkip(List<FieldType> fieldTypesToSkip) {
        if (CollectionUtils.isNotEmpty(fieldTypesToSkip)) {
            if (this.fieldTypesToSkip == null) {
                this.fieldTypesToSkip = new ArrayList<>();
            }
            this.fieldTypesToSkip.addAll(fieldTypesToSkip);
        }
        return this;
    }

    @Override
    public ViewFieldListBuilder add(List<String> fieldNames) {
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            validateAndThrowErrorIfDevelopment(CollectionUtils.isNotEmpty(fieldsToAdd), "Add", "skip");
            if (fieldsToAdd == null) {
                fieldsToAdd = new ArrayList<>();
            }
            fieldsToAdd.addAll(fieldNames);
        }
        return this;
    }

    @Override
    public ViewFieldListBuilder skip(List<String> fieldNames) {
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            validateAndThrowErrorIfDevelopment(CollectionUtils.isNotEmpty(fieldsToAdd), "Skip", "add");
            if (fieldsToSkip == null) {
                fieldsToSkip = new ArrayList<>();
            }
            fieldsToSkip.addAll(fieldNames);
        }
        return this;
    }

    @Override
    public ViewFieldListBuilder afterFetch(Command... afterFetchCommand) {
        if (afterFetchCommand != null) {
            this.afterFetchCommand = buildTransactionChain(afterFetchCommand);
        }
        return this;
    }

    @Override
    public FieldConfig done() {
        return this.parent;
    }

    @Override
    public ViewFieldListBuilder addFixedFields(List<String> fieldNames) {
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            if (fixedFields == null) {
                fixedFields = new ArrayList<>();
            }
            fixedFields.addAll(fieldNames);
        }
        return this;
    }

    @Override
    public ViewFieldListBuilder addFixedSelectableFields(List<String> fieldNames) {
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            if (fixedSelectableFields == null) {
                fixedSelectableFields = new ArrayList<>();
            }
            fixedSelectableFields.addAll(fieldNames);
        }
        return this;
    }

    @Override
    public ViewFieldListBuilder addConfigForField(String fieldName, int width, String textWrap) {
        if(customization == null) {
            customization = new HashMap<>();
        }
        JSONObject columnCustomization = new JSONObject();
        columnCustomization.put("columnWidth", width);
        columnCustomization.put("text-wrap", textWrap);

        customization.put(fieldName, columnCustomization);

        return this;
    }

    private static FacilioChain buildTransactionChain(Command[] facilioCommands) {
        FacilioChain c = FacilioChain.getNonTransactionChain();
        for (Command facilioCommand : facilioCommands) {
            c.addCommand(facilioCommand);
        }
        return c;
    }

    @SneakyThrows
    private static void validateAndThrowErrorIfDevelopment(boolean throwError, String configuringFieldList, String existingFieldListConfigured){
        if(throwError) {
            FacilioUtil.throwIllegalArgumentException(FacilioProperties.isDevelopment(), configuringFieldList + " fields can't be configured while " + existingFieldListConfigured + " fields is configured in the fieldConfiguration");
            LOGGER.info(configuringFieldList + " fields can't be configured while " + existingFieldListConfigured + " fields is configured in the fieldConfiguration");
        }
    }
}
