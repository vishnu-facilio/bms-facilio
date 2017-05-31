package com.facilio.incidents.commands;

import org.apache.commons.chain.impl.ChainBase;

public class AddTicketChain extends ChainBase {
	public AddTicketChain() {
		// TODO Auto-generated constructor stub
		super();
		addCommand(new ValidateFieldsCommand());
		addCommand(new AddTicketCommand());
	}
}
