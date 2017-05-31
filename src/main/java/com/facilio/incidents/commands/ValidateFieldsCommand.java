package com.facilio.incidents.commands;

import java.util.Calendar;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.incidents.tickets.TicketContext;

public class ValidateFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TicketContext ticketContext = (TicketContext) context;
		
		if(ticketContext.getOrgId() == 0) {
			return true;
		}
		
		if(ticketContext.getRequestor() == null || ticketContext.getRequestor().isEmpty()) {
			return true;
		}
		
		if(ticketContext.getSubject() == null || ticketContext.getSubject().isEmpty()) {
			return true;
		}
		
		if(ticketContext.getDescription() == null || ticketContext.getDescription().isEmpty()) {
			return true;
		}
		
		if(ticketContext.getDueTime() == null) {
			Calendar cal = Calendar.getInstance();
			ticketContext.setDueTime((cal.getTimeInMillis()/1000)+TicketContext.DEFAULT_DURATION);
		}
		
		return false;
	}

}
