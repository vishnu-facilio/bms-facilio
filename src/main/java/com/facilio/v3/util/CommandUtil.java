package com.facilio.v3.util;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CommandUtil {

    public static List<? extends ModuleBaseWithCustomFields> getModuleDataList(Context context, String moduleName) {
        Map<String, List<? extends ModuleBaseWithCustomFields>> recordMap = (Map<String, List<? extends ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        return recordMap.get(moduleName);
    }

    public static ModuleBaseWithCustomFields getModuleData(Context context, String moduleName, long id) {
        List<? extends ModuleBaseWithCustomFields> moduleDataList = getModuleDataList(context, moduleName);
        if (CollectionUtils.isNotEmpty(moduleDataList)) {
            for (ModuleBaseWithCustomFields moduleData : moduleDataList) {
                if (moduleData.getId() == id) {
                    return moduleData;
                }
            }
        }
        return null;
    }
}
