package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderPlannedToolsContext;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.updateEstimatedCostDuringUpdate;

public class SAEstimatedToolCostAfterUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools = recordMap.get(moduleName);
        Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedTools) && changeSet!=null){
            for(ServiceOrderPlannedToolsContext serviceOrderPlannedTool : serviceOrderPlannedTools){
                if(serviceOrderPlannedTool.getServiceAppointment()!=null){
                    ServiceAppointmentContext serviceAppointment = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,serviceOrderPlannedTool.getServiceAppointment().getId());
                    List<UpdateChangeSet> changes =changeSet.get(serviceOrderPlannedTool.getId());
                    updateEstimatedCostDuringUpdate(changes,serviceAppointment,moduleName);
                }
            }
        }
        return false;
    }
}
