package com.facilio.bmsconsoleV3.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ToolsReservationSummaryCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = FacilioConstants.ContextNames.WO_PLANNED_TOOLS;
        Map<String, List<Object>> queryParams = new HashMap<>();
        queryParams.put("reserveValidation", Collections.singletonList(true));
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

        FacilioContext workOrderPlannedToolsContext = V3Util.getSummary(moduleName,recordIds,queryParams,false,null);
        context.put(FacilioConstants.ContextNames.WO_PLANNED_TOOLS, ((Map<String, List>) workOrderPlannedToolsContext.get("recordMap")).get(FacilioConstants.ContextNames.WO_PLANNED_TOOLS));
        return false;
    }
}
