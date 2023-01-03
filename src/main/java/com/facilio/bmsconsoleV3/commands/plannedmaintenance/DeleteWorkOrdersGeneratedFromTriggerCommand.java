package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.plannedmaintenance.ScheduleExecutor;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteWorkOrdersGeneratedFromTriggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,ArrayList<PMPlanner>> pmPlannerMap = (Map<String, ArrayList<PMPlanner>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<PMPlanner> pmPlannerList = pmPlannerMap.get(FacilioConstants.PM_V2.PM_V2_PLANNER);
        for(PMPlanner pmPlanner : pmPlannerList){
            PMPlanner pmPlannerObj = pmPlanner;
            PMTriggerV2 pmTriggerV2 = pmPlannerObj.getTrigger();
            if(pmTriggerV2 != null){
                return false;
            }
            else {
                Long plannerId = pmPlannerObj.getId();
                FacilioStatus status = TicketAPI.getStatus("preopen");
                PlannedMaintenanceAPI.deletePreOpenworkOrder(plannerId,status);
            }

        }
        return false;
    }
}
