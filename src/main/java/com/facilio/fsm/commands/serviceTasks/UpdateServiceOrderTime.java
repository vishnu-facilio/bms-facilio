package com.facilio.fsm.commands.serviceTasks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdateServiceOrderTime extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long taskId = (Long) context.get(Constants.RECORD_ID);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        List<FacilioField> serviceTaskFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String, FacilioField> serviceTaskMap = FieldFactory.getAsMap(serviceTaskFields);
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        List<FacilioField> serviceOrderFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> soFieldMap = FieldFactory.getAsMap(serviceOrderFields);

        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks = (List<ServiceTaskContext>) recordMap.get(context.get("moduleName"));
        for(ServiceTaskContext task : serviceTasks) {
            //fetching the service task info based on the service task id
            ServiceTaskContext serviceTask = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,task.getId());
            //NEED TO DO: get service appointment from service task and fetch its details
            ServiceOrderContext serviceOrder = serviceTask.getServiceOrder();
            if(serviceOrder != null){
                Long serviceOrderId = serviceOrder.getId();
                //fetch all service appointments against the service order
                SelectRecordsBuilder<ServiceTaskContext> serviceTasksBuilder = new SelectRecordsBuilder<ServiceTaskContext>();
                serviceTasksBuilder.select(serviceTaskFields)
                        .module(serviceTaskModule)
                        .beanClass(ServiceTaskContext.class)
                        .andCondition(CriteriaAPI.getCondition(serviceTaskMap.get("serviceOrder"),String.valueOf(serviceOrderId), StringOperators.IS ));

                List<ServiceTaskContext> serviceTaskList = serviceTasksBuilder.get();

                Boolean initOrder;
                Boolean completeOrder;
                if(CollectionUtils.isNotEmpty(serviceTaskList)) {
                    Long initCount = 0L;
                    Long completeCount = 0L;
                    for (ServiceTaskContext st : serviceTaskList) {
                        //if status of all other tasks are new and the status of current task is in progress we increase the initCount
                        if(st.getStatus() == ServiceTaskContext.ServiceTaskStatus.NEW.getIndex() || (st.getId() == taskId && st.getStatus() == ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex()) ){
                            initCount++;
                        }
                        //if status of all tasks are in completed status then we increase the completeCount
                        else if (st.getStatus() == ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex() ){
                            completeCount++;
                        }
                    }
                    //Based on the count above we decide whether the service order has been initiated or in progress or closed
                    //we handle time/duration update for initiated and closed state, for in progress state we skip everything
                    initOrder = initCount == serviceTaskList.size() ? true : false;
                    completeOrder = completeCount == serviceTaskList.size() ? true : false;

                    ServiceOrderContext so = new ServiceOrderContext();
                    List<FacilioField> updateFields = new ArrayList<>();
                    //for initiated state we update the actual start time
                    if(initOrder != null && initOrder){
                        so.setActualStartTime(System.currentTimeMillis());
                        updateFields.add(soFieldMap.get("actualStartTime"));
                    }
                    //for initiated state we update the actual end time and the duration
                    if (completeOrder != null && completeOrder){
                        ServiceTaskContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,serviceOrderId);
                        so.setActualEndTime(System.currentTimeMillis());
                        so.setActualDuration(System.currentTimeMillis() - serviceOrderInfo.getActualStartTime());
                        updateFields.add(soFieldMap.get("actualEndTime"));
                        updateFields.add(soFieldMap.get("actualDuration"));
                    }
                    //if the service order is not in initiated or closed state we skip the update
                    if( initOrder != null || completeOrder != null){
                        V3RecordAPI.updateRecord(so,serviceOrderModule,updateFields);
                    }
                }
            }
        }
        return false;
    }
}
