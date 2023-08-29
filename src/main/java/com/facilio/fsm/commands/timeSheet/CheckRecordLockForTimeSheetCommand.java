package com.facilio.fsm.commands.timeSheet;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.context.TimeSheetStatusContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;

public class CheckRecordLockForTimeSheetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        HashMap<String,Object> oldRecordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);

        List<TimeSheetContext> timeSheets = (List<TimeSheetContext>) recordMap.get(context.get("moduleName"));
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);

        if (CollectionUtils.isNotEmpty(timeSheets)) {
            for (TimeSheetContext timeSheet: timeSheets) {
                TimeSheetStatusContext status = V3RecordAPI.getRecord(FacilioConstants.TimeSheet.TIME_SHEET_STATUS, timeSheet.getStatus().getId(),TimeSheetStatusContext.class);
                if (eventType == EventType.EDIT) {
                    if (status.isRecordLocked()) {
                        throw new FSMException(FSMErrorCode.TIME_SHEET_RECORD_LOCKED);
                    }
                } else if (eventType == EventType.DELETE) {
                    if (status.isDeleteLocked()) {
                        throw new FSMException(FSMErrorCode.TIME_SHEET_RECORD_LOCKED);
                    }
                }
            }
        }
        return false;
    }
}
