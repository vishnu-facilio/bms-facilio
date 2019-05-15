package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

// TODO to be removed once supported by every client
public class BackwardCompatibleStateFlowUpdateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		List<WorkOrderContext> oldWos = (List<WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField("moduleState", moduleName);
		if (workOrder != null && CollectionUtils.isNotEmpty(oldWos)) {
			
			// check whether moduleState is found
			if (field != null) {
				TicketStatusContext status = workOrder.getStatus();
				for (WorkOrderContext wo : oldWos) {
					StateFlowRulesAPI.updateState(wo, modBean.getModule(moduleName), status, false, context);
				}
			}
		}
		return false;
	}

}
