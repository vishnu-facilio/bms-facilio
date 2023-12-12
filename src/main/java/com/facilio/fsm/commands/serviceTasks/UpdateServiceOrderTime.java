package com.facilio.fsm.commands.serviceTasks;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdateServiceOrderTime extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long taskId = (Long) context.get(Constants.RECORD_ID);

        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks = (List<ServiceTaskContext>) recordMap.get(context.get("moduleName"));
        String newStatus = FacilioConstants.ContextNames.ServiceTaskStatus.NEW;
        String inProgress = FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS;
        String completed = FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED;

        for(ServiceTaskContext task : serviceTasks) {
            //fetching the service task info based on the service task id
            ServiceTaskContext serviceTask = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,task.getId());
            //NEED TO DO: get service appointment from service task and fetch its details
            ServiceOrderContext serviceOrder = serviceTask.getServiceOrder();
            if(serviceOrder != null){
                Long serviceOrderId = serviceOrder.getId();
                List<ServiceTaskContext> serviceTaskList = ServiceOrderAPI.getServiceTasksByServiceOrder(serviceOrderId);
                ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,serviceOrderId);
                Boolean initOrder;
                Boolean completeOrder;
                if(CollectionUtils.isNotEmpty(serviceTaskList)) {
                    Long initCount = 0L;
                    Long completeCount = 0L;
                    for (ServiceTaskContext st : serviceTaskList) {
                        //if status of all other tasks are new and the status of current task is in progress we increase the initCount
                        if(st.getStatus()!=null && (task.getId() != st.getId() && st.getStatus().getName().equals(newStatus)) || (task.getId() == st.getId() && st.getStatus().getName().equals(inProgress))){
                            initCount= (long) serviceTaskList.size();
                        }
                        //if status of all tasks are in completed status then we increase the completeCount
                        else if (st.getStatus()!=null && st.getStatus().getName().equals(completed)){
                            completeCount++;
                        }
                    }
                    //Based on the count above we decide whether the service order has been initiated or in progress or closed
                    //we handle time/duration update for initiated and closed state, for in progress state we skip everything
                    initOrder = initCount == serviceTaskList.size() ? true : false;
                    completeOrder = completeCount == serviceTaskList.size() ? true : false;

                    //for initiated state we update the actual start time
                    if(initOrder && serviceOrderInfo.getActualStartTime() == null){
                        serviceOrderInfo.setActualStartTime(System.currentTimeMillis());
                    } else {
                        if(serviceOrderInfo.getActualEndTime() != null){
                            serviceOrderInfo.setActualEndTime(null);
                        }
                        if(serviceOrderInfo.getActualDuration() != null){
                            serviceOrderInfo.setActualDuration(null);
                        }
                    }
                    //for initiated state we update the actual end time and the duration
                    if (completeOrder){
                        serviceOrderInfo.setStatus(ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED));
                        long currentTime = System.currentTimeMillis();
                        serviceOrderInfo.setActualEndTime(currentTime);
                        serviceOrderInfo.setActualDuration(currentTime - serviceOrderInfo.getActualStartTime());
                        User user = AccountUtil.getCurrentAccount().getUser();
                        if(user != null){
                            V3PeopleContext completedBy = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,user.getPeopleId(),V3PeopleContext.class);
                            serviceOrderInfo.setCompletedBy(completedBy);
                        }
                        serviceOrderInfo.setCompletedTime(currentTime);
                    }
                    //if the service order is not in initiated or closed state we skip the update
                    if(initOrder || completeOrder){
                        ServiceOrderAPI.updateServiceOrder(serviceOrderInfo);
                    }
                }
            }
        }
        return false;
    }
}
