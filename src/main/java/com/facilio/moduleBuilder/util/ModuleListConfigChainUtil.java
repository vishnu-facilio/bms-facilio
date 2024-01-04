package com.facilio.moduleBuilder.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.moduleBuilder.builder.Feature;
import com.facilio.moduleBuilder.builder.ModuleListHandler;
import com.facilio.moduleBuilder.command.GetModuleListFromBuilderCommand;
import com.facilio.moduleBuilder.command.ModuleListResponseCommand;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

public class ModuleListConfigChainUtil {
    protected static final Map<String, Supplier<ModuleListHandler>> MODULE_LIST_CONFIG_HANDLER_MAP = fillModuleListConfigMap();
    public static void initModuleListConfigHandlerMap() {
    }

    @SneakyThrows
    private static Map<String, Supplier<ModuleListHandler>> fillModuleListConfigMap() {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com\\.facilio\\.moduleBuilder"), new MethodAnnotationsScanner());
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(Feature.class);


        Map<String, Supplier<ModuleListHandler>> moduleListHandlerMap = new HashMap<>();
        for (Method method : methodsAnnotatedWithModule) {
            if (method.getParameterCount() != 0) {
                continue;
            }
            if (!method.getReturnType().equals(Supplier.class)) {
                continue;
            }

            Feature annotation = method.getAnnotation(Feature.class);
            String feature = annotation.value().trim();

            if (StringUtils.isEmpty(feature)) {
                throw new IllegalStateException("FeatureOrAPI annotation can't be empty");
            }

            Supplier<ModuleListHandler> config = (Supplier<ModuleListHandler>) method.invoke(null, null);

            if (moduleListHandlerMap.containsKey(feature)) {
                throw new IllegalStateException("ModuleList configuration already done for - "+feature);
            }
            moduleListHandlerMap.put(feature, config);
        }
        return Collections.unmodifiableMap(moduleListHandlerMap);
    }


    private static ModuleListHandler findModuleListHandler(@NonNull String featureOrAPI) throws Exception{
        Supplier<ModuleListHandler> moduleListHandlerSupplier = MODULE_LIST_CONFIG_HANDLER_MAP.get(featureOrAPI);
        return moduleListHandlerSupplier != null ? moduleListHandlerSupplier.get() : null;
    }


    public static FacilioChain getModuleListChain(@NonNull String feature, long appId) throws Exception {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(feature), "feature name to fetch moduleList can't be empty");

        ModuleListHandler moduleListHandler = findModuleListHandler(feature);
        FacilioUtil.throwIllegalArgumentException(moduleListHandler == null, "ModuleListHandler is not defined for - " + feature);

        FacilioChain chain = FacilioChain.getNonTransactionChain();
        FacilioContext context = chain.getContext();

        addIfNotNull(context, FacilioConstants.ModuleListConfig.FETCH_CUSTOM_MODULES, moduleListHandler.isFetchCustomModules());
        addIfNotNull(context, FacilioConstants.ModuleListConfig.RESPONSE_FIELDS, moduleListHandler.getFieldsInResponse());

