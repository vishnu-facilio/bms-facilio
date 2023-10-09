package com.facilio.fsm.commands.timeSheet;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.*;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UpdateSAandTasksOnTimeSheetCreateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<TimeSheetContext> timeSheets = (List<TimeSheetContext>) recordMap.get(context.get("moduleName"));

        if (CollectionUtils.isNotEmpty(timeSheets)) {
            for (TimeSheetContext timeSheet : timeSheets) {
                if(timeSheet.getServiceAppointment()!=null) {
                    FacilioContext appointmentList = V3Util.getSummary(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, Collections.singletonList(timeSheet.getServiceAppointment().getId()));
                    if (Constants.getRecordList(appointmentList) != null) {
                        ServiceAppointmentContext appointment = (ServiceAppointmentContext) Constants.getRecordList(appointmentList).get(0);

                            Long appointmentId = appointment.getId();
                            Long agentId = timeSheet.getFieldAgent().getId();
                            long workDuration = ServiceAppointmentUtil.getAppointmentDuration(agentId, appointmentId);
                            appointment.setActualDuration(workDuration);


                    List<TimeSheetTaskContext> tasks = timeSheet.getServiceTasks();
                    if (CollectionUtils.isNotEmpty(tasks)) {
                        List<Long> taskIds = new ArrayList<>();
                        for(TimeSheetTaskContext task:tasks){
                            taskIds.add(task.getId());
                        }
                        FacilioContext recordList = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, taskIds);
                        List<ServiceTaskContext> serviceTasks = Constants.getRecordList(recordList);
                        if (CollectionUtils.isNotEmpty(serviceTasks)) {
                            for (ServiceTaskContext task : serviceTasks) {
                                if (task.getActualStartTime() == null) {
                                    task.setActualStartTime(timeSheet.getStartTime());
                                }
                                String status = task.getStatus().getName();
                                //endtime???
                                //Moving Task to OnHold state if EndTime is filled in Timesheet creation
                                if (timeSheet.getEndTime() != null) {
                                    if (!status.equals(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED) && !status.equals(FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED) && !status.equals(FacilioConstants.ContextNames.ServiceTaskStatus.ON_HOLD) && !status.equals(FacilioConstants.ContextNames.ServiceTaskStatus.SCHEDULED)) {
                                        ServiceTaskStatusContext onHold = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.ON_HOLD);
                                        task.setStatus(onHold);
                                    }
                                } else {
                                    if (!status.equals(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED) && !status.equals(FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED) &&  !status.equals(FacilioConstants.ContextNames.ServiceTaskStatus.SCHEDULED)) {
                                        ServiceTaskStatusContext inProgress = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
                                        task.setStatus(inProgress);
                                    }
                                    //Updating SA to InProgress state

                                    String appointmentStatus = appointment.getStatus().getStatus();
                                    if (appointmentStatus.equals(FacilioConstants.ServiceAppointment.DISPATCHED)) {
                                        ServiceAppointmentTicketStatusContext inProgressState = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.IN_PROGRESS);
                                        appointment.setStatus(inProgressState);
                                        V3Util.processAndUpdateSingleRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,appointmentId, FieldUtil.getAsJSON(appointment), null, null, null, null, null, null, null, null,null);
                                    }
                                }
                                V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, task.getId(), FieldUtil.getAsJSON(task), null, null, null, null, null, null, null, null,null);

                            }
                        }}


                    }
                }
                else{
                    throw new FSMException(FSMErrorCode.TIME_SHEET_SA_MANDATORY);
                }
            }
        }





        return false;
    }
}
