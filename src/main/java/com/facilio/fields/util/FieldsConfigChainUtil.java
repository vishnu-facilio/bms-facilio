package com.facilio.fields.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.context.FieldListType;
import com.facilio.fields.fieldBuilder.AppDomainFieldListHandler;
import com.facilio.fields.fieldBuilder.AppFieldListHandler;
import com.facilio.fields.fieldBuilder.FieldConfig;
import com.facilio.fields.fieldBuilder.FieldListHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.annotation.ModuleType;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Log4j
public class FieldsConfigChainUtil {
    private static final String FIELDS_CONF_PATH = FacilioUtil.normalizePath("conf/fieldsConfig.yaml");
    protected static final Map<String, Map<String, Object>> FIELDS_CONFIG_MAP = new HashMap<>();
    // key fieldListType name --> value their configs defined in fieldsComfig.yaml

    protected static void fillFieldsConfigMap() {
        Yaml yaml = new Yaml();
        Map<String, Object> json = null;
        try(InputStream inputStream = FieldsConfigChainUtil.class.getClassLoader().getResourceAsStream(FIELDS_CONF_PATH);) {
            json = yaml.load(inputStream);
        }
        catch (Exception e) {
            throwRunTimeException(MessageFormat.format("Error occurred while reading fields conf file, msg : {0}",e.getMessage()), e);
        }

        try {
            Object fieldListTypes = json.get(FacilioConstants.FieldsConfig.FIELD_LIST_TYPE);
            if (CollectionUtils.isNotEmpty((List<Map<String, Object>>)fieldListTypes)) {
                for (Map<String, Object> fieldConfigMap : (List<Map<String, Object>>)fieldListTypes) {

                    FieldListType fieldListType = FieldListType.valueOf((String) fieldConfigMap.get(FacilioConstants.FieldsConfig.NAME));

                    Map<String, Object> configMap = new HashMap<>();

                    List<String> skipConfigFields = (List<String>) fieldConfigMap.get(FacilioConstants.FieldsConfig.SKIP_CONFIG_FIELDS);
                    if(CollectionUtils.isNotEmpty(skipConfigFields)) {
                        configMap.put(FacilioConstants.FieldsConfig.SKIP_CONFIG_FIELDS, Collections.unmodifiableList(skipConfigFields));
                    }

                    String accessTypeName = (String) fieldConfigMap.getOrDefault(FacilioConstants.FieldsConfig.ACCESS_TYPE, null);
                    if(StringUtils.isNotBlank(accessTypeName)) {
                        FacilioField.AccessType accessType = FacilioField.AccessType.valueOf(accessTypeName);
                        configMap.put(FacilioConstants.FieldsConfig.ACCESS_TYPE, accessType);
                    }

                    List<String> fieldTypeNamesToFetch = (List<String>) fieldConfigMap.get(FacilioConstants.FieldsConfig.FIELD_TYPE_NAMES_TO_FETCH);  //fieldTypesToFetch
                    if(CollectionUtils.isNotEmpty(fieldTypeNamesToFetch)) {
                        List<FieldType> fieldsTypesToFetch = fieldTypeNamesToFetch
                                .stream()
                                .map(FieldType::valueOf)
                                .collect(Collectors.toList());
                        configMap.put(FacilioConstants.FieldsConfig.FIELD_TYPES_TO_FETCH, Collections.unmodifiableList(fieldsTypesToFetch));
                    }

                    boolean fetchSupplements = (boolean) fieldConfigMap.getOrDefault(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, false);
                    configMap.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchSupplements);

                    List<String> fieldsResponseCommand = (List<String>) fieldConfigMap.get(FacilioConstants.FieldsConfig.FIELD_RESPONSE_CHAIN); //add fieldResponseChain
                    if(CollectionUtils.isNotEmpty(fieldsResponseCommand)) {
                        configMap.put(FacilioConstants.FieldsConfig.FIELD_RESPONSE_CHAIN, Collections.unmodifiableList(fieldsResponseCommand));
                    }

                    if(fieldListType.getFieldListTypeConfig() != null) {
                        Map<String, Object> fieldListTypeConfigMap = FIELDS_CONFIG_MAP.get(fieldListType.getFieldListTypeConfig().getName());
                        for(Map.Entry<String, Object> entry : fieldListTypeConfigMap.entrySet()) {
                            if(!configMap.containsKey(entry.getKey())) {
                                configMap.put(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    FIELDS_CONFIG_MAP.put(fieldListType.getName(), Collections.unmodifiableMap(configMap));
                }

                for (FieldListType fieldListType : FieldListType.values()) {
                    if(!FIELDS_CONFIG_MAP.containsKey(fieldListType.getName()) && fieldListType.getFieldListTypeConfig() != null) {
                        FIELDS_CONFIG_MAP.put(fieldListType.getName(), FIELDS_CONFIG_MAP.get(fieldListType.getFieldListTypeConfig().getName()));
                    }
                }
            }
        }
        catch (Exception e) {
            throwRunTimeException(MessageFormat.format("Error occurred while parsing fields conf file, msg : {0}",e.getMessage()), e);
        }
    }

    private static void throwRunTimeException (String logMsg, Throwable e) {
        LOGGER.error(logMsg, e);
        throw new RuntimeException(logMsg, e);
    }

    protected static FacilioChain getChainFromString(List<String> commandNames) throws Exception {
        if(CollectionUtils.isNotEmpty(commandNames)) {
            FacilioChain chain = FieldsConfigChain.getDefaultChain();
            for (String commandName : commandNames) {
                chain.addCommand((Command) Class.forName(commandName).newInstance());
            }
            return chain;
        }
        return null;
    }

    public static FacilioContext fetchFieldList(String moduleName, long appId,  FieldListType fieldListType, List<Long> defaultFieldIds) throws Exception {
        return fetchFieldList(moduleName, appId, fieldListType, defaultFieldIds, false);
    }
    public static FacilioContext fetchFieldList(String moduleName, long appId,  FieldListType fieldListType, List<Long> defaultFieldIds, boolean isOnelevel) throws Exception {
        FacilioChain fieldsChain = null;
        FacilioContext fieldsContext = null;
        ApplicationContext app = appId > 0 ? ApplicationApi.getApplicationForId(appId) : AccountUtil.getCurrentApp();
        FacilioUtil.throwIllegalArgumentException(app == null, "Invalid appId");
        if (fieldListType == FieldListType.VIEW_FIELDS) {
            fieldsChain = FieldsConfigChain.getViewFieldsConfigChain(moduleName, app);
        } else {
            fieldsChain = FieldsConfigChain.getFieldsConfigChain(moduleName, app, fieldListType);
        }
        fieldsContext = fieldsChain.getContext();
        addDefaultValuesToContext(fieldsContext, moduleName, appId, fieldListType, defaultFieldIds, isOnelevel);
        if (isOnelevel) {
            fieldsContext.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, false);
        }
        fieldsChain.execute();
        return fieldsContext;
    }


    private static void addDefaultValuesToContext(FacilioContext context, String moduleName, long appId, FieldListType fieldListType, List<Long> defaultFieldIds, boolean isOnelevel) throws Exception {
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.DEFAULT_FIELD_IDS, defaultFieldIds);
        context.put(FacilioConstants.FieldsConfig.FIELD_LIST_TYPE, fieldListType);if (isOnelevel) {
            context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, false);
        }
    }

