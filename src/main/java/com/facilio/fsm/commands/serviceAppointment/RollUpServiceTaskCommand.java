package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.*;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RollUpServiceTaskCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> serviceTaskIds = (List<Long>) context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_IDS);
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
        Boolean isTaskAdd = (Boolean) context.get(FacilioConstants.ServiceAppointment.IS_TASK_ADD);
        ServiceTaskStatusContext scheduled = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.SCHEDULED);
        ServiceTaskStatusContext newStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.NEW);
        ServiceTaskStatusContext dispatched = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.DISPATCHED);
        if(CollectionUtils.isNotEmpty(serviceAppointments)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
//                if (isTaskAdd) {
//                    FacilioContext saContext = V3Util.getSummary(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, Collections.singletonList(serviceAppointment.getId()));
//                    if (Constants.getRecordList(saContext) != null) {
//                        ServiceAppointmentContext sa = (ServiceAppointmentContext) Constants.getRecordList(saContext).get(0);
//                        if (sa != null) {
//                            List<Long> taskIds = sa.getServiceTasks().stream().map(ServiceAppointmentTaskContext::getId).collect(Collectors.toList());
//                            serviceTaskIds.addAll(taskIds);
//                        }
//                    }
//                }
                if (serviceAppointment.getStatus() != null) {
                    ServiceAppointmentTicketStatusContext appointmentStatus = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS, serviceAppointment.getStatus().getId());

                    if (CollectionUtils.isNotEmpty(serviceTaskIds)) {


                        if (eventType == EventType.EDIT) {

                            List<Long> oldTaskIds = (List<Long>) context.get(FacilioConstants.ContextNames.FieldServiceManagement.OLD_SERVICE_TASK_IDS);
                            List<Long> disjunction = (List<Long>) CollectionUtils.disjunction(oldTaskIds, serviceTaskIds);
                            List<Long> oldIds = (List<Long>) CollectionUtils.intersection(oldTaskIds, disjunction);
                            List<Long> newIds = (List<Long>) CollectionUtils.intersection(serviceTaskIds, disjunction);


                            for (Long id : oldIds) {


                                FacilioContext task = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, Collections.singletonList(id));
                                ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(task).get(0);

                                existingTask.setServiceAppointment(null);
                                existingTask.setStatus(newStatus);
                                V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, id, FieldUtil.getAsJSON(existingTask), null, null, null, null, null, null, null, null);


//
//                        ServiceTaskContext serviceTask = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,id);
////                        ServiceTaskContext serviceTask = new ServiceTaskContext();
////                        serviceTask.setId(id);
//                        existingTask.setServiceAppointment(null);
//                        existingTask.setStatus(ServiceTaskContext.ServiceTaskStatus.NEW.getIndex());
//                        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,id, FieldUtil.getAsJSON(serviceTask), null, null, null, null, null,null, null, null);
//                        V3RecordAPI.updateRecord(serviceTask, serviceTaskModule, Collections.singletonList(serviceAppointmentId));
                            }
                            for (Long taskId : newIds) {


                                FacilioContext task = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, Collections.singletonList(taskId));
                                ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(task).get(0);

                                existingTask.setServiceAppointment(serviceAppointment);

                                if (!appointmentStatus.getStatus().equals(FacilioConstants.ServiceAppointment.SCHEDULED)) {
                                    existingTask.setStatus(dispatched);
                                } else {
                                    existingTask.setStatus(scheduled);
                                }
                                V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, taskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null, null, null, null);
//                        V3RecordAPI.updateRecord(serviceTask, serviceTaskModule, Collections.singletonList(serviceAppointmentId));
                            }

                        } else {

                            for (Long serviceTaskId : serviceTaskIds) {


                                FacilioContext task = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, Collections.singletonList(serviceTaskId));
                                ServiceTaskContext existingTask = (ServiceTaskContext) Constants.getRecordList(task).get(0);


                                existingTask.setServiceAppointment(serviceAppointment);
                                if (appointmentStatus.getStatus() == FacilioConstants.ServiceAppointment.DISPATCHED) {
                                    existingTask.setStatus(dispatched);
                                } else {
                                    existingTask.setStatus(scheduled);
                                }


                                V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, serviceTaskId, FieldUtil.getAsJSON(existingTask), null, null, null, null, null, null, null, null);
//                    V3RecordAPI.updateRecord(serviceTask, serviceTaskModule, Collections.singletonList(serviceAppointmentId));
                            }
                        }
                    }
                }
            }
        }

        return false;
        }

}
