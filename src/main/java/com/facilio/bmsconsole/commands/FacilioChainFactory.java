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
		c.addCommand(new AddAttachmentRelationshipCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetTicketModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetTicketCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketListChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetTicketModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetTicketListCommand());
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
	
	public static Chain getTaskListChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetTaskModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetTaskListCommand());
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
	
	public static Chain getAllCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetCampusModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetAllCampusCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetCampusModuleTableNames());
		c.addCommand(new LoadActionFormCommand());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateCampusFieldsCommand());
		c.addCommand(new SetCampusModuleTableNames());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new AddCampusCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getCampusDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetCampusModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetCampusCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetBuildingModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetAllBuildingCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetBuildingModuleTableNames());
		c.addCommand(new LoadActionFormCommand());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateBuildingFieldsCommand());
		c.addCommand(new SetBuildingModuleTableNames());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new AddBuildingCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getBuildingDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetBuildingModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetBuildingCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllFloorChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetFloorModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetAllFloorCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewFloorChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetFloorModuleTableNames());
		c.addCommand(new LoadActionFormCommand());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddFloorChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateFloorFieldsCommand());
		c.addCommand(new SetFloorModuleTableNames());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new AddFloorCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getFloorDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetFloorModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetFloorCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetSpaceModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetAllSpaceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetSpaceModuleTableNames());
		c.addCommand(new LoadActionFormCommand());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateSpaceFieldsCommand());
		c.addCommand(new SetSpaceModuleTableNames());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new AddSpaceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getSpaceDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetSpaceModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetSpaceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllZoneChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetZoneModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetAllZoneCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewZoneChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetZoneModuleTableNames());
		c.addCommand(new LoadActionFormCommand());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddZoneChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateZoneFieldsCommand());
		c.addCommand(new SetZoneModuleTableNames());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new AddZoneCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getZoneDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetZoneModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetZoneCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAttachmentChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AddAttachmentRelationshipCommand());
		return c;
	}
	
	public static Chain getDeleteAttachmentChain() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteAttachmentCommand());
		return c;
	}
	
	public static Chain getAllAreaChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetAllAreaCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain processImportData() {
		Chain c = new ChainBase();
		c.addCommand(new SetZoneModuleTableNames());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadFieldsCommand());
		c.addCommand(new GetZoneCommand());
		addCleanUpCommand(c);
		return c;
	}
}
