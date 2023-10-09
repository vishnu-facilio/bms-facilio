package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
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

public class AddServiceOrderActivityForAddServiceTaskAction_ServiceOrder_Command extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrderList = (List<ServiceOrderContext>) recordMap.get(context.get("moduleName"));

        // Add Activity to ServiceOrder
        if (CollectionUtils.isNotEmpty(serviceOrderList)) {
            Map<Long, Integer> taskServiceOrderCountMap = new HashMap<>();
            for (ServiceOrderContext serviceOrder : serviceOrderList) {
                if (serviceOrder.getRelations() != null &&
                        serviceOrder.getRelations().get("serviceTask") != null &&
                        serviceOrder.getRelations().get("serviceTask").get(0) != null &&
                        serviceOrder.getRelations().get("serviceTask").get(0).getData() != null
                ) {
                    List<ServiceTaskContext> serviceTaskContextList = (List<ServiceTaskContext>) serviceOrder.getRelations().get("serviceTask").get(0).getData();
                    if (CollectionUtils.isNotEmpty(serviceTaskContextList)) {
                        taskServiceOrderCountMap.put(serviceOrder.getId(), serviceTaskContextList.size());
                    }
                }
            }

            // Add Activity entry for the service order
            for (Map.Entry<Long, Integer> entry : taskServiceOrderCountMap.entrySet()) {
                JSONObject info = new JSONObject();
                info.put("taskCount", entry.getValue());
                CommonCommandUtil.addActivityToContext(entry.getKey(), -1, ServiceOrderActivityType.ADDED_TASK, info, (FacilioContext) context);
            }
        }
        return false;
    }
}
