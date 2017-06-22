package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

public class FacilioChainFactory {

	public static Chain getOrgSignupChain()
	{
		Chain c =new ChainBase();
		c.addCommand(new CreateUserCommand());
		return c;
	}
	
	public static Command getAddIncidentCommand() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateFieldsCommand());
		c.addCommand(new AddTicketCommand());
		
		return c;
	}

	public static Command getAddUserCommand() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateFieldsCommand());
		c.addCommand(new AddUserCommand());
		
		return c;
	}
	
	public static Command getUpdateUserCommand() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateUserCommand());
		
		return c;
	}
	
	public static Command getAddGroupCommand() {
		Chain c = new ChainBase();
		//c.addCommand(new ValidateFieldsCommand());
		c.addCommand(new AddGroupCommand());
		
		return c;
	}
	
	public static Command getUpdateGroupCommand() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateGroupCommand());
		
		return c;
	}
}
