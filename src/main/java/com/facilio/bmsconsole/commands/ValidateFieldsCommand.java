package com.facilio.bmsconsole.commands;

import java.util.Calendar;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.constants.FacilioConstants;

public class ValidateFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TicketContext ticketContext = (TicketContext) context.get(FacilioConstants.ContextNames.TICKET);
		if (ticketContext != null) {

			if(ticketContext.getRequester() == null || ticketContext.getRequester().isEmpty()) {
				throw new IllegalArgumentException("Requestor is invalid");
			}
			
			if(ticketContext.getSubject() == null || ticketContext.getSubject().isEmpty()) {
				throw new IllegalArgumentException("Subejct is invalid");
			}
			else {
				ticketContext.setSubject(ticketContext.getSubject().trim());
			}

			if(ticketContext.getDescription() == null || ticketContext.getDescription().isEmpty()) {
				throw new IllegalArgumentException("Description is invalid");
			}
			else {
				ticketContext.setDescription(ticketContext.getDescription().trim());
			}

			if(ticketContext.getDueDate() == 0) {
				Calendar cal = Calendar.getInstance();
				ticketContext.setDueDate((cal.getTimeInMillis()/1000)+TicketContext.DEFAULT_DURATION);
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
