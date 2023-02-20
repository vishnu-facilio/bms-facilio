package com.facilio.bmsconsole.ModuleSettingConfig.annotation;

import com.facilio.bmsconsole.ModuleSettingConfig.impl.GlimpseConfiguration;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.annotation.Config;

@Config
public class ModuleSettingConfigAnnotation {

    @SettingConfig(FacilioConstants.ContextNames.GLIMPSE)
    public static Class getGlimpseConfig() throws Exception {

        return GlimpseConfiguration.class;
    }

}
