package com.facilio.bmsconsole.commands;

import java.util.Map;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.leed.commands.AddConsumptionForLeed;
import com.facilio.leed.commands.AddEnergyMeterCommand;
import com.facilio.leed.commands.FetchArcAssetsCommand;
import com.facilio.leed.commands.LeedBuildingDetailsCommand;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.transaction.FacilioTransactionManager;

public class FacilioChainFactory {
    private static Logger log = LogManager.getLogger(FacilioChainFactory.class.getName());

    protected static Chain getTransactionChain()
    {
    	    return new FacilioChain(true);
    }
    
    protected static Chain getNonTransactionChain()
    {
    	    return new FacilioChain(false);
    }
    
    public static Chain getOrgSignupChain() {
		Chain c = getTransactionChain();
		c.addCommand(new CreateOrgCommand());
		c.addCommand(new AddDefaultModulesCommand());
		c.addCommand(new AddDefaultReportCommand());
		c.addCommand(new AddDefaultUnitsCommand());
		c.addCommand(new AddEventModuleCommand());
		c.addCommand(new AddOrgInfoCommand());
		c.addCommand(new CreateSuperAdminCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addDefaultReportChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddDefaultReportCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}

	public static Chain getAddEnergyDataChain() {
		Chain c = getTransactionChain();
		//c.addCommand(SetTableNamesCommand.getForEner);
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}

	public static Chain getAssetActionChain() {
		Chain c = getTransactionChain();
		c.addCommand(new PerformAssetAction());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
		
	}
	public static Chain getPickListChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadMainFieldCommand());
		c.addCommand(new GetPickListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateTicketChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateTicketCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain calculateTenantBill() {
		
		Chain c = new ChainBase();
		c.addCommand(new CalculateUtilityService());
		c.addCommand(new CalculateFormulaService());
		c.addCommand(new CalculateFinalResult());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getAssignTicketChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new AssignTicketCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketActivitiesChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetTicketActivitesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketStatusListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicketStatus());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketStatusListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketPriorityListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketPriorityListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTicketCategoryListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketCategoryListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddUserCommand() {
		Chain c = new ChainBase();
	    // c.addCommand(new ValidateLicenseCommand());
		c.addCommand(new ValidateWorkOrderFieldsCommand());
		c.addCommand(new AddUserCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getChangeUserStatusCommand() {
		Chain c = new ChainBase();
		c.addCommand(new ChangeUserStatusCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getChangeTeamStatusCommand(){
		Chain c =new ChainBase();
		c.addCommand(new ChangeTeamStatusCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getUpdateUserCommand() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateUserCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getDeleteUserCommand() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteUserCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getAddUserMobileSettingCommand() {
		Chain c = new ChainBase();
		c.addCommand(new AddUserMobileSettingCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Command getDeleteUserMobileSettingCommand() {
		Chain c = new ChainBase();
		c.addCommand(new RemoveUserMobileSettingCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getGetTasksOfTicketCommand() {
		Chain c = new ChainBase();
		c.addCommand(new GetTasksOfTicketCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getAddGroupCommand() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateWorkOrderFieldsCommand());
		c.addCommand(new AddGroupCommand());
		c.addCommand(new AddGroupMembersCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getAddRoleCommand() {
		Chain c = new ChainBase();
		c.addCommand(new AddRoleCommand());
		c.addCommand(new AddPermissionsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;		
	}

	
	public static Command getUpdateGroupCommand() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateGroupCommand());
		c.addCommand(new AddGroupMembersCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getUpdateRoleCommand() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateRoleCommand());
		c.addCommand(new AddPermissionsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command getDeleteGroupCommand() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteGroupCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Command getDeleteRoleCommand() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteRoleCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}

	public static Chain getNewWorkOrderChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
//	public static Chain getAddWorkOrderWithTicketChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(getAddTicketChain());
//        c.addCommand(getAddWorkOrderChain());
//        //c.addCommand(new AddTicketActivityCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
	
	public static Chain getDeleteWorkOrderChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new SplitDependentTicketsCommand());
		c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE, RuleType.SCHEDULED_RULE));
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkOrderDataChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewWorkOrderRequestChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddWorkOrderRequestChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GetFormMetaCommand());
		c.addCommand(new ValidateFormCommand());
//		c.addCommand(getAddTicketChain());
		c.addCommand(new AddRequesterCommand());
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddWorkOrderRequestCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new AddTicketActivityCommand());
		c.addCommand(SetTableNamesCommand.getForTicketAttachment());
		c.addCommand(getAddAttachmentChain());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain sendReadingReportMailChain() {
		Chain c = new ChainBase();
		c.addCommand(getExportReadingReportFileChain());
		c.addCommand(new SendReadingReportMailCommand());
		return c;
	}
	
	public static Chain sendReportMailChain() {
		Chain c = new ChainBase();
		c.addCommand(getExportReportFileChain());
		c.addCommand(new SendReadingReportMailCommand());
		return c;
	}
	
	public static Chain getExportReadingReportFileChain() {
		Chain c = new ChainBase();
		c.addCommand(ReadOnlyChainFactory.fetchReadingReportChain());
		c.addCommand(new GetExportReportDataCommand());
		return c;
	}
	
	public static Chain getExportReportFileChain() {
		Chain c = new ChainBase();
		c.addCommand(ReadOnlyChainFactory.fetchReportDataChain());
		c.addCommand(new GetExportReportDataCommand());
		return c;
	}
	
	public static Chain getWorkOrderRequestDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderRequestCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkOrderRequestListChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetWorkOrderRequestListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteWorkOrderRequestChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new SplitDependentTicketsCommand());
		c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateWorkOrderRequestChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateWorkOrderRequestCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new AddWOFromRequestCommand());
		//CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteAlarmChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new RemoveAlarmFromEventCommand());
		c.addCommand(new SplitDependentTicketsCommand());
		c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAlarmTemplateChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddAlarmTemplateCommand());
		CommonCommandUtil.addCleanUpCommand(c);
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
		c.addCommand(new GetAlarmListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAlarmDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAlarmCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateAlarmResourceChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateAlarmAssetCommand());
		c.addCommand(new AddWOFromAlarmCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getReadingAlarmsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForReadingAlarm());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateSortingQueryCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new getRelatedEvents());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewTaskChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTaskChain() {
		Chain c = getTransactionChain();
		//c.addCommand(getAddTicketChain());
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new ValidateTasksCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddTaskCommand());
		c.addCommand(new AddTaskOptionsCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddTaskTicketActivityCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateTaskChain() {
		Chain c = getTransactionChain();
		c.addCommand(new ValidateAndCreateValuesForInputTaskCommand());
		c.addCommand(getAddOrUpdateReadingValuesChain());
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateTaskCommand());
		c.addCommand(new UpdateClosedTasksCounterCommand());
		c.addCommand(new AddTaskTicketActivityCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
//		c.addCommand(getAddOrUpdateReadingValuesChain());
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getRelatedTasksChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new GetRelatedTasksCommand());
		c.addCommand(new GetTaskInputDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getRelatedMultipleTasksChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new GetMultipleRelatedTasksCommand());
		c.addCommand(new GetTaskInputDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addTaskSectionChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddTaskSectionCommand());
		c.addCommand(new UpdateTaskWithSectionCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getPortalInfoChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadPortalInfoCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updatePortalInfoChain() {
		Chain c = new ChainBase();
		c.addCommand(new HandlePortalInfoCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain updatePortalSSOChain() {
		Chain c = new ChainBase();
		c.addCommand(new HandlePortalSSOCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addReadingMetaDataEntry() {
		Chain c = new ChainBase();
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
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
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateLocationChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain deleteLocationChain() {
		
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getLocationChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetLocationCommand());
		CommonCommandUtil.addCleanUpCommand(c);
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
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new AddSkillCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Command updateSkillCommand() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new UpdateSkillCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	
	
	public static Command deleteSkillCommand(){
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		//c.addCommand(SetTableNamesCommand.getForSkill());
		//c.addCommand(new DeleteSkillCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
//	public static Chain getSkillChain() {
//		Chain c = new ChainBase();
//		c.addCommand(SetTableNamesCommand.getForSkill());
//		c.addCommand(new LoadModuleNameCommand());
//		c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new GetSkillCommand());
//		
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
	
//	public static Chain getNewSkillChain() {
//		Chain c = new ChainBase();
//		c.addCommand(SetTableNamesCommand.getForSkill());
//		c.addCommand(new LoadModuleNameCommand());
//		c.addCommand(new LoadAllFieldsCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
	
	
	
	public static Chain getAllCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllCampusCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateCampusFieldsCommand());
		c.addCommand(new AddLocationCommand());
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddCampusCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateCampusChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateCampusFieldsCommand());
		c.addCommand(updateLocationChain());
		c.addCommand(new UpdateBaseSpaceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain deleteSpaceChain () {
		Chain c = new ChainBase();
		c.addCommand(new GenericDeleteForSpaces());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getCampusDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetCampusCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getSiteReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new GetSiteReportCards());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllBuildingCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getBuildingReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new GetBuildingReportCards());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateBuildingFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddBuildingCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateBuildingChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateBuildingCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	
	public static Chain getBuildingDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetBuildingCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain AddEnergyMeterChain(){
		Chain c = new ChainBase();
		c.addCommand(new AddEnergyMeterCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
/*	public static Chain getBuildingUtilityProviderDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetBuildingCommand());
		c.addCommand(new GetUtilityProvidersCommand());
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewFloorChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddFloorChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateFloorFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddFloorCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getFloorDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetFloorCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getFloorReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new GetFloorReportCards());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getAllSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetAllSpaceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getIndependentSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetIndependentSpaceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(new ValidateSpaceFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddSpaceCommand());
		c.addCommand(getCategoryReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getSpaceDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetSpaceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getSpaceReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new GetSpaceReportCards());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllZoneChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllZoneCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllZoneChildrenChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllZoneChildrenCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewZoneChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddZoneChain() {
		Chain c = getTransactionChain();
		c.addCommand(new ValidateZoneFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddZoneCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateZoneChain() {
		Chain c = getTransactionChain();
		c.addCommand(new ValidateZoneFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateZoneCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getZoneDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetZoneCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNewAssetChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkflowRulesChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetWorkflowRulesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkflowRuleOfTypeChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetWorkFlowOfRuleTypeCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateWorkflowRuleAction() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateWorkFlowRuleAction());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTemplateOfWorkflowRuleAction() {
		Chain c = getTransactionChain();
		c.addCommand(new CreateActionTemplateForWorkflowCommand());
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new UpdateTemplateInAction());
		c.addCommand(new DeleteActionTemplateCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteAssetChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new DeleteResourceCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAssetListChain() {
		Chain c = new ChainBase();
//		c.addCommand(SetTableNamesCommand.getForAsset());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new SetModuleForSpecialAssetsCommand());
		c.addCommand(new LoadAssetFields());
		c.addCommand(new LoadViewCommand());
//		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetAssetListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAssetDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new SetModuleForSpecialAssetsCommand());
		c.addCommand(new LoadAssetFields());
		c.addCommand(new GetAssetDetailCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAssetReportCardsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new GetAssetReportCards());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAttachmentChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		c.addCommand(new AddAttachmentRelationshipCommand());
		c.addCommand(new AddAttachmentTicketActivityCommand());
		return c;
	}
	
	public static Chain getAddEnergyMeterChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddAdditionalPropsForEnergyMeterCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new AddVirtualMeterRelCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(getCategoryReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateEnergyMeterChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateAdditionalPropsForEnergyMeterCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new AddVirtualMeterRelCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getProcessDataChain() {
		Chain c = new ChainBase();
		c.addCommand(new ProcessDataCommand());
		c.addCommand(new ModeledDataCommand());
		c.addCommand(new UnModeledDataCommand());
		c.addCommand(getAddOrUpdateReadingValuesChain());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getProcessHistoricalDataChain() {
		Chain c = getTransactionChain();
		c.addCommand(new HistoricalReadingsCommand());
		c.addCommand(new BulkModeledReadingCommand());
		c.addCommand(getAddOrUpdateReadingValuesChain());
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddEnergyMeterPurposeChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeterPurpose());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getVirtualMeterChildrenChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetVirtualMeterChildrenCommand());
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getBaseSpaceChildrenChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetBaseSpaceChildrenCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getImportReadingChain() {
		ChainBase c = new ChainBase();
		c.addCommand(new DataParseForReadingsCommand());
		c.addCommand(new InsertReadingCommand());
		c.addCommand(new WriteSkippedToFileCommand());
		c.addCommand(new SendEmailCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain processImportData() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetZoneCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	
	
	public static Chain getEmailSettingChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadSupportEmailsCommand());
		c.addCommand(new LoadEmailSettingCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateEmailSettingChain() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateEmailSettingCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	
	public static Chain getSupportEmailChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetSupportEmailCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddSupportEmailChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddSupportEmailCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddControllerChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddControllerCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getControllerSettingsChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadControllerSettingsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getUpdateSupportEmailChain() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateSupportEmailCommand());
		//CommonCommandUtil.addCleanUpCommand(c);
		System.out.println("........ c"+ c);
		return c;
	}
	
	public static Chain getUpdateNotificationSettingChain(){
		Chain c = getTransactionChain();
		c.addCommand(new UpdateNotificationSettings());
		System.out.println("....Notification");

		return c;
	}
	public static Chain getDeleteSupportEmailChain() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteSupportEmailCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getTemplateChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetTemplateCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain deleteTemplateChain() {
		Chain c = getTransactionChain();
		c.addCommand(new DeleteTemplateCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddWorkorderTemplateChain() {
		Chain c = getTransactionChain();
		c.addCommand(new CreateWorkorderTemplateCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateWorkorderTemplateChain() {
		Chain c = getTransactionChain();
		c.addCommand(new DeleteTemplateCommand());
		c.addCommand(new CreateWorkorderTemplateCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addTaskGroupTemplateChain() {
		Chain c = getTransactionChain();
		c.addCommand(new CreateTaskGroupTemplateCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateTaskGroupTemplateChain() {
		Chain c = getTransactionChain();
		c.addCommand(new DeleteTemplateCommand());
		c.addCommand(new CreateTaskGroupTemplateCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddPreventiveMaintenanceChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		c.addCommand(new ValidatePMTriggersCommand());
		c.addCommand(new ValidateTasksCommand());
		c.addCommand(new AddPMReadingsForTasks());
		c.addCommand(new CreateWorkorderTemplateCommand());	  // template addition
		c.addCommand(new AddPreventiveMaintenanceCommand());  // PM addition
		c.addCommand(new AddResourceReadingRelCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		c.addCommand(new AddAndSchedulePMTriggerCommand());
		c.addCommand(new AddPMReminderCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddValidationRulesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getExecutePreventiveMaintenanceChain() {
		return getExecutePreventiveMaintenanceChain(false);
	}
	public static Chain getExecutePreventiveMaintenanceChain(boolean isMultipleWo) {
		Chain c = getTransactionChain();
		if(isMultipleWo) {
			c.addCommand(new PreparePMForMultipleAsset());
		}
		else {
			c.addCommand(new ExecutePMCommand());
		}
		c.addCommand(new ResetTriggersCommand());
		c.addCommand(new SchedulePMRemindersCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getExecutePMsChain() {
		Chain c = getTransactionChain();
		c.addCommand(new ExecutePMsCommand());
		c.addCommand(new ResetTriggersCommand());
		c.addCommand(new SchedulePMRemindersCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdatePreventiveMaintenanceChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		
		c.addCommand(new GetPreventiveMaintenanceCommand());
		c.addCommand(new ValidatePMTriggersCommand());
		c.addCommand(new ValidateTasksCommand());
		c.addCommand(new AddPMReadingsForTasks());
		c.addCommand(new CreateWorkorderTemplateCommand());
		c.addCommand(new UpdatePreventiveMaintenanceCommand());
		c.addCommand(new AddResourceReadingRelCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		c.addCommand(new DeletePMAndDependenciesCommand(false));
		c.addCommand(new AddAndSchedulePMTriggerCommand());
//		c.addCommand(new DeletePMRemindersCommand());
		c.addCommand(new AddPMReminderCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddValidationRulesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdatePreventiveMaintenanceJobChain() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdatePreventiveMaintenanceJobCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getPreventiveMaintenanceSummaryChain() {
		Chain c = new ChainBase();
		c.addCommand(new PreventiveMaintenanceSummaryCommand());
		c.addCommand(new GetPMWorkOrders());
		c.addCommand(new GetTaskInputDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeletePreventiveMaintenanceChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GetPreventiveMaintenanceCommand());
		c.addCommand(new DeletePMAndDependenciesCommand(true));
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getChangePreventiveMaintenanceStatusChain() {
		Chain c = getTransactionChain();
		c.addCommand(new ChangePreventiveMaintenanceStatusCommand());
		c.addCommand(new DeletePMAndDependenciesCommand(false, true));
		c.addCommand(new AddAndSchedulePMTriggerCommand(true));
		c.addCommand(new AddPMReminderCommand(true));
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetUpcomingPreventiveMaintenanceListChain() {
		Chain c = new ChainBase();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetUpcomingPreventiveMaintenanceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetPMJobListChain() {
		Chain c = new ChainBase();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetPMJobsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetPreventiveMaintenanceListChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetPreventiveMaintenanceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getExecutePMReminderChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetPMAndPMReminderCommand());
		c.addCommand(new GetWOForPMReminderCommand());
		c.addCommand(new ExecutePMReminderCommand());
		return c;
	}
	
	public static Chain getTemplatesOfTypeChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetTemplatesOfTypeCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTemplateChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddTemplateCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteWorkflowRuleChain() {
		Chain c = new ChainBase();
		c.addCommand(new DeleteWorkflowRuleCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getReadingRulesOfFieldsChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetReadingRulesFromFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain runThroughFilters() {
		Chain c = new ChainBase();
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAlarmEMailNotifierChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddOrUpdateAlarmEMailTemplateCommand());
		c.addCommand(new UpdateAlarmCreationActionCommand());
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewListChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetViewListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewListsChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetViewListsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	
	}
	
	public static Chain getAddViewChain() {
		Chain c = getTransactionChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new AddCVCommand());
		c.addCommand(new CustomizeViewColumnCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateFilterFromCriteriaCommand());		
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewsCustomizeChain() {
		Chain c = getTransactionChain();
		c.addCommand(new CustomizeViewCommand());
		c.addCommand(new GetViewListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewCustomizeColumnChain() {
		Chain c = new ChainBase();
		c.addCommand(new CustomizeViewColumnCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getViewCustomizeSortColumnsChain() {
		Chain c = getTransactionChain();
		c.addCommand(new SaveSortFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddModuleChain() {
		Chain c = getTransactionChain();
		c.addCommand(new CreateCustomModuleCommand());
		c.addCommand(new AddModuleCommand());
		c.addCommand(new SetColumnNameForNewCFsCommand());
		c.addCommand(new AddFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddFieldsChain() {
		Chain c = getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new SetColumnNameForNewCFsCommand());
		c.addCommand(new AddFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateFieldChain() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateFieldCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getdeleteFieldsChain() {
		Chain c = getTransactionChain();
		c.addCommand(new DeleteFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetFieldsChain() {
		Chain c = new ChainBase();
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllSpaceCategoriesChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;	
//		Chain c = new ChainBase();
//		c.addCommand(new GetSpaceCategoriesCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
	}
	
/*	public static Chain getAllBuildingsChain() {
		Chain c = new ChainBase() ;
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;	
	}*/
	
	public static Chain addSpaceCategoryChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
		
//		c.addCommand(new AddSpaceCategoryCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
	}
	
	public static Chain updateSpaceCategoryChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
//		
//		c.addCommand(new EditSpaceCategoryCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;		
	}
	
	public static Chain deleteSpaceCategoryChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
		
//		c.addCommand(new DeleteSpaceCategoryCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;		
	}
	
	public static Chain getAddReadingChain() {
		Chain c = getTransactionChain();
		c.addCommand(new CreateReadingModuleCommand());
		c.addCommand(new AddModuleCommand());
		c.addCommand(new SetColumnNameForNewCFsCommand());
		c.addCommand(new AddFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateReadingChain() {
		Chain c = getTransactionChain();
		c.addCommand(new RestrictUneditablePropsInFieldCommand());
		c.addCommand(getUpdateFieldChain());
		c.addCommand(new AddValidationRulesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddCategoryReadingChain() {
		Chain c = getTransactionChain();
		c.addCommand(getAddReadingChain());
		c.addCommand(new AddCategoryReadingRelCommand());
		c.addCommand(new GetCategoryResourcesCommand());
		c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
		//c.addCommand(new SetValidationRulesContextCommand());
		c.addCommand(new AddValidationRulesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addResourceReadingChain() {
		Chain c = getTransactionChain();
		c.addCommand(getAddReadingChain());
		c.addCommand(new AddResourceReadingRelCommand());
		c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAssetReadingsChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetAssetSpecifcReadingsCommand());
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetLatestAssetReadingValuesChain() {
		Chain c = new ChainBase();
		c.addCommand(getAssetReadingsChain());
		c.addCommand(new GetLatestReadingValuesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getResourcesOccupantReadingValuesChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetResourcesLatestReadingValuesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getSpaceReadingsChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetSpaceSpecifcReadingsCommand());
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetLatestSpaceReadingValuesChain() {
		Chain c = new ChainBase();
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new GetLatestReadingValuesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getCategoryReadingsChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllFieldsChain () {
		Chain c = new ChainBase();
		c.addCommand(new GetAllFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllCategoryReadingsChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetAllCategoryReadingsCommand());
		c.addCommand(new GetAllAssetSpecificReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getReadingsForSpaceTypeChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GetAllSpaceTypeReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addResourceRDMChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GetReadingFieldsCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddOrUpdateReadingValuesChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddOrUpdateReadingsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain onlyAddOrUpdateReadingsChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GetReadingDataMetaCommand());
		c.addCommand(new ReadingUnitConversionCommand());
		c.addCommand(new DeltaCalculationCommand());
		c.addCommand(new CalculateDeltaCommand());
		c.addCommand(new CalculatePreFormulaCommand());
		c.addCommand(new ExecuteValidationRule());
		c.addCommand(new AddOrUpdateReadingValuesCommand());
		c.addCommand(new AddMarkedReadingValuesCommand());
		c.addCommand(new SpaceBudIntegrationCommand());	//For RMZ-SpaceBud Integration
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain executeWorkflowsForReadingChain() {
		Chain c = new ChainBase();
		c.addCommand(new ExecuteAllWorkflowsCommand(false, RuleType.READING_RULE, RuleType.PM_READING_RULE, RuleType.VALIDATION_RULE));
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain calculateFormulaChain() {
		Chain c = new ChainBase();
		c.addCommand(new CalculatePostFormulaCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetLatestReadingValuesChain() {
		Chain c = new ChainBase();
		c.addCommand(getCategoryReadingsChain());
		c.addCommand(new GetLatestReadingValuesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getGetReadingValuesChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GetLatestReadingValuesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddPhotosChain() {
		Chain c = getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddPhotosCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUploadPhotosChain() {
		Chain c = getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UploadPhotosCommand());
		c.addCommand(new AddPhotosCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain justUploadPhotosChain() {
		Chain c = getTransactionChain();
		c.addCommand(new UploadPhotosCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getReadingFromImageChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetTextsFromImageCommand());
		c.addCommand(new FilterReadingsFromImageCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateDefaultSpacePhotoChain() {
		Chain c = getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateDefaultSpacePhotoCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getPhotosChain() {
		Chain c = getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetPhotosCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getNotesChain() {
		Chain c = getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetNotesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddDashboardChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddDashboardCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateDashboardChain() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateDashboardCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
//	public static Chain getReportData() {
//		Chain c = getDefaultChain();
//		c.addCommand(new SetFieldsCommand());
//		c.addCommand(new SetCriteriaCommand());
//		c.addCommand(new SetUserFilterCommand());
//		c.addCommand(new SetGroupByCommand());
//		c.addCommand(new SetLimitAndOrderCommand());
//		c.addCommand(new FetchData());
//		c.addCommand(new ProcessResult());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
	
	public static Chain getAddWidgetChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddWidgetCommand());
		c.addCommand(new AddDashboardVsWidgetCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getAddDashboardVsWidgetChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddDashboardVsWidgetCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addBaseLineChain() {
		Chain c = getTransactionChain();
		c.addCommand(new ValidateBaseLineCommand());
		c.addCommand(new AddBaseLineCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getBaseLinesOfSpaceChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetBaseLinesForSpaceCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllBaseLinesChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetAllBaseLinesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addReportBaseLinesChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddReportBaseLinesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateReportBaseLinesChain() {
		Chain c = getTransactionChain();
		c.addCommand(new DeleteExistingReportBaseLineCommand());
		c.addCommand(new AddReportBaseLinesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addFormulaFieldChain() {
		Chain c = getTransactionChain();
		c.addCommand(new CreateFormulaFieldDependenciesCommand());
		c.addCommand(getAddReadingChain());
		c.addCommand(new AddResourceReadingRelCommand());
		c.addCommand(new AddCategoryReadingRelCommand());
		c.addCommand(new GetCategoryResourcesCommand());
		c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
		c.addCommand(new AddFormulaFieldCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAllFormulasOfTypeChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetAllFormulasOfTypeCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateFormulaChain() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateFormulaCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain deleteFormulaChain() {
		Chain c = getTransactionChain();
		c.addCommand(new DeleteFormulaCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addTabularReportChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddComparisonReportsCommand());
		c.addCommand(new AddReportColumnsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateReportColumnSequence() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateReportColumnSequence());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddTicketCategoryChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateTicketCategoryChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getDeleteTicketCategoryChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getAddTicketPriorityChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateTicketPriorityChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteTicketPriorityChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getAddTicketTypeChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	public static Chain getUpdateTicketTypeChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteTicketTypeChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAssetCategoryChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateAssetCategoryChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		
		return c;
	}
	
	public static Chain getDeleteAssetCategoryChain() {		
		Chain c = getTransactionChain();
		c.addCommand(new ValidateAssetCategoryDeletion());
		c.addCommand(SetTableNamesCommand.getForAssetCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAssetDepartmentChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetDepartment());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateAssetDepartmentChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetDepartment());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteAssetDepartmentChain() {		
		Chain c = getTransactionChain();
		c.addCommand(new ValidateAssetDepartmentDeletion());
		c.addCommand(SetTableNamesCommand.getForAssetDepartment());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAssetTypeChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateAssetTypeChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteAssetTypeChain() {
		Chain c = getTransactionChain();
		c.addCommand(new ValidateAssetTypeDeletion());
		c.addCommand(SetTableNamesCommand.getForAssetType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddAlarmSeverityChain() {
		Chain c = getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarmSeverity());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addDerivationFormulaChain() {
		Chain c = getTransactionChain();
		c.addCommand(addFormulaFieldChain());
		c.addCommand(new UpdateDerivationCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addModuleDataChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateModuleDataChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain deleteModuleDataChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain calculateBenchmarkValueChain() {
		Chain c = new ChainBase();
		c.addCommand(new CalculateBenchmarkValueCommand());
		return c;
	}
	
	public static Chain getAllShiftsCommand() {
		Chain c = new ChainBase();
		c.addCommand(new GetAllShiftsCommand());
		return c;
	}
	
	public static Chain getAddShiftCommand() {
		Chain c = getTransactionChain();
		c.addCommand(new AddShiftCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUpdateShiftCommand() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateShiftCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getDeleteShiftCommand() {
		Chain c = getTransactionChain();
		c.addCommand(new DeleteShiftCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain calculateCostChain() {
		Chain c = getTransactionChain();
		c.addCommand(new FetchCostDataCommand());
		c.addCommand(new CalculateCostCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addTenantChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddTenantCommand());
		c.addCommand(new AddTenantUserCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addTenantUserChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddTenantUserCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateTenantChain() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateTenantCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addRateCardChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddRateCardCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateRateCardChain() {
		Chain c = getTransactionChain();
		c.addCommand(new UpdateRateCardCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain addOfflineSyncErrorChain() {
		Chain c = getTransactionChain();
		c.addCommand(new AddOfflineSyncErrorCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getFormMetaChain() {
		Chain c = getNonTransactionChain();
		c.addCommand(new GetFormMetaCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getFormFieldsChain() {
		Chain c = getNonTransactionChain();
		c.addCommand(new GetFormFieldsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain editFormChain() {
		Chain c = getTransactionChain();
		c.addCommand(new GetFormMetaCommand());
		c.addCommand(new EditFormCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	private static  long createOrg(Organization org) throws Exception {
		
		Organization existingOrg = getOrg(org.getDomain());
		if (existingOrg != null) {
			throw new AccountException(AccountException.ErrorCode.ORG_DOMAIN_ALREADY_EXISTS, "The given domain already registered.");
		}
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getOrgModule().getTableName())
				.fields(AccountConstants.getOrgFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(org);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		long orgId = (Long) props.get("id");
		
		AccountUtil.getRoleBean().createSuperdminRoles(orgId);
		
		return orgId;
	}
	private static Organization getOrg(String domain) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Chain getTestTransactionChain()
	{
		Chain c = getTransactionChain();
	
		Command command = new Command() {

			@Override
			public boolean execute(Context arg0) throws Exception {
				String prefix = "x";
				// TODO Auto-generated method stub
				Organization org = new Organization();
				//	String prefix = "abi";
					org.setDomain(prefix+1);
					createOrg(org);
					org = new Organization();
					org.setDomain(prefix+2);
					createOrg(org);
					org = new Organization();
					org.setDomain(prefix+3);
					createOrg(org);
					org = new Organization();
					org.setDomain(prefix+4);
					createOrg(org);
					if(false)
					{
					throw new Exception();
					}
					org = new Organization();
					org.setDomain(prefix+5);
					createOrg(org);
					org = new Organization();
					org.setDomain(prefix+6);
					createOrg(org);
				return false;
			}
			
			
		};
		c.addCommand(command);
		
		return c;
	}
	
	public static class FacilioChain extends ChainBase {
		
		private boolean enableTransaction = false;
		public FacilioChain(boolean isTransactionChain) {
			// TODO Auto-generated constructor stub
			this.enableTransaction = isTransactionChain && Boolean.valueOf(AwsUtil.getConfig("enable.transaction"));
		}
		
	    private static Logger log = LogManager.getLogger(FacilioChain.class.getName());

		public boolean execute(Context context) throws Exception {
			boolean istransaction = false;
			try {
				if (enableTransaction) {
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
				if (enableTransaction) {
					if (istransaction) {
					//	LOGGER.info("commit transaction for " + method.getName());
						FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
					}
				}
					
				return status;
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
				if (enableTransaction) {
					if (istransaction) {
						FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
						//LOGGER.info("rollback transaction for " + method.getName());
					} 
				}
				throw e;
			}
		}
	}
}