package com.facilio.fields.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.commands.*;
import com.facilio.fields.context.FieldListType;
import com.facilio.fields.fieldBuilder.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.Reflections;

import java.util.*;
import java.util.function.Supplier;

import static com.facilio.fields.util.FieldsConfigChainUtil.FIELDS_CONFIG_MAP;

@Log4j
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
    public static FacilioChain getViewFieldsConfigChain(String moduleName, ApplicationContext app) throws Exception {
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
        List<String> fieldsToSkipList = new ArrayList<>();
        List<String> onelevelFieldsToSkipList = new ArrayList<>();
        List<FieldType> fieldTypesToSkip = null;
        List<String> fixedFields = null;
        List<String> fixedSelectableFields = null;
        Map<String, JSONObject> customization = null;
        Command afterFetchCommand = null;

        if (fieldHandler != null) {
            // TODO remove once skip is deprecated completely
            List<String> fieldsToSkip = fieldHandler.getFieldsToSkip();
            if(CollectionUtils.isNotEmpty(fieldsToSkip)) {
                LOGGER.info("Remove skip fields for moduleName -- "+ moduleName);
                fieldsToSkipList.addAll(fieldsToSkip);
            }
            fieldsToAddList = fieldHandler.getFieldsToAdd();
            List<String> onelevelFieldsToSkip = fieldHandler.getOnelevelFieldsToSkip();
            if(CollectionUtils.isNotEmpty(onelevelFieldsToSkip)) {
                onelevelFieldsToSkipList.addAll(onelevelFieldsToSkip);
            }
            fieldTypesToSkip = fieldHandler.getFieldTypesToSkip();
            fixedFields = fieldHandler.getFixedFields();
            fixedSelectableFields = fieldHandler.getFixedSelectableFields();
            customization = fieldHandler.getCustomization();
            afterFetchCommand = fieldHandler.getAfterFetchCommand();
            FieldsConfigChainUtil.addDomainBasedConfigs(fieldHandler, app, fieldsToSkipList, onelevelFieldsToSkipList);
            FieldsConfigChainUtil.addAppBasedConfigs(fieldHandler, app, fieldsToSkipList, onelevelFieldsToSkipList);
        }

        FacilioChain chain = getDefaultChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.FieldsConfig.FIXED_FIELD_NAMES, fixedFields);
        context.put(FacilioConstants.FieldsConfig.FIXED_SELECTABLE_FIELD_NAMES, fixedSelectableFields);
        context.put(FacilioConstants.FieldsConfig.CUSTOMIZATION, customization);

        // add fields to "skip  or add" to context
        FieldsConfigChainUtil.putAddAndSkipFieldsInContext(chain,excludeFields, licenseBasedFieldsMap, fieldsToAddList, fieldsToSkipList, onelevelFieldsToSkipList, fieldTypesToSkip, configMap);
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

    public static <T extends FieldListHandler<T>> FacilioChain getFieldsConfigChain(String moduleName, FieldListType fieldListType) throws Exception {
        return getFieldsConfigChain(moduleName, null, fieldListType);
    }

    public static <T extends FieldListHandler<T>> FacilioChain getFieldsConfigChain(String moduleName, ApplicationContext app, FieldListType fieldListType) throws Exception {
        FacilioModule module = getModule(moduleName);
        FieldConfig fieldConfig = getFieldHandlerForModule(module);

        T fieldHandler = getFieldHandlerForType(fieldConfig, fieldListType);
        Map<String, Object> configMap = FIELDS_CONFIG_MAP.get(fieldListType.getName());

        List<String> excludeFields = null;
        Map<AccountUtil.FeatureLicense, List<String>> licenseBasedFieldsMap = null;
        if(fieldConfig != null) {
            excludeFields = fieldConfig.getExcludeFields();
            licenseBasedFieldsMap = fieldConfig.getLicenseBasedFieldsMap();
        }

        List<String> fieldsToAddList = null;
        List<String> fieldsToSkipList = new ArrayList<>();
        List<String> onelevelFieldsToSkipList = new ArrayList<>();
        List<FieldType> fieldTypesToSkip = null;
        Command afterFetchCommand = null;

        if (fieldHandler != null) {
            // TODO remove once skip is deprecated completely
            List<String> fieldsToSkip = fieldHandler.getFieldsToSkip();
            if(CollectionUtils.isNotEmpty(fieldsToSkip)) {
                LOGGER.info("Remove skip fields for moduleName -- "+ moduleName);
                fieldsToSkipList.addAll(fieldsToSkip);
            }
            fieldsToAddList = fieldHandler.getFieldsToAdd();
            List<String> onelevelFieldsToSkip = fieldHandler.getOnelevelFieldsToSkip();
            if(CollectionUtils.isNotEmpty(onelevelFieldsToSkip)) {
                onelevelFieldsToSkipList.addAll(onelevelFieldsToSkip);
            }
            fieldTypesToSkip = fieldHandler.getFieldTypesToSkip();
            afterFetchCommand = fieldHandler.getAfterFetchCommand();
            FieldsConfigChainUtil.addDomainBasedConfigs(fieldHandler, app, fieldsToSkipList, onelevelFieldsToSkipList);
            FieldsConfigChainUtil.addAppBasedConfigs(fieldHandler, app, fieldsToSkipList, onelevelFieldsToSkipList);
        }

        FacilioChain chain = getDefaultChain();
        // add fields to "skip  or add" to context
        FieldsConfigChainUtil.putAddAndSkipFieldsInContext(chain,excludeFields, licenseBasedFieldsMap, fieldsToAddList, fieldsToSkipList, onelevelFieldsToSkipList, fieldTypesToSkip, configMap);
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

    private static <T extends FieldListHandler<T>> T getFieldHandlerForType(FieldConfig fieldConfig, FieldListType fieldListType) {
        if(fieldConfig != null && fieldListType != null) {
            return (T) fieldConfig.getFieldListTypeHandlerMap().get(fieldListType.getName());
        }
        return null;
    }

    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }
}
