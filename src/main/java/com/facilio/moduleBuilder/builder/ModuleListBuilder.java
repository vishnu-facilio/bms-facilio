package com.facilio.moduleBuilder.builder;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import org.apache.commons.chain.Command;

import java.util.List;

public interface ModuleListBuilder {
    ModuleListBuilder add(List<String> moduleNames);

    ModuleListBuilder addModulesForApp(String appName, List<String> moduleNames);

    ModuleListBuilder addModulesForDomain(AppDomain.AppDomainType appDomainType, List<String> moduleNames);

    ModuleListBuilder responseFields(List<String> fieldNames);

    ModuleListBuilder afterFetch(Command responseCommand);

    ModuleListBuilder fetchCustomModules();

    ModuleListBuilder addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense license, List<String> licenseEnabledModules, List<String> licenseDisabledModules);

    ModuleListHandler done();

}
