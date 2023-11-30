package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ConstructTriggerNameCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServicePMTriggerContext> servicePMTriggers = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(servicePMTriggers)){
            for(ServicePMTriggerContext servicePMTrigger : servicePMTriggers) {
                if (servicePMTrigger != null) {
                    if (servicePMTrigger.getFrequencyEnum() == null) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Trigger Frequency is required");
                    }
                    Long startTime = servicePMTrigger.getStartTime() != null ? servicePMTrigger.getStartTime() : System.currentTimeMillis();
                    ScheduleInfo scheduleInfo = servicePMTrigger.getScheduleInfo();
                    String triggerName = scheduleInfo.getDescription(startTime);
                    servicePMTrigger.setName(triggerName);
                }
            }
        }
        return false;
    }
}
