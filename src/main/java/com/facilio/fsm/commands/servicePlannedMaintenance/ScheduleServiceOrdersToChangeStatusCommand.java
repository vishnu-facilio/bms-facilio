package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.tasker.FacilioTimer;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Log4j
public class ScheduleServiceOrdersToChangeStatusCommand extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        //schedules service orders to move in queue for changing status from upcoming to New
        List<ServiceOrderContext> serviceOrders = (List<ServiceOrderContext>) context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST);
        if(CollectionUtils.isNotEmpty(serviceOrders)){
            for(ServiceOrderContext serviceOrder : serviceOrders){
                try{
                    FacilioTimer.deleteJob(serviceOrder.getId(), "MoveScheduledServiceOrdersToNewState");
                    FacilioTimer.scheduleOneTimeJobWithTimestampInSec(serviceOrder.getId(), "MoveScheduledServiceOrdersToNewState", serviceOrder.getCreatedTime() / 1000, "priority");
                }
                catch (Exception e){
                    CommonCommandUtil.emailException("ServiceOrderStatusChangeScheduler", "ScheduleServiceOrdersToChangeStatusCommand | service order = " + serviceOrder.getId(), e);
                    LOGGER.error("Error while scheduling service orders to change status from upcoming to new",e);
                }
            }
        }
        return false;
    }
}
