package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Calendar;

public class ValidateWorkOrderFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext woContext = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if (woContext != null) {

			if(woContext.getSubject() == null || woContext.getSubject().isEmpty()) {
				throw new IllegalArgumentException("Subject is invalid");
			}
			else {
				woContext.setSubject(woContext.getSubject().trim());
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