    protected static void fillModuleNameMap(Reflections reflections) throws InvocationTargetException, IllegalAccessException {
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(Module.class);

        for (Method method : methodsAnnotatedWithModule) {
            if (method.getParameterCount() != 0) {
                continue;
            }
            if (!method.getReturnType().equals(Supplier.class)) {
                continue;
            }

            Module annotation = method.getAnnotation(Module.class);
            String moduleName = annotation.value().trim();

            if (StringUtils.isEmpty(moduleName)) {
                throw new IllegalStateException("Module name cannot be empty for fieldsConfig.");
            }

            Supplier<FieldConfig> config = (Supplier<FieldConfig>) method.invoke(null, null);
            if (FieldsConfigChain.FIELDS_CONFIG_MODULE_HANDLER_MAP.containsKey(moduleName)) {
                throw new IllegalStateException("Fields config already present for moduleName.");
            }
            FieldsConfigChain.FIELDS_CONFIG_MODULE_HANDLER_MAP.put(moduleName, config);
        }
    }

    protected static void fillModuleTypeMap(Reflections reflections) throws InvocationTargetException, IllegalAccessException {
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(ModuleType.class);

        for (Method method : methodsAnnotatedWithModule) {
            if (method.getParameterCount() != 0) {
                continue;
            }
            if (!method.getReturnType().equals(Supplier.class)) {
                continue;
            }

            ModuleType annotation = method.getAnnotation(ModuleType.class);
            FacilioModule.ModuleType moduleType = annotation.type();

            if (moduleType == null) {
                throw new IllegalStateException("Module type cannot be empty for FieldsConfig.");
            }

            Supplier<FieldConfig> config = (Supplier<FieldConfig>) method.invoke(null, null);
            if (FieldsConfigChain.FIELDS_CONFIG_MODULETYPE_HANDLER_MAP.containsKey(moduleType)) {
                throw new IllegalStateException("Fields config already present for moduleType.");
            }
            FieldsConfigChain.FIELDS_CONFIG_MODULETYPE_HANDLER_MAP.put(moduleType, config);
        }
    }

