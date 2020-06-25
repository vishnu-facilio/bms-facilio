package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3TicketAPI;
import com.facilio.bmsconsoleV3.util.V3WorkOderAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.logging.Logger;

public class AddWorkOrderCommandV3 extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(AddWorkOrderCommandV3.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        if(CollectionUtils.isNotEmpty(wos)) {

            V3WorkOrderContext workOrder = wos.get(0);
            if(workOrder.getRequester() != null && workOrder.getRequester().getId() == -1) {
                workOrder.setRequester(null);
            }

            V3WorkOderAPI.handleSiteRelations(workOrder);
            V3TicketAPI.validateSiteSpecificData(workOrder);
            if (workOrder.getSiteId() == -1) {
                throw new IllegalArgumentException("Please select site");
            }

            workOrder.setCreatedBy(AccountUtil.getCurrentUser());
            if (workOrder.getScheduledStart() != null && workOrder.getScheduledStart() > 0) {
                workOrder.setCreatedTime(workOrder.getScheduledStart());
            } else {
                workOrder.setCreatedTime(workOrder.getCurrentTime());
            }

            if (workOrder.getParentWO() != null) {
                validateStatusOfParentAndChild(workOrder);
            }

            workOrder.setModifiedTime(workOrder.getCreatedTime());
            workOrder.setScheduledStart(workOrder.getCreatedTime());
            workOrder.setApprovalState(ApprovalState.YET_TO_BE_REQUESTED.getValue());

            if (workOrder.getPriority() == null || workOrder.getPriority().getId() == -1) {
                workOrder.setPriority(V3TicketAPI.getPriority(AccountUtil.getCurrentOrg().getId(), "Low"));
            }

            if(workOrder.getDuration() != null) {
                workOrder.setDueDate(workOrder.getCreatedTime()+(workOrder.getDuration()*1000));
            }
            workOrder.setEstimatedEnd(workOrder.getDueDate());

            V3TicketAPI.updateTicketAssignedBy(workOrder);

            CommonCommandUtil.handleLookupFormData(fields, workOrder.getData());
        }
        else {
            throw new IllegalArgumentException("WorkOrder Object cannot be null");
        }
        return false;
    }

    private void validateStatusOfParentAndChild(V3WorkOrderContext workOrder) throws Exception {
        V3WorkOrderContext parentWO = V3WorkOderAPI.getWorkOrder(workOrder.getParentWO().getId());
        FacilioStatus status = V3TicketAPI.getStatus(parentWO.getStatus().getId());
        if (status.getType() == FacilioStatus.StatusType.CLOSED) {
            throw new IllegalArgumentException("Cannot add open WO as a child to closed parent");
        }
    }

}
