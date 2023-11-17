package com.facilio.fsm.commands.timeSheet;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckForExistingTimeSheetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<TimeSheetContext> timeSheets = (List<TimeSheetContext>) recordMap.get(context.get("moduleName"));

        if(CollectionUtils.isNotEmpty(timeSheets)){
            for(TimeSheetContext timeSheet : timeSheets){
                if(timeSheet.getFieldAgent() != null && timeSheet.getStartTime() != null && timeSheet.getServiceTasks() != null){
                    List<Long> recordIds = new ArrayList<>();
                    if(timeSheet.getId() > 0){
                        recordIds.add(timeSheet.getId());
                    }
                    List<TimeSheetContext> records = ServiceAppointmentUtil.getTimeSheetsForTimeRange(timeSheet.getFieldAgent().getId(), timeSheet.getStartTime(), timeSheet.getEndTime(),recordIds);
                    if(CollectionUtils.isNotEmpty(records)){
                        JSONObject errorData = new JSONObject();
                        errorData.put(FacilioConstants.TimeSheet.TIME_SHEET,records);
                        throw new FSMException(FSMErrorCode.TIMESHEET_ALREADY_RUNNING).setRelatedData(errorData);
                    }
                } else {
                        throw new FSMException(FSMErrorCode.TIME_SHEET_NOT_ENOUGH_DETAILS);
                }
            }
        }

        return false;
    }
}
