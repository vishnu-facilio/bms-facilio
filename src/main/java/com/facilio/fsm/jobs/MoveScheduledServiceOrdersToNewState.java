package com.facilio.fsm.jobs;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;

import java.util.Collections;

@Log4j
public class MoveScheduledServiceOrdersToNewState extends FacilioJob {
    @Override
    public void execute(JobContext jobContext) throws Exception {
        try{
            Long serviceOrderId = jobContext.getJobId();
            if(serviceOrderId!=null){
                if(validateServiceOrder(serviceOrderId)) {
                    V3Util.postCreateRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, Collections.singletonList(serviceOrderId),null, null, null);
                }
                else{
                    LOGGER.error( "#" + serviceOrderId + " - Service Order skipped from changing status to new");
                }
            }
        }catch(Exception e){
            CommonCommandUtil.emailException("MoveScheduledServiceOrdersToNewState", ""+jobContext.getJobId(), e);
            LOGGER.error("Error while changing status of service orders to new: ",e);
        }
    }
    private Boolean validateServiceOrder(Long serviceOrderId)throws Exception{
        ServiceOrderContext serviceOrder = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,serviceOrderId,ServiceOrderContext.class);
        if(serviceOrder!=null && serviceOrder.getServicePlannedMaintenance()!=null && serviceOrder.getServicePMTrigger()!=null){
            ServicePlannedMaintenanceContext servicePlannedMaintenance = V3RecordAPI.getRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,serviceOrder.getServicePlannedMaintenance().getId(),ServicePlannedMaintenanceContext.class);
            return servicePlannedMaintenance.getIsPublished();
        }
        return false;
    }
}
