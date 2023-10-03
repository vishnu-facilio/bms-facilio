package com.facilio.fsm.commands.trip;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.*;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckRecordLockForTripCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<Long> recordIds = Constants.getRecordIds(context);

        List<TripContext> trips = new ArrayList<>();
        if(recordMap != null) {
            trips = (List<TripContext>) recordMap.get(context.get("moduleName"));
        }else if(CollectionUtils.isNotEmpty(recordIds)) {
            FacilioContext listContext = V3Util.getSummary(FacilioConstants.Trip.TRIP,recordIds);
            trips = Constants.getRecordList(listContext);
        }

        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);

        if (CollectionUtils.isNotEmpty(trips)) {
            for (TripContext trip: trips) {
                TripStatusContext status = V3RecordAPI.getRecord(FacilioConstants.Trip.TRIP_STATUS, trip.getStatus().getId(),TripStatusContext.class);
                if (eventType == EventType.EDIT) {
                    if (status.isRecordLocked()) {
                        throw new FSMException(FSMErrorCode.TRIP_RECORD_LOCKED);
                    }
                } else if (eventType == EventType.DELETE) {
                    if (status.isDeleteLocked()) {
                        throw new FSMException(FSMErrorCode.TRIP_RECORD_LOCKED);
                    }
                }
            }
        }
        return false;
    }
}