    public static void addDomainBasedConfigs(FieldListHandler fieldHandler,ApplicationContext app, List<String> fieldsToSkipList, List<String> onelevelFieldsToSkipList) {
        if(app != null) {
            Map<AppDomain.AppDomainType, AppDomainFieldListHandler> appDomainFieldListHandlerMap = fieldHandler.getAppDomainFieldsConfigMap();
            if (MapUtils.isNotEmpty(appDomainFieldListHandlerMap)) {
                AppDomainFieldListHandler appDomainFieldListHandler = appDomainFieldListHandlerMap.get(AppDomain.AppDomainType.valueOf(app.getDomainType()));
                if(appDomainFieldListHandler != null) {
                    List<String> domainBasedSkipFields = (List<String>) appDomainFieldListHandler.getFieldsToSkip();
                    if (CollectionUtils.isNotEmpty(domainBasedSkipFields)) {
                        fieldsToSkipList.addAll(domainBasedSkipFields);
                    }
                    List<String> domainBasedOnelevelFieldsToSkip = appDomainFieldListHandler.getOnelevelFieldsToSkip();
                    if (CollectionUtils.isNotEmpty(domainBasedOnelevelFieldsToSkip)) {
                        onelevelFieldsToSkipList.addAll(domainBasedOnelevelFieldsToSkip);
                    }
                }
            }
        }
    }

    public static void addAppBasedConfigs(FieldListHandler fieldHandler,ApplicationContext app, List<String> fieldsToSkipList, List<String> onelevelFieldsToSkipList) {
        if(app != null) {
            Map<String, AppFieldListHandler> appFieldListHandlerMap = fieldHandler.getAppFieldConfigMap();
            if(MapUtils.isNotEmpty(appFieldListHandlerMap)) {
                AppFieldListHandler appFieldListHandler = appFieldListHandlerMap.get(app.getLinkName());

                if(appFieldListHandler != null) {
                    List<String> appBasedSkipFields = (List<String>) appFieldListHandler.getFieldsToSkip();
                    if (CollectionUtils.isNotEmpty(appBasedSkipFields)) {
                        fieldsToSkipList.addAll(appBasedSkipFields);
                    }
                    List<String> appBasedOnelevelFieldsToSkip = appFieldListHandler.getOnelevelFieldsToSkip();
                    if (CollectionUtils.isNotEmpty(appBasedOnelevelFieldsToSkip)) {
                        onelevelFieldsToSkipList.addAll(appBasedOnelevelFieldsToSkip);
                    }
                }
            }
        }
    }
    public static void putAddAndSkipFieldsInContext(FacilioChain chain, List<String> excludeFields, Map<AccountUtil.FeatureLicense, List<String>> licenseBasedFieldsMap, List<String> fieldsToAddList, List<String> fieldsToSkipList,
                                             List<String> onelevelFieldsToSkip, List<FieldType> fieldTypesToSkip, Map<String, Object> configMap) {
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

        List<String> skipFieldsList = new ArrayList<>();

        List<String> skipConfigFields = (List<String>) configMap.get(FacilioConstants.FieldsConfig.SKIP_CONFIG_FIELDS);
        if (CollectionUtils.isNotEmpty(skipConfigFields)) {
            skipFieldsList.addAll(skipConfigFields);
        }

        if (CollectionUtils.isNotEmpty(excludeFields)) {
            skipFieldsList.addAll(excludeFields);
        }

        if (CollectionUtils.isNotEmpty(fieldsToSkipList)) {
            skipFieldsList.addAll(fieldsToSkipList);
        }

        if (CollectionUtils.isNotEmpty(fieldsToAddList)) {
            if (CollectionUtils.isNotEmpty(skipFieldsList)) {
                fieldsToAddList = new ArrayList<>(fieldsToAddList);
                fieldsToAddList.removeAll(skipFieldsList);
            }
            context.put(FacilioConstants.FieldsConfig.IS_ADD_FIELD, true);
            context.put(FacilioConstants.FieldsConfig.FIELDS_TO_ADD_LIST, Collections.unmodifiableList(fieldsToAddList));
        } else if (CollectionUtils.isNotEmpty(skipFieldsList)) {
            context.put(FacilioConstants.FieldsConfig.IS_ADD_FIELD, false);
            context.put(FacilioConstants.FieldsConfig.FIELDS_TO_SKIP_LIST, Collections.unmodifiableList(skipFieldsList));
        }

        if(CollectionUtils.isNotEmpty(onelevelFieldsToSkip)) {
            context.put(FacilioConstants.FieldsConfig.ONE_LEVEL_FIELDS_TO_SKIP_LIST, Collections.unmodifiableList(onelevelFieldsToSkip));
        }

        if (MapUtils.isNotEmpty(licenseBasedFieldsMap)) {
            context.put(FacilioConstants.FieldsConfig.LICENSE_BASED_FIELDS_MAP, Collections.unmodifiableMap(licenseBasedFieldsMap));
        }

        boolean fetchSupplements = (boolean) configMap.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchSupplements);
    }
}
