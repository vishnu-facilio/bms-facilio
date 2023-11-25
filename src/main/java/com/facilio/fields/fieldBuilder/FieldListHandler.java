package com.facilio.fields.fieldBuilder;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.chain.FacilioChain;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

@Log4j
public class FieldListHandler<T extends  FieldListBuilder<T>> implements FieldListBuilder<T> {
    private final FieldConfig parent;

    public FieldListHandler(FieldConfig parent) {
        this.parent = parent;
    }

    @Getter
    private Command afterFetchCommand;
    private List<String> fieldsToAdd;
    public List<String> getFieldsToAdd() {
        return CollectionUtils.isNotEmpty(fieldsToAdd)?Collections.unmodifiableList(fieldsToAdd):null;
    }
    private List<String> fieldsToSkip;
    public List<String> getFieldsToSkip() {
        return CollectionUtils.isNotEmpty(fieldsToSkip)?Collections.unmodifiableList(fieldsToSkip):null;
    }
    private List<FieldType> fieldTypesToSkip;
    public List<FieldType> getFieldTypesToSkip() {
        return CollectionUtils.isNotEmpty(fieldTypesToSkip)?Collections.unmodifiableList(fieldTypesToSkip):null;
    }
    @Getter
    private Map<AppDomain.AppDomainType, AppDomainFieldListHandler<T>> appDomainFieldsConfigMap;
    @Getter
    private Map<String, AppFieldListHandler<T>> appFieldConfigMap;
    private List<String> onelevelFieldsToSkip;
    public List<String> getOnelevelFieldsToSkip() {
        return CollectionUtils.isNotEmpty(onelevelFieldsToSkip)?Collections.unmodifiableList(onelevelFieldsToSkip):null;
    }
//    @Getter
//    private Map<String, List<String>> configSpecificFields;



    @Override
    public AppDomainFieldListBuilder<T> addConfigForDomain(AppDomain.AppDomainType appDomainType) {
        if(appDomainFieldsConfigMap == null) {
            this.appDomainFieldsConfigMap = new HashMap<>();
        }
        if(!appDomainFieldsConfigMap.containsKey(appDomainType)) {
            appDomainFieldsConfigMap.put(appDomainType, new AppDomainFieldListHandler<T>((T) this));
        }
        return this.appDomainFieldsConfigMap.get(appDomainType);
    }

    @Override
    public AppFieldListBuilder<T> addConfigForApp(String appName) {
        if(appFieldConfigMap == null) {
            this.appFieldConfigMap = new HashMap<>();
        }
        if(!appFieldConfigMap.containsKey(appName)) {
            appFieldConfigMap.put(appName, new AppFieldListHandler<>((T) this));
        }
        return this.appFieldConfigMap.get(appName);

    }

    @Override
    public T afterFetch(Command... afterFetchCommand) {
        if (afterFetchCommand != null) {
            this.afterFetchCommand = buildTransactionChain(afterFetchCommand);
        }
        return (T) this;
    }
    @Deprecated  //avoid usage of this method will be removed soon
    @Override
    public T skip(List<String> fieldNames){
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            if (fieldsToSkip == null) {
                fieldsToSkip = new ArrayList<>();
            }
            fieldsToSkip.addAll(fieldNames);
        }
        return (T) this;
    }

    @Override
    public T skipOnelevelFields(List<String> onelevelFieldNames){
        if (CollectionUtils.isNotEmpty(onelevelFieldNames)) {
            if (onelevelFieldsToSkip == null) {
                onelevelFieldsToSkip = new ArrayList<>();
            }
            onelevelFieldsToSkip.addAll(onelevelFieldNames);
        }
        return (T) this;
    }

    @Override
    public  T fieldTypesToSkip(List<FieldType> fieldTypesToSkip) {
        if (CollectionUtils.isNotEmpty(fieldTypesToSkip)) {
            if (this.fieldTypesToSkip == null) {
                this.fieldTypesToSkip = new ArrayList<>();
            }
            this.fieldTypesToSkip.addAll(fieldTypesToSkip);
        }
        return (T) this;
    }

    @Override
    public T add(List<String> fieldNames){
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            if (fieldsToAdd == null) {
                fieldsToAdd = new ArrayList<>();
            }
            fieldsToAdd.addAll(fieldNames);
        }
        return (T) this;
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
}
