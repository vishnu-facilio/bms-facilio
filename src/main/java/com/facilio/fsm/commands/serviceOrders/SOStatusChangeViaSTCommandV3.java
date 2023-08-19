package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.activity.ServiceTaskActivityType;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceOrderAPI.getServiceTasksByServiceOrder;
import static com.facilio.fsm.util.ServiceOrderAPI.updateServiceOrder;

public class SOStatusChangeViaSTCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = String.valueOf(context.get("moduleName"));
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        ServiceOrderTicketStatusContext newStatus = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.NEW);
        ServiceOrderTicketStatusContext scheduledStatus = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.SCHEDULED);
        ServiceOrderTicketStatusContext inProgressStatus = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.IN_PROGRESS);
        String newTask = FacilioConstants.ContextNames.ServiceTaskStatus.NEW;
        String completedTask = FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED;
        String inProgressTask = FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS;
        String scheduledTask = FacilioConstants.ContextNames.ServiceTaskStatus.SCHEDULED;
        List<ServiceTaskContext> dataList = (List<ServiceTaskContext>) recordMap.get(moduleName);
        Map<String, Map<Long, ServiceTaskContext>> oldrecordMap = (Map<String, Map<Long, ServiceTaskContext>>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);
        Map<Long, ServiceTaskContext> oldMap = oldrecordMap != null ? oldrecordMap.get(moduleName) : new HashMap<>();
        for(ServiceTaskContext task : dataList) {
            if(task.getServiceOrder() != null){
                //Task adding Activity in service Order
//                JSONObject changeObj = new JSONObject();
//                changeObj.put("name", task.getName());
//                CommonCommandUtil.addActivityToContext(task.getServiceOrder().getId(), System.currentTimeMillis(), ServiceTaskActivityType.ADD_SO, changeObj, (FacilioContext) context);
                Long orderId = task.getServiceOrder().getId();
                if(orderId != null){
                    ServiceTaskContext oldTask = oldMap.get(task.getId());
                    ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,orderId);
                    if(task.getStatus()!=null && task.getStatus().getName().equals(newTask) || ( oldTask != null && oldTask.getStatus().getName().equals(completedTask) && task.getStatus().getName().equals(inProgressTask))){
                        if(serviceOrderInfo.getStatus() != null && (serviceOrderInfo.getStatus().getId() != newStatus.getId() && serviceOrderInfo.getStatus().getId() != scheduledStatus.getId())){
//                        if(serviceOrderInfo.getStatus() != null && (serviceOrderInfo.getStatus().getId() != inProgressStatus.getId())){
                            serviceOrderInfo.setStatus(ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.IN_PROGRESS));
                            //serviceOrderInfo.setStatus( (task.getStatus() == ServiceTaskContext.ServiceTaskStatus.NEW.getIndex()) ? ServiceOrderContext.ServiceOrderStatus.NEW : ServiceOrderContext.ServiceOrderStatus.IN_PROGRESS);
//                            if(task.getStatus() == ServiceTaskContext.ServiceTaskStatus.REOPENED.getIndex()){
                                serviceOrderInfo.setActualEndTime(null);
                                serviceOrderInfo.setActualDuration(null);
//                            }
                            updateServiceOrder(serviceOrderInfo);
                        }
                    }else {
                        List<ServiceTaskContext> serviceTasks = getServiceTasksByServiceOrder(orderId);
                        if(serviceOrderInfo.getStatus() != null && serviceOrderInfo.getStatus().getId() == newStatus.getId() && task.getStatus()!=null && task.getStatus().getName().equals(scheduledTask)){
                            serviceOrderInfo.setStatus(scheduledStatus);
                            updateServiceOrder(serviceOrderInfo);
                        } else if ( task.getStatus()!=null && task.getStatus().getName().equals(inProgressTask) &&  (serviceOrderInfo.getStatus() != null && (serviceOrderInfo.getStatus().getId() == newStatus.getId() ||  serviceOrderInfo.getStatus().getId() == scheduledStatus.getId()))){
                            serviceOrderInfo.setStatus(inProgressStatus);
                            if(serviceOrderInfo.getActualStartTime() == null){
                                serviceOrderInfo.setActualStartTime(System.currentTimeMillis());
                            }
                            updateServiceOrder(serviceOrderInfo);
                        }
                        else{
                            Long completedCount = 0L;
                            for(ServiceTaskContext soTask: serviceTasks){
                                if(soTask.getServiceAppointment() != null && soTask.getServiceAppointment().getId() > 0){
                                    if(soTask.getStatus().getName().equals(completedTask)){
                                        completedCount++;
                                    }
                                }
                            }
                            if(serviceTasks.size() == completedCount){
                                serviceOrderInfo.setStatus(ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED));
                                Long startDuration = serviceOrderInfo.getActualStartTime();
                                Long endDuration = System.currentTimeMillis();
                                serviceOrderInfo.setActualEndTime(endDuration);
                                serviceOrderInfo.setActualDuration(endDuration - startDuration);
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
