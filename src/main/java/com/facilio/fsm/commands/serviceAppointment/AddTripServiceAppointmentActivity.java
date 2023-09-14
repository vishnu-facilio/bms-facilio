package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.activity.ServiceAppointmentActivityType;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.context.TripContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddTripServiceAppointmentActivity extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<Long> recordIds = Constants.getRecordIds(context);

        List<TripContext> trips = new ArrayList<>();
        if(recordMap != null){
            trips = (List<TripContext>) recordMap.get(context.get("moduleName"));
        } else if(CollectionUtils.isNotEmpty(recordIds)) {
            FacilioContext listContext = V3Util.getSummary(FacilioConstants.Trip.TRIP,recordIds);
            trips = Constants.getRecordList(listContext);
        }
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);

        if (CollectionUtils.isNotEmpty(trips)) {
            for (TripContext trip: trips) {
                if (eventType == EventType.DELETE) {
                    JSONObject info = new JSONObject();
                    info.put("trip",trip.getCode());
                    info.put("doneBy", AccountUtil.getCurrentUser().getName());
                    CommonCommandUtil.addActivityToContext(trip.getServiceAppointment().getId(),-1,ServiceAppointmentActivityType.TRIP_DELETED,info,(FacilioContext) context);
                    new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY).executeCommand(context);
                }
            }
        }
        return false;
    }
}
