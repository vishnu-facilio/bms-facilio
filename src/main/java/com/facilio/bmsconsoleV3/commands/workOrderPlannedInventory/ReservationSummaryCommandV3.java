package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReservationSummaryCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = FacilioConstants.ContextNames.WO_PLANNED_ITEMS;
        Map<String, List<Object>> queryParams = new HashMap<>();
        queryParams.put("reserveValidation", Collections.singletonList(true));
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

        FacilioContext workOrderPlannedItemsContext = V3Util.getSummary(moduleName,recordIds,queryParams,false,null);
        context.put(FacilioConstants.ContextNames.WO_PLANNED_ITEMS, ((Map<String, List>) workOrderPlannedItemsContext.get("recordMap")).get("workOrderPlannedItems"));
        return false;
    }
}
