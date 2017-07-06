package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

public class FacilioChainFactory {

	public static Chain getOrgSignupChain()
	{
		Chain c =new ChainBase();
		c.addCommand(new CreateUserCommand());
		c.addCommand(new AddDefaultModulesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewTicketChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetTicketModuleTableNames());
		c.addCommand(new LoadActionFormCommand());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTicketChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateFieldsCommand());
		c.addCommand(new SetTicketModuleTableNames());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new AddTicketCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetTicketModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetTicketCommand());
		c.addCommand(new SetTaskModuleTableNames());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetTasksOfTicketCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllTicketsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetTicketModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetAllTicketsCommand());
		addCleanUpCommand(c);
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

	public static Chain getAddFieldsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetModuleIdCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new SetColumnNameForNewCFsCommand());
		c.addCommand(new AddFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getUpdateGroupCommand() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateGroupCommand());
		
		return c;
	}
	
	private static void addCleanUpCommand(Chain c)
	{
		c.addCommand(new TransactionExceptionHandler());
	}
	
	public static Chain getNewTaskChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetTaskModuleTableNames());
		c.addCommand(new LoadActionFormCommand());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTaskChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateTasksFieldsCommand());
		c.addCommand(getAddScheduleObjectChain());
		c.addCommand(new SetTaskModuleTableNames());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new AddTaskCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTaskDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetTaskModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetTaskCommand());
		c.addCommand(new GetScheduleObjectCommand());
		c.addCommand(new GetNotesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllTasksChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetTaskModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetAllTasksCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTaskNoteChain() {
		Chain c = new ChainBase();
		c.addCommand(getAddNoteChain());
		c.addCommand(new AddTaskNoteCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddScheduleObjectChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateSchedulePropsCommand());
		c.addCommand(new AddScheduleObjectCommand());
		return c;
	}
	
	public static Chain getAddNoteChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateNotePropsCommand());
		c.addCommand(new AddNoteCommand());
		
		return c;
	}

	public static Chain getNewLocationChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadActionFormCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddLocationChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddLocationCommand());
		addCleanUpCommand(c);
		return c;
	}
}
