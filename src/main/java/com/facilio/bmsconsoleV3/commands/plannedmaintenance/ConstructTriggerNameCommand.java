package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.command.FacilioCommand;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;

/**
 * This command constructs the trigger name based on the schedule info, using the helper function
 * ScheduleInfo.getDescription().
 */
public class ConstructTriggerNameCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        if(MapUtils.isEmpty(recordMap)){
            return false;
        }
        List<PMTriggerV2> pmTriggerV2List = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(pmTriggerV2List)){
            for(PMTriggerV2 triggerV2 : pmTriggerV2List){
                if(triggerV2.getStartTime() <= 0L){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Execution start date is required");
                }
                ScheduleInfo scheduleInfo = triggerV2.getScheduleInfo();
                String triggerName = scheduleInfo.getDescription(triggerV2.getStartTime());
                if(triggerName!=null){
                    triggerV2.setName(triggerName);
                }
            }
        }
        return false;
    }
}
