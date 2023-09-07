package com.facilio.fsm.commands.actuals;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderToolsContext;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.updateActualCostDuringUpdate;

public class SAToolCostAfterUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderToolsContext> serviceOrderTools = recordMap.get(moduleName);
        Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);
        if(CollectionUtils.isNotEmpty(serviceOrderTools) && changeSet!=null){
            for(ServiceOrderToolsContext serviceOrderTool : serviceOrderTools){
                if(serviceOrderTool.getServiceAppointment()!=null){
                    ServiceAppointmentContext serviceAppointment = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,serviceOrderTool.getServiceAppointment().getId());
                    List<UpdateChangeSet> changes =changeSet.get(serviceOrderTool.getId());
                    updateActualCostDuringUpdate(changes,serviceAppointment,moduleName);
                }
            }
        }
        return false;
    }
}
