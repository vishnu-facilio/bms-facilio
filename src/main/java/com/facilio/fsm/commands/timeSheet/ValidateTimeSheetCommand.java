package com.facilio.fsm.commands.timeSheet;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.context.TimeSheetTaskContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateTimeSheetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        HashMap<String,Object> oldRecordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);

        List<TimeSheetContext> timeSheets = (List<TimeSheetContext>) recordMap.get(context.get("moduleName"));
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        if (CollectionUtils.isNotEmpty(timeSheets)) {
            for (TimeSheetContext timeSheet: timeSheets) {
                List<TimeSheetTaskContext> timeSheetTasks = timeSheet.getServiceTasks();
                if(CollectionUtils.isNotEmpty(timeSheetTasks)){
                    List<Long> taskIds = timeSheetTasks.stream().map(TimeSheetTaskContext::getId).collect(Collectors.toList());
                    boolean isTaskCompleted = checkForCompletedTask(taskIds);
                    if(isTaskCompleted){
                        if(timeSheet.getEndTime() == null){
                            throw new FSMException(FSMErrorCode.TIMESHEET_TASK_STATUS_ERROR);
                        }
                    }
                }


                if (timeSheet.getStartTime() != null && timeSheet.getEndTime() != null) {

                    timeSheet.setStatus(ServiceAppointmentUtil.getTimeSheetStatus(FacilioConstants.TimeSheet.COMPLETED));
                    timeSheet.setDuration((timeSheet.getEndTime()-timeSheet.getStartTime())/1000);
                    if (timeSheet.getStartTime() > timeSheet.getEndTime()) {
                        throw new FSMException(FSMErrorCode.TIME_SHEET_TIME_MISMATCH);
                    }
                }
                else if(timeSheet.getStartTime() != null && timeSheet.getEndTime() == null){
                    timeSheet.setStatus(ServiceAppointmentUtil.getTimeSheetStatus(FacilioConstants.TimeSheet.IN_PROGRESS));
                }
                if(eventType == EventType.EDIT){
                    Map<Long,Object> oldTimeSheets = (Map<Long,Object>) oldRecordMap.get(context.get("moduleName"));
                    TimeSheetContext oldTimeSheet = (TimeSheetContext) oldTimeSheets.get(timeSheet.getId());
                    if(timeSheet.getFieldAgent()!=oldTimeSheet.getFieldAgent()) {
                        throw new FSMException(FSMErrorCode.UPDATE_PREVENT);
                    }
                    else if(timeSheet.getServiceAppointment()!=oldTimeSheet.getServiceAppointment()) {
                        throw new FSMException(FSMErrorCode.UPDATE_PREVENT);
                    }
                    else if(timeSheet.getServiceTasks()!=oldTimeSheet.getServiceTasks()) {
                        throw new FSMException(FSMErrorCode.UPDATE_PREVENT);
                    }


                }

            }
        }

        return false;
    }

    public static boolean checkForCompletedTask(List<Long> taskIds)throws Exception {

        FacilioField status = Constants.getModBean().getField("name",FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS);

        List<String>taskStatus = new ArrayList<>();
        taskStatus.add(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED);
        taskStatus.add(FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(status))
                .table("Service_Task")
                .innerJoin("Service_Task_Status")
                .on("Service_Task.STATUS=Service_Task_Status.ID")
                .andCondition((CriteriaAPI.getCondition("Service_Task.ID", "id", StringUtils.join(taskIds,","), NumberOperators.EQUALS)))
                .andCondition((CriteriaAPI.getCondition("Service_Task_Status.NAME", "name", StringUtils.join(taskStatus,","), StringOperators.IS)));

        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            return true;
        }
        return false;
    }
}
