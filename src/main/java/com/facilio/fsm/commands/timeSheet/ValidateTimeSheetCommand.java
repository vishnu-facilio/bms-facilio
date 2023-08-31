package com.facilio.fsm.commands.timeSheet;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateTimeSheetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        HashMap<String,Object> oldRecordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);

        List<TimeSheetContext> timeSheets = (List<TimeSheetContext>) recordMap.get(context.get("moduleName"));
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        if (CollectionUtils.isNotEmpty(timeSheets)) {
            for (TimeSheetContext timeSheet: timeSheets) {

                if (timeSheet.getStartTime() != null && timeSheet.getEndTime() != null) {

                    timeSheet.setStatus(ServiceAppointmentUtil.getTimeSheetStatus(FacilioConstants.TimeSheet.COMPLETED));
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
}
