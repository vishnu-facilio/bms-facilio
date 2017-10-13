package com.facilio.bmsconsole.commands;

import java.util.Calendar;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;

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

			if(woContext.getDescription() == null || woContext.getDescription().isEmpty()) {
				throw new IllegalArgumentException("Description is invalid");
			}
			else {
				woContext.setDescription(woContext.getDescription().trim());
			}

			if(woContext.getDueDate() == 0) {
				Calendar cal = Calendar.getInstance();
				woContext.setDueDate((cal.getTimeInMillis())+TicketContext.DEFAULT_DURATION);
			}
		}
		else if (context instanceof UserContext) {

			UserContext userContext = (UserContext) context;

			if(userContext.getEmail() == null || userContext.getEmail().isEmpty()) {
				throw new IllegalArgumentException("Email is invalid");
			}

			if(userContext.getPassword() == null || userContext.getPassword().isEmpty()) {
				throw new IllegalArgumentException("Password is invalid");
			}
		}
		return false;
	}

}
