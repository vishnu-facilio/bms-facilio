package com.facilio.bmsconsole.commands;

import java.util.Calendar;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;

public class ValidateWorkOrderFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext woContext = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if (woContext != null) {

			if(woContext.getSubject() == null || woContext.getSubject().isEmpty()) {
				throw new IllegalArgumentException("Subject is invalid");
			}
			else {
				woContext.setSubject(woContext.getSubject().trim());
			}
			
			if (woContext.getSiteId() == -1 && woContext.getResource() != null && woContext.getResource().getId() != -1) {
				ResourceContext resource = ResourceAPI.getResource(woContext.getResource().getId());
				woContext.setSiteId(resource.getSiteId());
			}

			if(woContext.getDescription() != null && !woContext.getDescription().isEmpty()) {
				woContext.setDescription(woContext.getDescription().trim());
			}

			if(woContext.getDueDate() == 0) {
				Calendar cal = Calendar.getInstance();
				woContext.setDueDate((cal.getTimeInMillis())+TicketContext.DEFAULT_DURATION);
			}
		}
		return false;
	}
}
