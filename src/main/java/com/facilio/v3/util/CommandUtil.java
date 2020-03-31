package com.facilio.v3.util;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class CommandUtil {

    public static List<? extends ModuleBaseWithCustomFields> getModuleDataList(Context context, String moduleName) {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        return recordMap.get(moduleName);
    }
}
