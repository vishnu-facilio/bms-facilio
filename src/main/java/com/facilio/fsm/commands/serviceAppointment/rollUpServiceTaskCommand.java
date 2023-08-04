package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class rollUpServiceTaskCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> serviceTaskIds = (List<Long>) context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_IDS);
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceTaskModule = Constants.getModBean().getModule("serviceTask");
        FacilioField serviceAppointmentId = modBean.getField("serviceAppointment", "serviceTask");
        if(CollectionUtils.isNotEmpty(serviceAppointments) && CollectionUtils.isNotEmpty(serviceTaskIds)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
                ServiceAppointmentTicketStatusContext appointmentStatus = serviceAppointment.getStatus();
                if (eventType == EventType.EDIT) {

                    List<Long> oldTaskIds = (List<Long>) context.get(FacilioConstants.ContextNames.FieldServiceManagement.OLD_SERVICE_TASK_IDS);
                    List<Long> disjunction = (List<Long>) CollectionUtils.disjunction(oldTaskIds, serviceTaskIds);
                    List<Long> oldIds = (List<Long>) CollectionUtils.intersection(oldTaskIds, disjunction);
                    List<Long> newIds = (List<Long>) CollectionUtils.intersection(serviceTaskIds, disjunction);


                    for (Long id : oldIds) {
                        ServiceTaskContext serviceTask = new ServiceTaskContext();
                        serviceTask.setId(id);
                        serviceTask.setServiceAppointment(null);
                        serviceTask.setStatus(ServiceTaskContext.ServiceTaskStatus.NEW.getIndex());
                        V3RecordAPI.updateRecord(serviceTask, serviceTaskModule, Collections.singletonList(serviceAppointmentId));
                    }
                    for (Long taskId : newIds) {
                        ServiceTaskContext serviceTask = new ServiceTaskContext();
                        serviceTask.setId(taskId);
                        serviceTask.setServiceAppointment(serviceAppointment);
                        if(appointmentStatus != null && appointmentStatus.getStatus() != FacilioConstants.ServiceAppointment.SCHEDULED){
                            serviceTask.setStatus(ServiceTaskContext.ServiceTaskStatus.DISPATCHED.getIndex());
                        } else {
                            serviceTask.setStatus(ServiceTaskContext.ServiceTaskStatus.SCHEDULED.getIndex());
                        }
                        V3RecordAPI.updateRecord(serviceTask, serviceTaskModule, Collections.singletonList(serviceAppointmentId));
                    }

                }
                else {

                for (Long serviceTaskId : serviceTaskIds) {
                    ServiceTaskContext serviceTask = new ServiceTaskContext();
                    serviceTask.setId(serviceTaskId);
                    serviceTask.setServiceAppointment(serviceAppointment);
                    if(appointmentStatus != null && appointmentStatus.getStatus() == FacilioConstants.ServiceAppointment.DISPATCHED){
                        serviceTask.setStatus(ServiceTaskContext.ServiceTaskStatus.DISPATCHED.getIndex());
                    } else {
                        serviceTask.setStatus(ServiceTaskContext.ServiceTaskStatus.SCHEDULED.getIndex());
                    }
                    V3RecordAPI.updateRecord(serviceTask, serviceTaskModule, Collections.singletonList(serviceAppointmentId));
                }
                }
            }
        }

        return false;
        }

}
