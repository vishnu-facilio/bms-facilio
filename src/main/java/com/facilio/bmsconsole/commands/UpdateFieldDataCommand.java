package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class UpdateFieldDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		Map<String, Object> data = (Map<String, Object>) context.get(FacilioConstants.ContextNames.DATA_KEY);
		if (data != null && moduleData != null && StringUtils.isNotEmpty(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			switch (moduleName) {
			case "workorder":
				FacilioContext updateContext = new FacilioContext();
				WorkOrderContext workorder = FieldUtil.getAsBeanFromMap((Map<String, Object>) data.get("workorder"), WorkOrderContext.class);
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
				
				FacilioChain chain = TransactionChainFactory.getUpdateWorkOrderChain();
				chain.execute(updateContext);
				break;

			default:
				throw new Exception("No implementation found for module: " + moduleName);
			}
		}
		return false;
	}

}
