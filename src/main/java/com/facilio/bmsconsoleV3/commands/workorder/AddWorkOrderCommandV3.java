package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3TicketAPI;
import com.facilio.bmsconsoleV3.util.V3WorkOderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
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
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please select site");
            }

            workOrder.setCreatedBy(AccountUtil.getCurrentUser());
            Boolean pm_exec= (Boolean) context.get(FacilioConstants.ContextNames.IS_PM_EXECUTION);
            if (workOrder.getScheduledStart() != null && workOrder.getScheduledStart() > 0) {
                if (pm_exec != null && pm_exec) {
                    workOrder.setCreatedTime(workOrder.getScheduledStart());
                } else {
                    workOrder.setCreatedTime(workOrder.getCurrentTime());
                }
            } else {
                workOrder.setCreatedTime(workOrder.getCurrentTime());
                workOrder.setScheduledStart(workOrder.getCurrentTime());
            }

            if (workOrder.getParentWO() != null) {
                validateStatusOfParentAndChild(workOrder);
            }

            workOrder.setModifiedTime(workOrder.getCreatedTime());
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
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "WorkOrder Object cannot be null");
        }
        return false;
    }

    private void validateStatusOfParentAndChild(V3WorkOrderContext workOrder) throws Exception {
        V3WorkOrderContext parentWO = V3WorkOderAPI.getWorkOrder(workOrder.getParentWO().getId());
        FacilioStatus status = V3TicketAPI.getStatus(parentWO.getStatus().getId());
        /* Skip this check for the Task-Deviation-WorkOrders' whose `deviationTaskUniqueId` will be filled in. This
           is done, as we create the Task-Deviation-WorkOrder, after closing the parent WorkOrder.
         */
        if (status.getType() == FacilioStatus.StatusType.CLOSED && StringUtils.isEmpty(workOrder.getDeviationTaskUniqueId())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot add open WO as a child to closed parent");
        }
    }

}
