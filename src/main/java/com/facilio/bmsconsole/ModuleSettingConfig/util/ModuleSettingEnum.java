package com.facilio.bmsconsole.ModuleSettingConfig.util;

import com.facilio.bmsconsole.ModuleSettingConfig.impl.GlimpseConfiguration;
import com.facilio.constants.FacilioConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ModuleSettingEnum {
    STATE_FLOW(FacilioConstants.SettingConfigurationContextNames.STATE_FLOW,"State Flow","Configure a workflow to streamline a process",true,null),
    Glimpse(FacilioConstants.ContextNames.GLIMPSE,"Glimpse","Configure fields to enable quick access to this module",true, GlimpseConfiguration.class),
    MARKUP_PREFERENCES(FacilioConstants.ContextNames.MARKUP_PREFERENCES,"Markup Preferences","Configure fields to enable quick access to this module",true, GlimpseConfiguration.class),
    NUMBER_FORMATTER(FacilioConstants.ContextNames.NUMBER_FORMATTER,"Number Formatter", "Controls numerical display by rounding or truncating to specified decimals.",true, null),

    SITE_MAP_VIEW(FacilioConstants.ContextNames.SITE_MAP_VIEW,"Site Map View","Configure to show Google map view in the site list page",true,null);


    ModuleSettingEnum(String configurationName, String displayName, String description, boolean isStatusDependent,Class configurationClass) {
        this.configurationName = configurationName;
        this.displayName = displayName;
        this.description = description;
        this.isStatusDependent = isStatusDependent;
        this.configurationClass = configurationClass;
    }

        public static final Map<String, ModuleSettingEnum> moduleConfigMap = Collections.unmodifiableMap(initConfigMap());

        private static Map<String, ModuleSettingEnum> initConfigMap() {
            Map<String, ModuleSettingEnum> moduleConfigurationMap = new HashMap<>();

            for (ModuleSettingEnum settingEnum : values()) {
                moduleConfigurationMap.put(settingEnum.getConfigurationName(), settingEnum);
            }
            return moduleConfigurationMap;
        }

    public Class getConfigurationClass() {
        return configurationClass;
    }

    public void setConfigurationClass(Class configurationClass) {
        this.configurationClass = configurationClass;
    }

    private Class configurationClass;

    public boolean isStatusDependent() {
        return isStatusDependent;
    }

    public void setStatusDependent(boolean statusDependent) {
        isStatusDependent = statusDependent;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    private boolean isStatusDependent;
    private String displayName;
    private String description;
    private String configurationName;

}

