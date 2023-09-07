package com.facilio.bmsconsole.ModuleSettingConfig.impl;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PageBuilderConfigUtil {

    public static TreeMap<String, Boolean> getModuleVsPageBuilderStatusMap(long orgId) throws Exception {
        TreeMap<String,Boolean> modulesVsStatus = new TreeMap<>();

        AccountUtil.setCurrentAccount(orgId);

        if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == orgId) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule> moduleList = modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true);
            moduleList.add(modBean.getModule("serviceRequest"));
            moduleList.add(modBean.getModule("workorder"));
            moduleList.add(modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER));
            moduleList.add(modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS));
            moduleList.add(modBean.getModule(FacilioConstants.UTILITY_DISPUTE));
            moduleList.add(modBean.getModule(FacilioConstants.Meter.METER));
            moduleList.add(modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE));
            moduleList.add(modBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME));
            moduleList.add(modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME));
            moduleList.add(modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME));
            moduleList.add(modBean.getModule("vendors"));

            if (CollectionUtils.isNotEmpty(moduleList)) {
                List<Long> moduleIds = moduleList.stream().filter(Objects::nonNull).map(FacilioModule::getModuleId).collect(Collectors.toList());
                Map<Long, ModuleSettingContext> settingContextsMap = ModuleSettingConfigUtil.getModuleListConfigDetailsForConfigName(moduleIds, FacilioConstants.SettingConfigurationContextNames.PAGE_BUILDER);

                for (FacilioModule module : moduleList) {
                    if (module != null) {
                        long moduleId = module.getModuleId();
                        boolean status = false;

                        if (settingContextsMap != null && settingContextsMap.containsKey(moduleId)) {
                            status = settingContextsMap.get(moduleId) != null && settingContextsMap.get(moduleId).isStatus();
                        }

                        modulesVsStatus.put(module.getName(), status);
                    }
                }
            }
        }
        return modulesVsStatus;
    }


    public static void updatePageBuilderModuleSettings(long orgId, String[] moduleNames) throws Exception {

        AccountUtil.setCurrentAccount(orgId);

        if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == orgId) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule> modules = new ArrayList<>();
            if (moduleNames != null) {
                modules = Arrays.stream(moduleNames).map(mod -> {
                    try {
                        return modBean.getModule(mod);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid module name");
                    }
                }).collect(Collectors.toList());
            }

            List<ModuleSettingContext> settingsContext = ModuleSettingConfigUtil.getModuleConfigDetailsForConfigName(FacilioConstants.SettingConfigurationContextNames.PAGE_BUILDER);
            Map<Long, Boolean> moduleVsSettingsStatus = new HashMap<>();
            if (CollectionUtils.isNotEmpty(settingsContext)) {
                moduleVsSettingsStatus = settingsContext.stream().collect(Collectors.toMap(ModuleSettingContext::getModuleId, ModuleSettingContext::isStatus));
            }
            List<ModuleSettingContext> updateSettings = new ArrayList<>();
            List<ModuleSettingContext> newSettings = new ArrayList<>();

            List<Long> enabledModuleIds = new ArrayList<>();
            for (FacilioModule module : modules) {
                ModuleSettingContext settingContext = new ModuleSettingContext();
                settingContext.setModuleId(module.getModuleId());
                settingContext.setStatus(true);
                settingContext.setConfigurationName(FacilioConstants.SettingConfigurationContextNames.PAGE_BUILDER);
                if (moduleVsSettingsStatus.containsKey(module.getModuleId())) {
                    updateSettings.add(settingContext);
                    enabledModuleIds.add(module.getModuleId());
                } else {
                    newSettings.add(settingContext);
                }
            }

            if (CollectionUtils.isNotEmpty(settingsContext)) {
                settingsContext.removeIf(s -> enabledModuleIds.contains(s.getModuleId()));
                updateSettings.addAll(settingsContext.stream().peek(s -> s.setStatus(false)).collect(Collectors.toList()));
            }


            if (CollectionUtils.isNotEmpty(updateSettings)) {
                ModuleSettingConfigUtil.updateSettingContext(updateSettings);
            }
            if (CollectionUtils.isNotEmpty(newSettings)) {
                ModuleSettingConfigUtil.insertSettingContext(newSettings);
            }
        }
    }


}
