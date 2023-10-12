package com.facilio.bmsconsole.ModuleSettingConfig.impl;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.modules.ModuleFactory;
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
            moduleList.add(modBean.getModule("insurance"));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING));

            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.CLIENT));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.SITE));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.BUILDING));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.FLOOR));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.SPACE));
            moduleList.add(modBean.getModule(AlarmTypeModule.MODULE_NAME));
            moduleList.add(modBean.getModule(AlarmDefinitionModule.MODULE_NAME));
            moduleList.add(modBean.getModule(AlarmCategoryModule.MODULE_NAME));
            moduleList.add(modBean.getModule(AlarmDefinitionTaggingModule.MODULE_NAME));
            moduleList.add(modBean.getModule(AlarmDefinitionMappingModule.MODULE_NAME));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.CONTROLLER));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.CLIENT));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT));
            moduleList.add(modBean.getModule(AlarmFilterRuleModule.MODULE_NAME));
            moduleList.add(modBean.getModule(FilteredAlarmModule.MODULE_NAME));
            moduleList.add(modBean.getModule(FlaggedEventRuleModule.MODULE_NAME));
            moduleList.add(modBean.getModule(FlaggedEventModule.MODULE_NAME));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.TENANT));
            moduleList.add(modBean.getModule("serviceOrder"));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE));
            moduleList.add(modBean.getModule(FacilioConstants.Territory.TERRITORY));
            moduleList.add(modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT));
            moduleList.add(modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET));
            moduleList.add(modBean.getModule(FacilioConstants.TimeOff.TIME_OFF));
            moduleList.add(modBean.getModule(FacilioConstants.Trip.TRIP));
            moduleList.add(modBean.getModule(FacilioConstants.Attendance.ATTENDANCE));
            moduleList.add(modBean.getModule(FacilioConstants.Break.BREAK));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST));
            moduleList.add(modBean.getModule(FacilioConstants.ContextNames.SAFETY_PLAN));

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
