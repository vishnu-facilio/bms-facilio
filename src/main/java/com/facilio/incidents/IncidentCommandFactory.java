package com.facilio.incidents;

import org.apache.commons.chain.Command;

import com.facilio.incidents.commands.AddTicketChain;

public class IncidentCommandFactory {
	public static Command getAddIncidentCommand() {
		return new AddTicketChain();
	}
}
