package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;

public class UpdateFieldDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		Map<String, Object> data = (Map<String, Object>) context.get(FacilioConstants.ContextNames.DATA_KEY);
		if (data != null && moduleData != null && StringUtils.isNotEmpty(moduleName)) {
			switch (moduleName) {
			case "workorder":
				FacilioContext updateContext = new FacilioContext();
				WorkOrderContext workorder = (WorkOrderContext) FieldUtil.getAsBeanFromMap((Map<String, Object>) data.get("workorder"), FacilioConstants.ContextNames.getClassFromModuleName(moduleName));
				workorder.setId(moduleData.getId());
				updateContext.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
				updateContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(moduleData.getId()));

				EventType activityType = EventType.EDIT;
				if (workorder.getStatus() != null) {
					EventType type = TicketAPI.getActivityTypeForTicketStatus(workorder.getStatus().getId());
					if (type != null) {
						activityType = type;
					}
				}
				else if(!updateContext.containsKey(FacilioConstants.ContextNames.EVENT_TYPE) && ((workorder.getAssignedTo() != null && workorder.getAssignedTo().getId() > 0) || (workorder.getAssignmentGroup() != null && workorder.getAssignmentGroup().getId() > 0)) ) {
					activityType = EventType.ASSIGN_TICKET;
				}
				updateContext.put(FacilioConstants.ContextNames.EVENT_TYPE, activityType);
				updateContext.put(FacilioConstants.ContextNames.COMMENT, data.get("comment"));
				
				Chain chain = TransactionChainFactory.getUpdateWorkOrderChain();
				chain.execute(updateContext);
				break;

			default:
				throw new Exception("No implementation found for module: " + moduleName);
			}
		}
		return false;
	}

}
