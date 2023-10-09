package com.facilio.fsm.commands.timeSheet;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.context.TimeSheetStatusContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckRecordLockForTimeSheetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<Long> recordIds = Constants.getRecordIds(context);

        List<TimeSheetContext> timeSheets = new ArrayList<>();
        if(recordMap != null){
            timeSheets = (List<TimeSheetContext>) recordMap.get(context.get("moduleName"));
        } else if(CollectionUtils.isNotEmpty(recordIds)) {
            FacilioContext listContext = V3Util.getSummary(FacilioConstants.TimeSheet.TIME_SHEET,recordIds);
            timeSheets = Constants.getRecordList(listContext);
        }
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);

        if (CollectionUtils.isNotEmpty(timeSheets)) {
            for (TimeSheetContext timeSheet: timeSheets) {
                TimeSheetStatusContext status = V3RecordAPI.getRecord(FacilioConstants.TimeSheet.TIME_SHEET_STATUS, timeSheet.getStatus().getId(),TimeSheetStatusContext.class);
                if (eventType == EventType.EDIT) {
                    if (status.isRecordLocked()) {
                        throw new FSMException(FSMErrorCode.TIMESHEET_RECORD_LOCKED);
                    }
                } else if (eventType == EventType.DELETE) {
                    if (status.isDeleteLocked()) {
                        throw new FSMException(FSMErrorCode.TIMESHEET_RECORD_LOCKED);
                    }
                }
            }
        }
        return false;
    }
}
