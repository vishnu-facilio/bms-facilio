package com.facilio.fsm.commands.serviceOrders;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.activity.ServiceOrderActivityType;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AddServiceOrderActivityForAddServiceTaskAction_ServiceTask_Command
 */
public class AddServiceOrderActivityForAddServiceTaskAction_ServiceTask_Command extends FacilioCommand implements PostTransactionCommand {

    private Context context;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context = context;
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        if (this.context == null) {
            return false;
        }

        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks = (List<ServiceTaskContext>) recordMap.get(context.get("moduleName"));

        if (CollectionUtils.isNotEmpty(serviceTasks)) {
            Map<Long, Integer> taskServiceOrderCountMap = new HashMap<>();
            for (ServiceTaskContext task : serviceTasks) {
                ServiceOrderContext serviceOrderContext = task.getServiceOrder();
                if (serviceOrderContext != null) {
                    if (!taskServiceOrderCountMap.containsKey(serviceOrderContext.getId())) {
                        taskServiceOrderCountMap.put(serviceOrderContext.getId(), 0);
                    }
                    Integer previousTaskCount = taskServiceOrderCountMap.get(serviceOrderContext.getId());
                    taskServiceOrderCountMap.put(serviceOrderContext.getId(), ++previousTaskCount);
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

            if (eventType != null) {
                ActivityType activityType = null;

                switch (eventType) {
                    case CREATE:
                        activityType = ServiceOrderActivityType.ADDED_TASK;
                        break;
                    case DELETE:
                        activityType = ServiceOrderActivityType.DELETED_TASK;
                        break;
                }

                if (activityType != null) {
                    User user = AccountUtil.getCurrentUser();
                    // Add Activity entry for the service order
                    for (Map.Entry<Long, Integer> entry : taskServiceOrderCountMap.entrySet()) {
                        JSONObject info = new JSONObject();
                        if (user != null) {
                            info.put("doneBy", user.getName());
                        }
                        info.put("taskCount", entry.getValue());
                        CommonCommandUtil.addActivityToContext(entry.getKey(), -1, activityType, info, (FacilioContext) context);
                    }

                    // Add Activity to ServiceOrder
                    new AddActivitiesCommandV3(FacilioConstants.ContextNames.SERVICE_ORDER_ACTIVITY).executeCommand(context);
                }
            }

        }
        return false;
    }
}
