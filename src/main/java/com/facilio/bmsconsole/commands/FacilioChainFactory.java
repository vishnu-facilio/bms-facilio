package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class FacilioChainFactory {

	public static Chain getOrgSignupChain()
	{
		Chain c =new ChainBase();
		c.addCommand(new CreateUserCommand());
		c.addCommand(new AddDefaultModulesCommand());
		addCleanUPCommand(c);
		return c;
	}
	
	public static Chain getAddIncidentChain() {
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

	public static Chain getAddCustomFieldChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddPropsToCustomFieldsCommand());
		c.addCommand(new AddCustomFieldCommand());
		
		return c;
	}
	
	public static Command getUpdateGroupCommand() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateGroupCommand());
		
		return c;
	}
	private static void addCleanUPCommand(Chain c)
	{
		c.addCommand(new Command(){
			@Override 
			public boolean execute(Context arg0) throws Exception
			{
				if(arg0 instanceof FacilioContext)
				{
					FacilioContext fc = (FacilioContext)arg0;
					fc.cleanup();
				}
				return false;
			}
		});
	}	
	
	public static Chain getAddTaskChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateTasksFieldsCommand());
		c.addCommand(new AddTaskScheduleCommand());
		c.addCommand(new AddTaskCommand());
		
		return c;
	}
	
	public static Chain getAddScheduleObjectChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateSchedulePropsCommand());
		c.addCommand(new AddScheduleObjectCommand());
		
		return c;
	}
	
	public static Chain getAddTaskNoteChain() {
		Chain c = new ChainBase();
		c.addCommand(getAddNoteChain());
		c.addCommand(new AddTaskNoteCommand());
		
		return c;
	}
	
	public static Chain getAddNoteChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateNotePropsCommand());
		c.addCommand(new AddNoteCommand());
		
		return c;
	}
}