        List<String> fetchModuleList = new ArrayList<>();
        List<String> modulesToFetch = moduleListHandler.getModulesToFetch();
        if(CollectionUtils.isNotEmpty(modulesToFetch)) {
            fetchModuleList.addAll(modulesToFetch);
        }
        fetchModuleList.addAll(getLicenseEnabledAndDisabledModulesToFetch(moduleListHandler.getFeatureLicensePairMap()));
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(fetchModuleList), "ModulesToFetch list can't be empty for fetching modules - " + feature);

        ApplicationContext app = appId > 0 ? ApplicationApi.getApplicationForId(appId) : AccountUtil.getCurrentApp();
        FacilioUtil.throwIllegalArgumentException(app == null, "App can't be null for fetching modulesList of - " + feature);

        //domainBasedModuleConfig
        fetchModuleList.addAll(getDomainBasedModulesToAdd(AppDomain.AppDomainType.valueOf(app.getDomainType()), moduleListHandler.getDomainBasedAddModules()));
        fetchModuleList.removeAll(getDomainBasedModulesToRemove(AppDomain.AppDomainType.valueOf(app.getDomainType()), moduleListHandler.getDomainBasedSkipModules()));

        //appBasedModuleConfig
        fetchModuleList.addAll(getAppBasedModulesToAdd(app.getLinkName(), moduleListHandler.getAppBasedAddModules()));
        fetchModuleList.removeAll(getAppBasedModulesToRemove(app.getLinkName(), moduleListHandler.getAppBasedSkipModules()));

        addIfNotNull(context, FacilioConstants.ModuleListConfig.MODULES_TO_FETCH, fetchModuleList);

        chain.addCommand(new GetModuleListFromBuilderCommand());
        Command afterFetchCommand = moduleListHandler.getAfterFetchCommand();
        chain.addCommand(new ModuleListResponseCommand());
        addIfNotNull(chain, afterFetchCommand);

        return chain;
    }

    private static void addIfNotNull(@NonNull FacilioContext context, String key, Object value) {
        if(value != null) {
            context.put(key, value);
        }
    }

    private static List<String> getDomainBasedModulesToAdd(AppDomain.AppDomainType currentAppDomain, Map<AppDomain.AppDomainType, List<String>> domainBasedAddModulesMap) {
        if(MapUtils.isNotEmpty(domainBasedAddModulesMap)) {
            List<String> domainBasedModulesToAdd = domainBasedAddModulesMap.get(currentAppDomain);
            if(CollectionUtils.isNotEmpty(domainBasedModulesToAdd)) {
                return domainBasedModulesToAdd;
            }
        }
        return Collections.emptyList();
    }

    private static List<String> getDomainBasedModulesToRemove(AppDomain.AppDomainType currentAppDomain, Map<AppDomain.AppDomainType, List<String>> domainBasedSkipModulesMap) {
        if(MapUtils.isNotEmpty(domainBasedSkipModulesMap)) {
            List<String> domainBasedModulesToSkip = domainBasedSkipModulesMap.get(currentAppDomain);
            if(CollectionUtils.isNotEmpty(domainBasedModulesToSkip)) {
                return domainBasedModulesToSkip;
            }
        }
        return Collections.emptyList();
    }

    private static List<String> getAppBasedModulesToAdd(String currentAppName, Map<String, List<String>> appBasedAddModulesMap) {
        if(MapUtils.isNotEmpty(appBasedAddModulesMap)) {
            List<String> appBasedModulesToAdd = appBasedAddModulesMap.get(currentAppName);
            if(CollectionUtils.isNotEmpty(appBasedModulesToAdd)) {
                return appBasedModulesToAdd;
            }
        }
        return Collections.emptyList();
    }

    private static List<String> getAppBasedModulesToRemove(String currentAppName, Map<String, List<String>> appBasedSkipModulesMap) {
        if(MapUtils.isNotEmpty(appBasedSkipModulesMap)) {
            List<String> appBasedModulesToSkip = appBasedSkipModulesMap.get(currentAppName);
            if(CollectionUtils.isNotEmpty(appBasedModulesToSkip)) {
                return appBasedModulesToSkip;
            }
        }
        return Collections.emptyList();
    }
    private static List<String> getLicenseEnabledAndDisabledModulesToFetch( Map<AccountUtil.FeatureLicense, Pair<List<String>, List<String>>> featureLicensePairMap) throws Exception {
        List<String> licenseBasedModulesToFetch = new ArrayList<>();
        if(MapUtils.isNotEmpty(featureLicensePairMap)) {
            for(Map.Entry<AccountUtil.FeatureLicense, Pair<List<String>, List<String>>> licenseEnabledAndDisabledModules : featureLicensePairMap.entrySet()) {
                if(AccountUtil.isFeatureEnabled(licenseEnabledAndDisabledModules.getKey())) {
                    licenseBasedModulesToFetch.addAll(licenseEnabledAndDisabledModules.getValue().getLeft());
                } else  {
                    licenseBasedModulesToFetch.addAll(licenseEnabledAndDisabledModules.getValue().getRight());
                }
            }
        }
        return licenseBasedModulesToFetch;
    }

    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }

    @SneakyThrows
    public static boolean hasLicenseEnabled(AccountUtil.FeatureLicense license) {
        return license == null || AccountUtil.isFeatureEnabled(license);
    }

}
