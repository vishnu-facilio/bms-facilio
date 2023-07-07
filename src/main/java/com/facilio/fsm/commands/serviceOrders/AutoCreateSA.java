package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoCreateSA extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = (List<ServiceOrderContext>) recordMap.get(context.get("moduleName"));
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        List<FacilioField> serviceTaskFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String, FacilioField> serviceTaskMap = FieldFactory.getAsMap(serviceTaskFields);
        for(ServiceOrderContext order : serviceOrders) {
            if(order.getAutoCreateSa()){
                ServiceAppointmentContext sa = new ServiceAppointmentContext();
                sa.setAppointmentType(ServiceAppointmentContext.AppointmentType.SERVICE_WORK_ORDER);
                sa.setName("Service Appointment for "+ order.getName() + "(AC)");
                sa.setActualStartTime(order.getActualStartTime());
                sa.setActualEndTime(order.getActualEndTime());
                sa.setSite(order.getSite());
                //fetch the list of all tasks against the service order
                SelectRecordsBuilder<ServiceTaskContext> serviceTasksBuilder = new SelectRecordsBuilder<ServiceTaskContext>();
                serviceTasksBuilder.select(serviceTaskFields)
                        .module(serviceTaskModule)
                        .beanClass(ServiceTaskContext.class)
                        .andCondition(CriteriaAPI.getCondition(serviceTaskMap.get("serviceOrder"),String.valueOf(order.getId()), StringOperators.IS ));
                //Need to add it against the service appointment
                List<ServiceTaskContext> serviceTaskList = serviceTasksBuilder.get();
                if(CollectionUtils.isNotEmpty(serviceTaskList)) {
                    for (ServiceTaskContext st : serviceTaskList) {

                    }
                }
//                sa.setServiceTasks(order.getServiceTasks());
            }
        }

        return false;
    }
}
