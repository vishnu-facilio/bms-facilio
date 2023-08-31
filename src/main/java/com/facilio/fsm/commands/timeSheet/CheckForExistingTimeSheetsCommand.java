package com.facilio.fsm.commands.timeSheet;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;

public class CheckForExistingTimeSheetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<TimeSheetContext> timeSheets = (List<TimeSheetContext>) recordMap.get(context.get("moduleName"));

        if(CollectionUtils.isNotEmpty(timeSheets)){
            for(TimeSheetContext timeSheet : timeSheets){
                if(timeSheet.getFieldAgent() != null && timeSheet.getStartTime() != null){
                    List<TimeSheetContext> records = ServiceAppointmentUtil.getTimeSheetsForTimeRange(timeSheet.getFieldAgent().getId(), timeSheet.getStartTime(), timeSheet.getEndTime());
                    if(CollectionUtils.isNotEmpty(records)){
                        throw new FSMException(FSMErrorCode.TIMESHEET_ALREADY_RUNNING);
                    }
                } else {
                        throw new FSMException(FSMErrorCode.TIME_SHEET_NOT_ENOUGH_DETAILS);
                }
            }
        }

        return false;
    }
}
