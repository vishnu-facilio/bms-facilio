package com.facilio.bmsconsole.commands;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;

import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.leed.commands.AddConsumptionForLeed;
import com.facilio.leed.commands.AddEnergyMeterCommand;
import com.facilio.leed.commands.FetchArcAssetsCommand;
import com.facilio.leed.commands.LeedBuildingDetailsCommand;
import com.facilio.transaction.FacilioTransactionManager;

public class FacilioChainFactory {

	public static Chain getOrgSignupChain()
	{
		Chain c = new ChainBase();
		c.addCommand(new CreateAccountCommand());
		c.addCommand(new AddDefaultModulesCommand());
		c.addCommand(new AddEventModuleCommand());
		c.addCommand(new AddOrgInfoCommand());
		addCleanUpCommand(c);
		return c;
	}

	public static Chain getAddEnergyDataChain() {
		Chain c = new TransactionChain();
		//c.addCommand(SetTableNamesCommand.getForEner);
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}

	
	public static Chain getPickListChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadMainFieldCommand());
		c.addCommand(new GetPickListCommand());
	//	addCleanUpCommand(c);
		return c;
	}
	
	private static Chain getNewTicketChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
//		addCleanUpCommand(c);
		return c;
	}
	
	private static Chain getAddTicketChain() {
		Chain c = new TransactionChain();
		c.addCommand(new ValidateWorkOrderFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddTicketCommand());
		c.addCommand(new AddAttachmentRelationshipCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateTicketChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateTicketCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteTicketChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new DeleteTicketCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAssignTicketChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new AssignTicketCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketActivitiesChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetTicketActivitesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketStatusListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicketStatus());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketStatusListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketPriorityListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketPriorityListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketCategoryListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketCategoryListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddUserCommand() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateWorkOrderFieldsCommand());
		c.addCommand(new AddUserCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getChangeUserStatusCommand() {
		Chain c = new ChainBase();
		c.addCommand(new ChangeUserStatusCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getChangeTeamStatusCommand(){
		Chain c =new ChainBase();
		c.addCommand(new ChangeTeamStatusCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getUpdateUserCommand() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateUserCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getAddUserMobileSettingCommand() {
		Chain c = new ChainBase();
		c.addCommand(new AddUserMobileSettingCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getAddGroupCommand() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateWorkOrderFieldsCommand());
		c.addCommand(new AddGroupCommand());
		c.addCommand(new AddGroupMembersCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getAddRoleCommand() {
		Chain c = new ChainBase();
		c.addCommand(new AddRoleCommand());
		c.addCommand(new AddPermissionsCommand());
		addCleanUpCommand(c);
		return c;		
	}

	
	public static Command getUpdateGroupCommand() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateGroupCommand());
		c.addCommand(new AddGroupMembersCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getDeleteGroupCommand() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteGroupCommand());
		addCleanUpCommand(c);
		return c;
	}

	private static void addCleanUpCommand(Chain c)
	{
		c.addCommand(new TransactionExceptionHandler());
	}
	
	public static Chain getNewWorkOrderChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
//	public static Chain getAddWorkOrderWithTicketChain() {
//		Chain c = new TransactionChain();
//		c.addCommand(getAddTicketChain());
//        c.addCommand(getAddWorkOrderChain());
//        //c.addCommand(new AddTicketActivityCommand());
//		addCleanUpCommand(c);
//		return c;
//	}
	
	public static Chain getAddWorkOrderChain() {
		Chain c = new TransactionChain();
		c.addCommand(new AddRequesterCommand());
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new ValidateWorkOrderFieldsCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddWorkOrderCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE));
		c.addCommand(new AddTicketActivityCommand());
		c.addCommand(getAddTasksChain());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateWorkOrderChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateWorkOrderCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE));
		c.addCommand(new SendNotificationCommand());
		c.addCommand(new ClearAlarmOnWOCloseCommand());
		c.addCommand(new AddTicketActivityCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteWorkOrderChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_AGENT_NOTIFICATION_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkOrderDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkOrderListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new GenerateCondtionsFromFiltersCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateSortingQueryCommand());
		c.addCommand(new GetWorkOrderListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewWorkOrderRequestChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddWorkOrderRequestChain() {
		Chain c = new TransactionChain();
//		c.addCommand(getAddTicketChain());
		c.addCommand(new AddRequesterCommand());
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddWorkOrderRequestCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkOrderRequestDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderRequestCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkOrderRequestListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCondtionsFromFiltersCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateSortingQueryCommand());
		c.addCommand(new GetWorkOrderRequestListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateWorkOrderRequestChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateWorkOrderRequestCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new AddWOFromRequestCommand());
		//addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAlarmChain() {
		Chain c = new TransactionChain();
//		c.addCommand(getAddTicketChain());
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddAlarmCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new AddAlarmFollowersCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteAlarmChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new DeleteAlarmCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAlarmTemplateChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddAlarmTemplateCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAlarmListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateSortingQueryCommand());
		c.addCommand(new GetAlarmListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAlarmDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAlarmCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateAlarmChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateAlarmCommand());
		c.addCommand(new AddWOFromAlarmCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateAlarmAssetChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateAlarmAssetCommand());
		c.addCommand(new AddWOFromAlarmCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewTaskChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTaskChain() {
		Chain c = new TransactionChain();
		//c.addCommand(getAddTicketChain());
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new ValidateTasksCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddTaskCommand());
		c.addCommand(new AddTaskOptionsCommand());
		c.addCommand(new AddTaskTicketActivityCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTasksChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new ValidateTasksCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddTaskSectionsCommand());
		c.addCommand(new AddTasksCommand());
		c.addCommand(new AddTaskOptionsCommand());
		c.addCommand(new AddTaskTicketActivityCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateTaskChain() {
		Chain c = new TransactionChain();
		c.addCommand(new ValidateAndCreateValuesForInputTaskCommand());
		c.addCommand(getAddOrUpdateReadingValuesChain());
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateTaskCommand());
		c.addCommand(new UpdateClosedTasksCounterCommand());
		// c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getDeleteTaskChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new DeleteTaskCommand());
		return c;
	}
	
	public static Chain getTaskDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTaskCommand());
		c.addCommand(new GetTaskInputDataCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTaskListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTaskListCommand());
		c.addCommand(new GetTaskInputDataCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getRelatedTasksChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new GetRelatedTasksCommand());
		c.addCommand(new GetTaskInputDataCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addTaskSectionChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddTaskSectionCommand());
		c.addCommand(new UpdateTaskWithSectionCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getAllLocationsCommand() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain addLocationChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateLocationChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain deleteLocationChain() {
		
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getLocationChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetLocationCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command getAllSkillsCommand() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new GetAllSkillsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain addSkillChain()
	{
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new AddSkillCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Command updateSkillCommand() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new UpdateSkillCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	
	
	public static Command deleteSkillCommand(){
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		//c.addCommand(SetTableNamesCommand.getForSkill());
		//c.addCommand(new DeleteSkillCommand());
		addCleanUpCommand(c);
		return c;
	}
	
//	public static Chain getSkillChain() {
//		Chain c = new ChainBase();
//		c.addCommand(SetTableNamesCommand.getForSkill());
//		c.addCommand(new LoadModuleNameCommand());
//		c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new GetSkillCommand());
//		
//		addCleanUpCommand(c);
//		return c;
//	}
	
//	public static Chain getNewSkillChain() {
//		Chain c = new ChainBase();
//		c.addCommand(SetTableNamesCommand.getForSkill());
//		c.addCommand(new LoadModuleNameCommand());
//		c.addCommand(new LoadAllFieldsCommand());
//		addCleanUpCommand(c);
//		return c;
//	}
	
	
	
	public static Chain getAllCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllCampusCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateCampusFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddCampusCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getCampusDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetCampusCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getSiteReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new GetSiteReportCards());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllBuildingCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getBuildingReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new GetBuildingReportCards());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateBuildingFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddBuildingCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getBuildingDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetBuildingCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain AddEnergyMeterChain(){
		Chain c = new ChainBase();
		c.addCommand(new AddEnergyMeterCommand());
		addCleanUpCommand(c);
		return c;
	}
/*	public static Chain getBuildingUtilityProviderDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetBuildingCommand());
		c.addCommand(new GetUtilityProvidersCommand());
		addCleanUpCommand(c);
		return c;
	}*/
	

	public static Chain LeedDetailsPageChain()
	{
		Chain c = new ChainBase();
		c.addCommand(new LeedBuildingDetailsCommand());
		return c;
	}
	
	
	public static Chain addConsumptionDataChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddConsumptionForLeed());
		//c.addCommand(new AddConsumptionData());
		return c;
	}
	
	public static Chain FetchAssetsFromArcChain()
	{
		Chain c = new ChainBase();
		c.addCommand(new FetchArcAssetsCommand());
		return c;
	}
	
	public static Chain getAllFloorChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllFloorCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewFloorChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddFloorChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateFloorFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddFloorCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getFloorDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetFloorCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getFloorReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new GetFloorReportCards());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllSpaceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getIndependentSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetIndependentSpaceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateSpaceFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddSpaceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getSpaceDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetSpaceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getSpaceReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new GetSpaceReportCards());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllZoneChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllZoneCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllZoneChildrenChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllZoneChildrenCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewZoneChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddZoneChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateZoneFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddZoneCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getZoneDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetZoneCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewAssetChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAssetChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	public static Chain getWorkflowRulesChain() {
		Chain c = new TransactionChain();
		c.addCommand(new GetWorkflowRulesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkflowRuleOfTypeChain() {
		Chain c = new TransactionChain();
		c.addCommand(new GetWorkFlowOfRuleTypeCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateWorkflowRuleAction() {
		Chain c = new TransactionChain();
		c.addCommand(new UpdateWorkFlowRuleAction());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTemplateOfWorkflowRuleAction() {
		Chain c = new TransactionChain();
		c.addCommand(new CreateActionTemplateForWorkflowCommand());
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new UpdateTemplateInAction());
		c.addCommand(new DeleteActionTemplateCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateAssetChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteAssetChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAssetListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAssetDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAssetReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new GetAssetReportCards());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAttachmentChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AddAttachmentRelationshipCommand());
		c.addCommand(new AddAttachmentTicketActivityCommand());
		return c;
	}
	
	public static Chain getAddEnergyMeterChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddAdditionalPropsForEnergyMeterCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new AddVirtualMeterRelCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
		
	public static Chain getEnergyMeterListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new GenerateCondtionsFromFiltersCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddEnergyMeterPurposeChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeterPurpose());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getVirtualMeterChildrenChain() {
		Chain c = new TransactionChain();
		c.addCommand(new GetVirtualMeterChildrenCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAttachmentsChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetAttachmentsCommand());
		return c;
	}
	
	public static Chain getDeleteAttachmentChain() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteAttachmentCommand());
		return c;
	}
	
	public static Chain getAllAreaChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateSortingQueryCommand());
		c.addCommand(new GetAllAreaCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getBaseSpaceChildrenChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetBaseSpaceChildrenCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain processImportData() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetZoneCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getEmailSettingChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadSupportEmailsCommand());
		c.addCommand(new LoadEmailSettingCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateEmailSettingChain() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateEmailSettingCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	
	public static Chain getSupportEmailChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetSupportEmailCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddSupportEmailChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddSupportEmailCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddControllerChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddControllerCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getControllerSettingsChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadControllerSettingsCommand());
		addCleanUpCommand(c);
		return c;
	}
	public static Chain getUpdateSupportEmailChain() {
		Chain c = new TransactionChain();
		c.addCommand(new UpdateSupportEmailCommand());
		//addCleanUpCommand(c);
		System.out.println("........ c"+ c);
		return c;
	}
	
	public static Chain getUpdateNotificationSettingChain(){
		Chain c = new TransactionChain();
		c.addCommand(new UpdateNotificationSettings());
		System.out.println("....Notification");

		return c;
	}
	public static Chain getDeleteSupportEmailChain() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteSupportEmailCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddWorkorderTemplateChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddPMWorkorderTemplateCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddPreventiveMaintenanceChain() {
		Chain c = new TransactionChain();
		c.addCommand(new ValidatePMTriggersCommand());
		c.addCommand(new AddPMWorkorderTemplateCommand());
		c.addCommand(new AddPreventiveMaintenanceCommand());
		c.addCommand(new AddPMReminderCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getExecutePreventiveMaintenanceChain() {
		Chain c = new TransactionChain();
		c.addCommand(new ExecutePMCommand());
		c.addCommand(new ResetTriggersCommand());
		c.addCommand(new SchedulePMRemindersCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getExecutePMsChain() {
		Chain c = new TransactionChain();
		c.addCommand(new ExecutePMsCommand());
		c.addCommand(new ResetTriggersCommand());
		c.addCommand(new SchedulePMRemindersCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdatePreventiveMaintenanceChain() {
		Chain c = new TransactionChain();
		c.addCommand(new ValidatePMTriggersCommand());
		c.addCommand(new DeletePreventiveMaintenanceCommand());
		c.addCommand(new AddPMWorkorderTemplateCommand());
		c.addCommand(new AddPreventiveMaintenanceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdatePreventiveMaintenanceJobChain() {
		Chain c = new TransactionChain();
		c.addCommand(new UpdatePreventiveMaintenanceJobCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getPreventiveMaintenanceSummaryChain() {
		Chain c = new ChainBase();
		c.addCommand(new PreventiveMaintenanceSummaryCommand());
		c.addCommand(new GetPMWorkOrders());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeletePreventiveMaintenanceChain() {
		Chain c = new TransactionChain();
		c.addCommand(new DeletePreventiveMaintenanceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getChangePreventiveMaintenanceStatusChain() {
		Chain c = new TransactionChain();
		c.addCommand(new ChangePreventiveMaintenanceStatusCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetUpcomingPreventiveMaintenanceListChain() {
		Chain c = new ChainBase();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetUpcomingPreventiveMaintenanceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetPreventiveMaintenanceListChain() {
		Chain c = new ChainBase();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetPreventiveMaintenanceCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getExecutePMReminderChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetPMAndPMReminderCommand());
		c.addCommand(new GetWOForPMReminderCommand());
		c.addCommand(new ExecutePMReminderCommand());
		return c;
	}
	
	public static Chain getAllWOTemplatesChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetAllWOTemplatesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTemplateChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddTemplateCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddActionChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddTemplateCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddWorkflowRuleChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new AddTemplateCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddReadingRuleChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddWorkflowRuleCommand());
		c.addCommand(new AddActionsForWorkflowRule());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addAssignmentRuleChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddWorkflowRuleCommand());
		c.addCommand(new AddActionsForWorkflowRule());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addSLARuleChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddWorkflowRuleCommand());
		c.addCommand(new AddActionsForWorkflowRule());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteWorkflowRuleChain() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteWorkflowRuleCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getReadingRulesOfFieldsChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetReadingRulesFromFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain runThroughFilters() {
		Chain c = new ChainBase();
		c.addCommand(new ExecuteAllWorkflowsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAlarmEMailNotifierChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddOrUpdateAlarmEMailTemplateCommand());
		c.addCommand(new UpdateAlarmCreationActionCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAlarmCreationRulesChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadAlarmCreationRulesCommand());
		return c;
	}
	
	public static Chain getAddAlarmSMSNotifierChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddOrUpdateAlarmSMSTemplateCommand());
		c.addCommand(new UpdateAlarmCreationActionCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewListChain() {
		Chain c = new TransactionChain();
		c.addCommand(new GetViewListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddViewChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new AddCVCommand());
		c.addCommand(new CustomizeViewColumnCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateFilterFromCriteriaCommand());		
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewsCustomizeChain() {
		Chain c = new ChainBase();
		c.addCommand(new CustomizeViewCommand());
		c.addCommand(new GetViewListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewCustomizeColumnChain() {
		Chain c = new ChainBase();
		c.addCommand(new CustomizeViewColumnCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddModuleChain() {
		Chain c = new TransactionChain();
		c.addCommand(new CreateCustomModuleCommand());
		c.addCommand(new AddModuleCommand());
		c.addCommand(new SetColumnNameForNewCFsCommand());
		c.addCommand(new AddFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddFieldsChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new SetColumnNameForNewCFsCommand());
		c.addCommand(new AddFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateFieldChain() {
		Chain c = new TransactionChain();
		c.addCommand(new UpdateFieldCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getdeleteFieldsChain() {
		Chain c = new TransactionChain();
		c.addCommand(new DeleteFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetFieldsChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllSpaceCategoriesChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		addCleanUpCommand(c);
		return c;	
//		Chain c = new ChainBase();
//		c.addCommand(new GetSpaceCategoriesCommand());
//		addCleanUpCommand(c);
//		return c;
	}
	
	public static Chain addSpaceCategoryChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		addCleanUpCommand(c);
		return c;
		
//		c.addCommand(new AddSpaceCategoryCommand());
//		addCleanUpCommand(c);
//		return c;
	}
	
	public static Chain updateSpaceCategoryChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		addCleanUpCommand(c);
		return c;
//		
//		c.addCommand(new EditSpaceCategoryCommand());
//		addCleanUpCommand(c);
//		return c;		
	}
	
	public static Chain deleteSpaceCategoryChain() {
		Chain c = new TransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		addCleanUpCommand(c);
		return c;
		
//		c.addCommand(new DeleteSpaceCategoryCommand());
//		addCleanUpCommand(c);
//		return c;		
	}
	
	public static Chain getAddReadingChain() {
		Chain c = new TransactionChain();
		c.addCommand(new CreateReadingModuleCommand());
		c.addCommand(new AddModuleCommand());
		c.addCommand(new SetColumnNameForNewCFsCommand());
		c.addCommand(new AddFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddCategoryReadingChain() {
		Chain c = new TransactionChain();
		c.addCommand(getAddReadingChain());
		c.addCommand(new AddCategoryReadingRelCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getCategoryReadingsChain() {
		Chain c = new TransactionChain();
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddOrUpdateReadingValuesChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddOrUpdateReadingValuesCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.READING_RULE, RuleType.PM_READING_RULE));
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetLatestReadingValuesChain() {
		Chain c = new TransactionChain();
		c.addCommand(getCategoryReadingsChain());
		c.addCommand(new GetLatestCategoryReadingValuesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetReadingValuesChain() {
		Chain c = new TransactionChain();
		c.addCommand(new GetLatestCategoryReadingValuesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddPhotosChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddPhotosCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUploadPhotosChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UploadPhotosCommand());
		c.addCommand(new AddPhotosCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateDefaultSpacePhotoChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateDefaultSpacePhotoCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getPhotosChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetPhotosCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddNoteChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddNoteCommand());
		c.addCommand(new ExecuteNoteWorkflowCommand());
		c.addCommand(new AddNoteTicketActivityCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNotesChain() {
		Chain c = new TransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetNotesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddDashboardChain() {
		Chain c = new TransactionChain();
		c.addCommand(new AddDashboardCommand());
		addCleanUpCommand(c);
		return c;
	}
	
//	public static Chain getReportData() {
//		Chain c = new TransactionChain();
//		c.addCommand(new SetFieldsCommand());
//		c.addCommand(new SetCriteriaCommand());
//		c.addCommand(new SetUserFilterCommand());
//		c.addCommand(new SetGroupByCommand());
//		c.addCommand(new SetLimitAndOrderCommand());
//		c.addCommand(new FetchData());
//		c.addCommand(new ProcessResult());
//		addCleanUpCommand(c);
//		return c;
//	}
	
	public static Chain getRelatedAlarmForReports() {
		Chain c = new TransactionChain();
		c.addCommand(new GetRelatedAlarmForReports());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddWidgetChain() {
		Chain c = new TransactionChain();
		c.addCommand(new AddWidgetCommand());
		c.addCommand(new AddDashboardVsWidgetCommand());
		addCleanUpCommand(c);
		return c;
	}
	public static Chain getAddDashboardVsWidgetChain() {
		Chain c = new TransactionChain();
		c.addCommand(new AddDashboardVsWidgetCommand());
		addCleanUpCommand(c);
		return c;
	}
	
}

class TransactionChain extends ChainBase
{
	private  final static boolean  ENABLE_JTA = false;
	public boolean execute(Context context)
            throws Exception
            {
		boolean istransaction = false;
		try {
			if (ENABLE_JTA) {
				TransactionManager tm = FacilioTransactionManager.INSTANCE.getTransactionManager();
				Transaction currenttrans = tm.getTransaction();
				if (currenttrans == null) {
					tm.begin();
					istransaction = true;
				} else {
					//LOGGER.info("joining parent transaction for " + method.getName());
				} 
			}
				boolean status = super.execute(context);
				if (ENABLE_JTA) {
					if (istransaction) {
					//	LOGGER.info("commit transaction for " + method.getName());
						FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
					} 
				}
				
return status;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (ENABLE_JTA) {
				if (istransaction) {
					FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
					//LOGGER.info("rollback transaction for " + method.getName());
				} 
			}
			throw e;
		}
            }
}