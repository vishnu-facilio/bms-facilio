package com.facilio.fsm.commands.timeSheet;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RollUpTimeSheetFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);

        List<TimeSheetContext> timeSheets = (List<TimeSheetContext>) recordMap.get(context.get("moduleName"));
        if (CollectionUtils.isNotEmpty(timeSheets)) {
            for (TimeSheetContext timeSheet: timeSheets) {
                if(timeSheet.getServiceAppointment() != null) {
                    ServiceAppointmentContext appointment = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, timeSheet.getServiceAppointment().getId(), ServiceAppointmentContext.class);
                    timeSheet.setServiceOrder(appointment.getServiceOrder());
                    Map< String, Object > bodyParams = Constants.getBodyParams(context);
                    timeSheet.setType(TimeSheetContext.Type.MANUAL);
                    if (!MapUtils.isEmpty(bodyParams) && bodyParams.containsKey("system")) {
                        if ((boolean) bodyParams.get("system")) {
                            timeSheet.setType(TimeSheetContext.Type.SYSTEM);
                        }
                    }
                } else{
                    throw new FSMException(FSMErrorCode.TIME_SHEET_SA_MANDATORY);
                }
            }
        }
        return false;
    }
}
