package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class AddBulkWOCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WorkOrderContext> workOrders = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST);
        if (CollectionUtils.isNotEmpty(workOrders)) {
            for (WorkOrderContext wo : workOrders) {
                wo.parseFormData();

                addWorkOrder(wo);
            }
        }
        return false;
    }

    private void addWorkOrder(WorkOrderContext workorder) throws Exception {
        try {
            if (workorder.getSourceTypeEnum() == null) {
                workorder.setSourceType(TicketContext.SourceType.WEB_ORDER);
            }
            FacilioContext context = new FacilioContext();
            //not to send email while creating wo directly from portal
            if(workorder.getRequester() != null && workorder.getRequester().getUid() <= 0) {
                workorder.getRequester().setInviteAcceptStatus(true);
            }
            context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
            if (AccountUtil.getCurrentUser() == null) {
                context.put(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST, true);
            }

            context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
//            context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);

            context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

//            if (this.getFormName() != null && !this.getFormName().isEmpty()) {
//                context.put(FacilioConstants.ContextNames.FORM_NAME, this.getFormName());
//                context.put(FacilioConstants.ContextNames.FORM_OBJECT, workorder);
//            }

            Command addWorkOrder = TransactionChainFactory.getAddWorkOrderChain();
            addWorkOrder.execute(context);
        }
        catch (Exception e) {
            throw e;
        }
    }
}
