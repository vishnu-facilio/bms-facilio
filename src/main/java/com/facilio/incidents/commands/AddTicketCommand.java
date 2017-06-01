package com.facilio.incidents.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.incidents.tickets.TicketContext;
import com.facilio.incidents.tickets.TicketApi;

public class AddTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TicketContext ticketContext = (TicketContext) context;
		long ticketId = TicketApi.addTicket(ticketContext);
		ticketContext.setTicketId(ticketId);
		
		return true;
	}

}
