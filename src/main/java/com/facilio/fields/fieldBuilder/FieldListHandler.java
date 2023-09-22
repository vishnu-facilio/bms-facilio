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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class FieldListHandler implements FieldListBuilder {
    private final FieldConfig parent;

    public FieldListHandler(FieldConfig parent) {
        this.parent = parent;
    }

    @Getter
    private Command afterFetchCommand;
    @Getter
    private List<String> fieldsToAdd;
    @Getter
    private List<String> fieldsToSkip;
    @Getter
    private List<FieldType> fieldTypesToSkip;
    @Getter
    private Map<String, List<String>> configSpecificFields;


    @Override
    public FieldListBuilder afterFetch(Command... afterFetchCommand) {
        if (afterFetchCommand != null) {
            this.afterFetchCommand = buildTransactionChain(afterFetchCommand);
        }
        return this;
    }

    @Override
    public FieldListBuilder skip(List<String> fieldNames){
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
    public FieldListBuilder fieldTypesToSkip(List<FieldType> fieldTypesToSkip) {
        if (CollectionUtils.isNotEmpty(fieldTypesToSkip)) {
            if (this.fieldTypesToSkip == null) {
                this.fieldTypesToSkip = new ArrayList<>();
            }
            this.fieldTypesToSkip.addAll(fieldTypesToSkip);
        }
        return this;
    }

    @Override
    public FieldListBuilder add(List<String> fieldNames){
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
    public FieldListBuilder addConfigSpecificFields(String name, List<String> fieldNames) {
        if (StringUtils.isNotEmpty(name) && CollectionUtils.isNotEmpty(fieldNames)) {
            if (configSpecificFields == null) {
                configSpecificFields = new HashMap<>();
            }
            if (!configSpecificFields.containsKey(name)) {
                configSpecificFields.put(name, new ArrayList<>());
            }
            configSpecificFields.get(name).addAll(fieldNames);
        }
        return this;
    }

    @Override
    public FieldConfig done() {
        return this.parent;
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
