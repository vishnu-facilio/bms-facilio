package com.facilio.moduleBuilder.builder;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ModuleListHandler implements ModuleListBuilder {

    public List<String> getModulesToFetch() {
        return CollectionUtils.isNotEmpty(modulesToFetch) ? Collections.unmodifiableList(modulesToFetch) : null;
    }

    private List<String> modulesToFetch;

    public List<String> getFieldsInResponse() {
        return CollectionUtils.isNotEmpty(fieldsInResponse)? Collections.unmodifiableList(fieldsInResponse) :
                Arrays.asList("name","displayName","moduleId","type");
    }

    private List<String> fieldsInResponse;


    public Map<String, List<String>> getAppBasedSkipModules() {
        return MapUtils.isNotEmpty(appBasedSkipModules) ? Collections.unmodifiableMap(appBasedSkipModules) : null;
    }

    private Map<String, List<String>> appBasedSkipModules;

    public Map<AppDomain.AppDomainType, List<String>> getDomainBasedSkipModules() {
        return MapUtils.isNotEmpty(domainBasedSkipModules) ? Collections.unmodifiableMap(domainBasedSkipModules) : null;
    }

    private Map<AppDomain.AppDomainType, List<String>> domainBasedSkipModules;

    public Map<AccountUtil.FeatureLicense, Pair<List<String>, List<String>>> getFeatureLicensePairMap() {
        return MapUtils.isNotEmpty(featureLicensePairMap) ? Collections.unmodifiableMap(featureLicensePairMap) : null;
    }

    private Map<AccountUtil.FeatureLicense, Pair<List<String>, List<String>>> featureLicensePairMap;
    @Getter
    private boolean fetchCustomModules = false;
    @Getter
    private Command afterFetchCommand;


    @Override
    public ModuleListBuilder add(List<String> moduleNames) {
        if (modulesToFetch == null) {
            modulesToFetch = new ArrayList<>();
        }
        if (CollectionUtils.isNotEmpty(moduleNames)) {
            modulesToFetch.addAll(moduleNames);
        }
        return this;
    }
    @Override
    public ModuleListBuilder skipModulesForApp(String appName, @NonNull List<String> moduleNames) {
        if (appBasedSkipModules == null) {
            appBasedSkipModules = new HashMap<>();
        }
        if (!appBasedSkipModules.containsKey(appName)) {
            appBasedSkipModules.put(appName, new ArrayList<>());
        }
        appBasedSkipModules.get(appName).addAll(moduleNames);
        return this;
    }
    @Override
    public ModuleListBuilder skipModulesForDomain(AppDomain.AppDomainType appDomainType, List<String> moduleNames) {
        if (domainBasedSkipModules == null) {
            domainBasedSkipModules = new HashMap<>();
        }
        if (!domainBasedSkipModules.containsKey(appDomainType)) {
            domainBasedSkipModules.put(appDomainType, new ArrayList<>());
        }
        domainBasedSkipModules.get(appDomainType).addAll(moduleNames);
        return this;
    }

    @Override
    public ModuleListBuilder responseFields(List<String> fieldNames) {
        if(fieldsInResponse == null) {
            fieldsInResponse = new ArrayList<>();
        }
        if(CollectionUtils.isNotEmpty(fieldNames)) {
            fieldsInResponse.addAll(fieldNames);
        }
        return this;
    }

//    @Override
//    public ModuleListBuilder fieldsToFetch(List<String> fieldNames) {
//        if(modulesToFetch == null) {
//            modulesToFetch = new ArrayList<>();
//        }
//        if(CollectionUtils.isNotEmpty(moduleNames)) {
//            modulesToFetch.addAll(moduleNames);
//        }
//        return this;
//    }

    @Override
    public ModuleListBuilder addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense license, List<String> licenseEnabledModules, List<String> licenseDisabledModules) {
        if (featureLicensePairMap == null) {
            featureLicensePairMap = new HashMap<>();
        }
        if (license != null) {
            featureLicensePairMap.put(license, Pair.of(CollectionUtils.isNotEmpty(licenseEnabledModules)? Collections.unmodifiableList(licenseEnabledModules) : Collections.emptyList()
                    , CollectionUtils.isNotEmpty(licenseDisabledModules)? Collections.unmodifiableList(licenseDisabledModules) : Collections.emptyList()));
        }
        return this;
    }
    @Override
    public ModuleListBuilder afterFetch(Command afterFetchCommand) {
        if (afterFetchCommand != null) {
            this.afterFetchCommand = afterFetchCommand;
        }
        return this;
    }
    @Override
    public ModuleListBuilder fetchCustomModules() {
        this.fetchCustomModules = true;
        return this;
    }

    public ModuleListHandler done() {
        return this;
    }
}
