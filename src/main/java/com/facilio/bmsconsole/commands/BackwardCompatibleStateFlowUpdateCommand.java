package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;

// TODO to be removed once supported by every client
public class BackwardCompatibleStateFlowUpdateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		List<WorkOrderContext> oldWos = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField("moduleState", moduleName);
		if (workOrder != null && CollectionUtils.isNotEmpty(oldWos)) {
			
			// check whether moduleState is found
			if (field != null) {
				FacilioModule module = modBean.getModule(moduleName);
				FacilioStatus status = workOrder.getStatus();
				if (status == null && workOrder.getApprovalStateEnum() != null) {
					ApprovalState approveState = workOrder.getApprovalStateEnum();
					if (approveState == ApprovalState.APPROVED) {
						status = TicketAPI.getStatus(module, "Submitted");
					}
					else if (approveState == ApprovalState.REJECTED) {
						status = TicketAPI.getStatus(module, "Rejected");
					}
					workOrder.setStatus(status);
				}
				if(status != null) {
					for (WorkOrderContext wo : oldWos) {
						StateFlowRulesAPI.updateState(wo, module, status, false, context);
					}
				}
			}
		}
		return false;
	}

}
