package com.facilio.fields.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.commands.*;
import com.facilio.fields.context.FieldListType;
import com.facilio.fields.fieldBuilder.FieldConfig;
import com.facilio.fields.fieldBuilder.FieldListHandler;
import com.facilio.fields.fieldBuilder.ViewFieldListHandler;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.Reflections;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.facilio.fields.util.FieldsConfigChainUtil.FIELDS_CONFIG_MAP;

public class FieldsConfigChain {
    protected static final Map<String, Supplier<FieldConfig>> FIELDS_CONFIG_MODULE_HANDLER_MAP = new HashMap<>();
    protected static final Map<FacilioModule.ModuleType, Supplier<FieldConfig>> FIELDS_CONFIG_MODULETYPE_HANDLER_MAP = new HashMap<>();

    public static void initFieldConfigHandlerMap() throws Exception {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com\\.facilio\\.fields"), new MethodAnnotationsScanner());
        FieldsConfigChainUtil.fillModuleNameMap(reflections);
        FieldsConfigChainUtil.fillModuleTypeMap(reflections);
        FieldsConfigChainUtil.fillFieldsConfigMap();
    }

    public static FacilioModule getModule(String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module name");
        }
        return module;
    }

    protected static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain getViewFieldsConfigChain(String moduleName) throws Exception {
        FacilioModule module = getModule(moduleName);
        FieldConfig fieldConfig = getFieldHandlerForModule(module);
        Map<String, Object> configMap = FIELDS_CONFIG_MAP.get(FieldListType.VIEW_FIELDS.getName());

        ViewFieldListHandler fieldHandler = null;
        List<String> excludeFields = null;
        Map<AccountUtil.FeatureLicense, List<String>> licenseBasedFieldsMap = null;

        if(fieldConfig != null) {
            fieldHandler = fieldConfig.getViewFieldListHandler();
            excludeFields = fieldConfig.getExcludeFields();
            licenseBasedFieldsMap = fieldConfig.getLicenseBasedFieldsMap();
        }

        List<String> fieldsToAddList = null;
        List<String> fieldsToSkipList = null;
        List<FieldType> fieldTypesToSkip = null;
        List<String> fixedFields = null;
        List<String> fixedSelectableFields = null;
        Map<String, JSONObject> customization = null;
        Command afterFetchCommand = null;

        if (fieldHandler != null) {
            fieldsToSkipList = fieldHandler.getFieldsToSkip();
            fieldsToAddList = fieldHandler.getFieldsToAdd();
            fieldTypesToSkip = fieldHandler.getFieldTypesToSkip();
            fixedFields = fieldHandler.getFixedFields();
            fixedSelectableFields = fieldHandler.getFixedSelectableFields();
            customization = fieldHandler.getCustomization();
            afterFetchCommand = fieldHandler.getAfterFetchCommand();
        }

        FacilioChain chain = getDefaultChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.FieldsConfig.FIXED_FIELD_NAMES, fixedFields);
        context.put(FacilioConstants.FieldsConfig.FIXED_SELECTABLE_FIELD_NAMES, fixedSelectableFields);
        context.put(FacilioConstants.FieldsConfig.CUSTOMIZATION, customization);

        // add fields to "skip  or add" to context
        putAddAndSkipFieldsInContext(chain,excludeFields, licenseBasedFieldsMap, fieldsToAddList, fieldsToSkipList, fieldTypesToSkip, configMap);
        chain.addCommand(new GetFieldsListCommand());
        chain.addCommand(new FilterFieldsByFieldType());
        chain.addCommand(new FilterFieldsByAccessType());
        chain.addCommand(new FilterFieldsBasedOnAddOrSkipFields());
        chain.addCommand(new RemoveLicenseDisabledFields());
        chain.addCommand(new FilterFieldsByPermissionCommand());
        addIfNotNull(chain, afterFetchCommand);
        chain.addCommand(new GetOneLevelModuleFieldsCommand());
        addIfNotNull(chain, FieldsConfigChainUtil.getChainFromString((List<String>) configMap.get(FacilioConstants.FieldsConfig.FIELD_RESPONSE_CHAIN)));

        return chain;
    }

    public static FacilioChain getFieldsConfigChain(String moduleName, FieldListType fieldListType) throws Exception {
        FacilioModule module = getModule(moduleName);
        FieldConfig fieldConfig = getFieldHandlerForModule(module);
        FieldListHandler fieldHandler = getFieldHandlerForType(fieldConfig, fieldListType);
        Map<String, Object> configMap = FIELDS_CONFIG_MAP.get(fieldListType.getName());

        List<String> excludeFields = null;
        Map<AccountUtil.FeatureLicense, List<String>> licenseBasedFieldsMap = null;
        if(fieldConfig != null) {
            excludeFields = fieldConfig.getExcludeFields();
            licenseBasedFieldsMap = fieldConfig.getLicenseBasedFieldsMap();
        }

        List<String> fieldsToAddList = null;
        List<String> fieldsToSkipList = null;
        List<FieldType> fieldTypesToSkip = null;
        Command afterFetchCommand = null;

        if (fieldHandler != null) {
            fieldsToSkipList = fieldHandler.getFieldsToSkip();
            fieldsToAddList = fieldHandler.getFieldsToAdd();
            fieldTypesToSkip = fieldHandler.getFieldTypesToSkip();
            afterFetchCommand = fieldHandler.getAfterFetchCommand();
        }

        FacilioChain chain = getDefaultChain();
        // add fields to "skip  or add" to context
        putAddAndSkipFieldsInContext(chain,excludeFields, licenseBasedFieldsMap, fieldsToAddList, fieldsToSkipList, fieldTypesToSkip, configMap);
        chain.addCommand(new GetFieldsListCommand());
        chain.addCommand(new FilterFieldsByFieldType());
        chain.addCommand(new FilterFieldsByAccessType());
        chain.addCommand(new FilterFieldsBasedOnAddOrSkipFields());
        chain.addCommand(new RemoveLicenseDisabledFields());
        chain.addCommand(new FilterFieldsByPermissionCommand());
        addIfNotNull(chain, afterFetchCommand);
        chain.addCommand(new GetOneLevelModuleFieldsCommand());
        addIfNotNull(chain, FieldsConfigChainUtil.getChainFromString((List<String>) configMap.get(FacilioConstants.FieldsConfig.FIELD_RESPONSE_CHAIN)));

        return chain;
    }

    private static FieldConfig getFieldHandlerForModule(FacilioModule module) throws Exception {
        return  findFieldsConfig(module);
    }

    private static FieldConfig findFieldsConfig(FacilioModule facilioModule) throws Exception{
        Supplier<FieldConfig> fieldsConfigSupplier = FIELDS_CONFIG_MODULE_HANDLER_MAP.get(facilioModule.getName());
        if(fieldsConfigSupplier == null) {
            if (facilioModule.isCustom()) {
                fieldsConfigSupplier = FIELDS_CONFIG_MODULE_HANDLER_MAP.get(FacilioConstants.FieldsConfig.CUSTOM);
            } else {
                fieldsConfigSupplier = FIELDS_CONFIG_MODULETYPE_HANDLER_MAP.get(facilioModule.getTypeEnum());
            }
        }
        return fieldsConfigSupplier != null ? fieldsConfigSupplier.get() : null;
    }

    private static FieldListHandler getFieldHandlerForType(FieldConfig fieldConfig, FieldListType fieldListType) {
        if(fieldConfig != null && fieldListType != null) {
            return fieldConfig.getFieldListTypeHandlerMap().get(fieldListType.getName());
        }
        return null;
    }

    private static void putAddAndSkipFieldsInContext(FacilioChain chain, List<String> excludeFields, Map<AccountUtil.FeatureLicense, List<String>> licenseBasedFieldsMap, List<String> fieldsToAddList, List<String> fieldsToSkipList,
                                                     List<FieldType> fieldTypesToSkip, Map<String, Object> configMap) {
        FacilioContext context = chain.getContext();
        List<FieldType> fieldTypesToFetch = (List<FieldType>) configMap.get(FacilioConstants.FieldsConfig.FIELD_TYPES_TO_FETCH);
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(fieldTypesToFetch), "fieldTypes to fetch can't be empty");
        List<FieldType> fieldTypesToFetchCopy = CollectionUtils.isNotEmpty(fieldTypesToFetch) ? new ArrayList<>(fieldTypesToFetch) : new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fieldTypesToSkip)) {
            fieldTypesToFetchCopy.removeAll(fieldTypesToSkip);
        }
        context.put(FacilioConstants.FieldsConfig.FIELD_TYPES_TO_FETCH, fieldTypesToFetchCopy);

        FacilioField.AccessType accessType = (FacilioField.AccessType) configMap.get(FacilioConstants.FieldsConfig.ACCESS_TYPE);
        if (accessType != null) {
            context.put(FacilioConstants.FieldsConfig.ACCESS_TYPE, accessType);
        }

        List<String> skipConfigFields = (List<String>) configMap.get(FacilioConstants.FieldsConfig.SKIP_CONFIG_FIELDS);
        if (CollectionUtils.isNotEmpty(skipConfigFields)) {
            if (CollectionUtils.isNotEmpty(excludeFields)) {
                excludeFields = new ArrayList<>(excludeFields);
                excludeFields.addAll(skipConfigFields);
            } else {
                excludeFields = skipConfigFields;
            }
        }
        if (CollectionUtils.isNotEmpty(fieldsToAddList)) {
            if (CollectionUtils.isNotEmpty(excludeFields)) {
                fieldsToAddList = new ArrayList<>(fieldsToAddList);
                fieldsToAddList.removeAll(excludeFields);
            }
            context.put(FacilioConstants.FieldsConfig.IS_ADD_FIELD, true);
            context.put(FacilioConstants.FieldsConfig.FIELDS_TO_ADD_LIST, fieldsToAddList);
        } else {
            if (CollectionUtils.isNotEmpty(excludeFields)) {
                fieldsToSkipList = Stream.concat(excludeFields.stream(), (CollectionUtils.isNotEmpty(fieldsToSkipList)? fieldsToSkipList : new ArrayList<String>()).stream())
                        .distinct()
                        .collect(Collectors.toList());
            }
            context.put(FacilioConstants.FieldsConfig.IS_ADD_FIELD, false);
            context.put(FacilioConstants.FieldsConfig.FIELDS_TO_SKIP_LIST, fieldsToSkipList);
        }

        if (MapUtils.isNotEmpty(licenseBasedFieldsMap)) {
            context.put(FacilioConstants.FieldsConfig.LICENSE_BASED_FIELDS_MAP, licenseBasedFieldsMap);
        }

        boolean fetchSupplements = (boolean) configMap.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchSupplements);
    }
    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }
}
