package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;

import static com.facilio.fsm.util.ServiceOrderAPI.getServiceTasksByServiceOrder;
import static com.facilio.fsm.util.ServiceOrderAPI.updateServiceOrder;

public class SOStatusChangeViaSTCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = String.valueOf(context.get("moduleName"));
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);

        List<ServiceTaskContext> dataList = (List<ServiceTaskContext>) recordMap.get(moduleName);
        for(ServiceTaskContext task : dataList) {
            if(task.getServiceOrder() != null){
                Long orderId = task.getServiceOrder().getId();
                if(orderId != null){
                    ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,orderId);
                    if(task.getStatus() == ServiceTaskContext.ServiceTaskStatus.NEW.getIndex() || task.getStatus() == ServiceTaskContext.ServiceTaskStatus.REOPENED.getIndex()){
                        if(serviceOrderInfo.getStatus() != ServiceOrderContext.ServiceOrderStatus.NEW.getIndex()){
                            serviceOrderInfo.setStatus( (task.getStatus() == ServiceTaskContext.ServiceTaskStatus.NEW.getIndex()) ? ServiceOrderContext.ServiceOrderStatus.NEW : ServiceOrderContext.ServiceOrderStatus.IN_PROGRESS);
                            if(task.getStatus() == ServiceTaskContext.ServiceTaskStatus.REOPENED.getIndex()){
                                serviceOrderInfo.setActualEndTime(null);
                                serviceOrderInfo.setActualDuration(null);
                            }
                            updateServiceOrder(serviceOrderInfo);
                        }
                    }else {
                        List<ServiceTaskContext> serviceTasks = getServiceTasksByServiceOrder(orderId);
                        if(serviceOrderInfo.getStatus() == ServiceOrderContext.ServiceOrderStatus.NEW.getIndex() && task.getStatus() == ServiceTaskContext.ServiceTaskStatus.SCHEDULED.getIndex()){
                            serviceOrderInfo.setStatus(ServiceOrderContext.ServiceOrderStatus.SCHEDULED);
                            updateServiceOrder(serviceOrderInfo);
                        } else if ( task.getStatus() == ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex() &&  serviceOrderInfo.getStatus() < ServiceOrderContext.ServiceOrderStatus.IN_PROGRESS.getIndex()){
                            serviceOrderInfo.setStatus(ServiceOrderContext.ServiceOrderStatus.IN_PROGRESS);
                            updateServiceOrder(serviceOrderInfo);
                        }
                        else{
                            Long completedCount = 0L;
                            for(ServiceTaskContext soTask: serviceTasks){
                                if(soTask.getServiceAppointment() != null && soTask.getServiceAppointment().getId() > 0){
                                    if(soTask.getStatus().equals(ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex())){
                                        completedCount++;
                                    }
                                }
                            }
                            if(serviceTasks.size() == completedCount){
                                serviceOrderInfo.setStatus(ServiceOrderContext.ServiceOrderStatus.COMPLETED);
                                updateServiceOrder(serviceOrderInfo);
                            }

                        }
                    }
                }
            }
        }
        return false;
    }
}
