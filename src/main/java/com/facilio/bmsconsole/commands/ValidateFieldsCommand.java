package com.facilio.bmsconsole.commands;

import java.util.Calendar;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;

public class ValidateFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TicketContext ticketContext = (TicketContext) context;
		
		if(ticketContext.getOrgId() == 0) {
			throw new IllegalArgumentException("ORG ID is invalid");
		}
		
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
			throw new IllegalArgumentException("ORG ID is invalid");
		}
		else {
			ticketContext.setDescription(ticketContext.getDescription().trim());
		}
		
		if(ticketContext.getDueTime() == null) {
			Calendar cal = Calendar.getInstance();
			ticketContext.setDueTime((cal.getTimeInMillis()/1000)+TicketContext.DEFAULT_DURATION);
		}
		
		return false;
	}

}
