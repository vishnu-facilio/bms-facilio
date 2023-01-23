package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.Arrays;
import java.util.List;

public class ReadingAlarm extends BaseModuleConfig {
    public ReadingAlarm() {
        setModuleName(FacilioConstants.ContextNames.READING_ALARM);
    }
}
