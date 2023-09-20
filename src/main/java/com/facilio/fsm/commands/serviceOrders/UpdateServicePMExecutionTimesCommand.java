package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.*;

public class UpdateServicePMExecutionTimesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrders)){
            List<Long> servicePMIds = serviceOrders.stream().filter(serviceOrder -> serviceOrder.getServicePlannedMaintenance()!=null).map(serviceOrder -> serviceOrder.getServicePlannedMaintenance().getId()).collect(Collectors.toList());
            for(Long servicePMId : servicePMIds){
                updateExecutionTimeInServicePM(servicePMId);
            }
        }
        return false;
    }
    private void updateExecutionTimeInServicePM(Long servicePMId)throws Exception{
        ServicePlannedMaintenanceContext servicePlannedMaintenance = V3RecordAPI.getRecord("servicePlannedMaintenance", servicePMId,ServicePlannedMaintenanceContext.class);
        Long nextRun = getNextRun(servicePMId);
        Long lastRun = getLastRun(servicePMId);
        if(nextRun!=null){
            servicePlannedMaintenance.setNextRun(nextRun);
        }
        if(lastRun!=null){
            servicePlannedMaintenance.setLastRun(lastRun);
        }
        if(nextRun!=null || lastRun!=null){
            V3Util.processAndUpdateSingleRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE, servicePMId, FieldUtil.getAsJSON(servicePlannedMaintenance), null, null, null, null, null,null,null, null,null);
        }
    }
}
