package com.facilio.readingkpi.commands.update;

import com.facilio.command.FacilioCommand;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class NullFieldIdAndModuleIdForDynamicKpi extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        ReadingKPIContext kpi = (ReadingKPIContext) recordMap.get(moduleName).get(0);
        if (kpi.getKpiTypeEnum() == KPIType.DYNAMIC) {
            kpi.setReadingFieldId(null);
            kpi.setReadingModuleId(null);
        }
        return false;
    }
}
