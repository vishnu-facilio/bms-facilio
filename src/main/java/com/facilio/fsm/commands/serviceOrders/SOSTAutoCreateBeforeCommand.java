package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fsm.context.ServiceTaskStatusContext;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SOSTAutoCreateBeforeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks = (List<ServiceTaskContext>) recordMap.get(context.get("moduleName"));
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        List<FacilioField> serviceAppointmentFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(serviceAppointmentFields);

        for(ServiceTaskContext task : serviceTasks) {
            if(task.getServiceOrder() != null && task.getServiceOrder().getId() > 0)
            {
                ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,task.getServiceOrder().getId());
                if(serviceOrderInfo.isAutoCreateSa()){
                    //fetching all the service appointments which are mapped to the particular service order
                    List<LookupField> fetchSupplementsList = Arrays.asList((LookupField) fieldMap.get("status"));

                            SelectRecordsBuilder<ServiceAppointmentContext> selectAppointmentsBuilder = new SelectRecordsBuilder<ServiceAppointmentContext>();
                    selectAppointmentsBuilder.select(serviceAppointmentFields)
                            .module(serviceAppointmentModule)
                            .beanClass(ServiceAppointmentContext.class)
                            .fetchSupplements(fetchSupplementsList)
                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceOrder"),String.valueOf(serviceOrderInfo.getId()), StringOperators.IS));
                    ServiceAppointmentContext selectAppointments = selectAppointmentsBuilder.fetchFirst();
                    if(selectAppointments != null){
                        task.setServiceAppointment(selectAppointments);
                        ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.SCHEDULED);
                        task.setStatus(taskStatus);
                    }
                }
            }
        }
        return false;
    }
}
