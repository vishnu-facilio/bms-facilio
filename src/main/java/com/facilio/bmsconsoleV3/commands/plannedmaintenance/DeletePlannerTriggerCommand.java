package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeletePlannerTriggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<Long, PMPlanner> recordMap = (Map<Long, PMPlanner>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<PMPlanner> pmPlannerList = (List<PMPlanner>) recordMap.get(FacilioConstants.PM_V2.PM_V2_PLANNER);
        if (pmPlannerList == null || pmPlannerList.isEmpty()) {
            return false;
        }
        List<Long> triggerIds = new ArrayList<>();
        for(PMPlanner planner : pmPlannerList){
            if(planner.getTrigger() == null){
                return false;
            }
            triggerIds.add(planner.getTrigger().getId());

        }
        if(triggerIds != null && !triggerIds.isEmpty()){
           PlannedMaintenanceAPI.deleteTriggerByIds(triggerIds);
        }
        return false;
    }
}
