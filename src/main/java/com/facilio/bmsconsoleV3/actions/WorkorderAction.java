package com.facilio.bmsconsoleV3.actions;


import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WorkorderAction extends V3Action {
    private static Logger LOGGER = LogManager.getLogger(WorkorderAction.class.getName());

    private List<Long> ids;
    private V3WorkOrderContext workOrder;

    public String close() throws Exception {

        if (CollectionUtils.isEmpty(ids)) {
            LOGGER.error("ids cannot be null");
            return ERROR;
        }

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        CommonCommandUtil.addEventType(EventType.CLOSE_WORK_ORDER, context);
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

        FacilioStatus closedState = TicketAPI.getStatus("Closed");
        Map<String, Object> mapping = new HashMap<>();

        // TODO::VR updating status to be removed when status is removed from view criteria
        mapping.put("status", closedState);


        JSONObject map = new JSONObject();
        map.put(FacilioConstants.ContextNames.CLOSE_ALL_FROM_BULK_ACTION,true);

        V3Util.updateBulkRecords("workorder", mapping, ids, map,null,false);

        return SUCCESS;
    }

    public String assign() throws Exception {

        if (CollectionUtils.isEmpty(ids)) {
            LOGGER.error("ids cannot be null");
            return ERROR;
        }

        if (workOrder.getAssignmentGroup() == null && workOrder.getAssignedTo() == null) {
            return SUCCESS;
        }
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ASSIGN_TICKET);
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
        context.put(FacilioConstants.ContextNames.REQUESTER, workOrder.getRequester());

        Map<String, Object> mapping = new HashMap<>();
        if (workOrder.getAssignmentGroup() != null) {
            mapping.put("assignmentGroup", workOrder.getAssignmentGroup());
        }
        if (workOrder.getAssignedTo() != null) {
            mapping.put("assignedTo", workOrder.getAssignedTo());
        }
        FacilioContext ctx = V3Util.updateBulkRecords("workorder", mapping, ids, false);

        return SUCCESS;
    }
}
