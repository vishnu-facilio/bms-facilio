package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.activity.ServiceOrderActivityType;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddServiceOrderActivityForAddServiceAppointmentAction_ServiceAppointment_Command extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));

        if (CollectionUtils.isEmpty(serviceAppointments)) {
            return false;
        }

        Map<Long, ServiceAppointmentContext> serviceOrderServiceAppointmentMap = new HashMap<>();

        for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
            if (serviceAppointment.getServiceOrder() != null & serviceAppointment.getServiceOrder().getId() > 0) {
                serviceOrderServiceAppointmentMap.put(serviceAppointment.getServiceOrder().getId(), serviceAppointment);
            }
        }


        // Check for EventType to add history accordingly
        EventType eventType = (EventType) context.get("eventType");
        if (eventType == null) {
            List<EventType> eventTypes = (List<EventType>) context.get("eventTypeList");
            if (CollectionUtils.isNotEmpty(eventTypes)) {
                for (EventType event : eventTypes) {
                    switch (event) {
                        case CREATE:
                            eventType = EventType.CREATE;
                            break;
                        case DELETE:
                            eventType = EventType.DELETE;
                            break;
                    }
                }
            }
        }

        if (eventType == null) {
            // Add log
            return false;
        }

        ActivityType activityType = null;

        switch (eventType) {
            case CREATE:
                activityType = ServiceOrderActivityType.ADDED_SERVICE_APPOINTMENT;
                break;
            case DELETE:
                activityType = ServiceOrderActivityType.DELETED_SERVICE_APPOINTMENT;
                break;
        }

        if (activityType == null) {
            return false;
        }
        User user = AccountUtil.getCurrentUser();
        // Add Activity entry for the service order
        for (Map.Entry<Long, ServiceAppointmentContext> entry : serviceOrderServiceAppointmentMap.entrySet()) {
            JSONObject info = new JSONObject();
            if (user != null) {
                info.put("doneBy", user.getName());
            }
            ServiceAppointmentContext appointmentContext = entry.getValue();
            info.put("serviceAppointmentName", appointmentContext.getName());
            info.put("serviceAppointmentID", appointmentContext.getId());
            CommonCommandUtil.addActivityToContext(entry.getKey(), -1, activityType, info, (FacilioContext) context);
        }

        // Add Activity to ServiceOrder
        new AddActivitiesCommandV3(FacilioConstants.ContextNames.SERVICE_ORDER_ACTIVITY).executeCommand(context);
        context.put(FacilioConstants.ContextNames.ACTIVITY_LIST, new ArrayList<>());
        return false;
    }
}
