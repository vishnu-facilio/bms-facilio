package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.CraftContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourPlanContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class WorkOrderPlannedLabourCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<Object>> queryParams = new HashMap<>();
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

        if(recordId != null) {
            if(moduleName.equals(FacilioConstants.CraftAndSKills.CRAFT)){
                CraftContext craft =  V3RecordAPI.getRecord(moduleName, recordId);
                    V3WorkOrderLabourPlanContext woLabourPlan = new V3WorkOrderLabourPlanContext();
                    woLabourPlan.setCraft(craft);
                    //woLabourPlan.setRate(craft.getStandardRate());

                context.put(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN, woLabourPlan);
            }
        }

        return false;
    }
}
