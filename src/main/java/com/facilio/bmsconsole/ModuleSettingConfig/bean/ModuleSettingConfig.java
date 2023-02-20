package com.facilio.bmsconsole.ModuleSettingConfig.bean;

import com.facilio.modules.FacilioModule;


public abstract class ModuleSettingConfig {

    public abstract Object getModuleConfigDetails(FacilioModule module) throws Exception;
}
