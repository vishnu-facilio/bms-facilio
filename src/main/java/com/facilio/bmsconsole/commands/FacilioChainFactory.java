package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.leed.commands.AddConsumptionForLeed;
import com.facilio.leed.commands.AddEnergyMeterCommand;
import com.facilio.leed.commands.FetchArcAssetsCommand;
import com.facilio.leed.commands.LeedBuildingDetailsCommand;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class FacilioChainFactory {
    private static Logger LOGGER = LogManager.getLogger(FacilioChainFactory.class.getName());

	public static Chain addDefaultReportChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddDefaultReportCommand());
		return c;
	}

	public static Chain getAddEnergyDataChain() {
		Chain c = FacilioChain.getTransactionChain();
		//c.addCommand(SetTableNamesCommand.getForEner);
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}

	public static Chain getAssetActionChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new PerformAssetAction());
		return c;
		
	}
	public static Chain getPickListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadMainFieldCommand());
		c.addCommand(new GetPickListCommand());
		return c;
	}
	
	public static Chain getUpdateTicketChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateTicketCommand());
		return c;
	}
	
	public static Chain getTicketDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketCommand());
		return c;
	}
	
	public static Chain getTicketListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketListCommand());
		return c;
	}
	public static Chain calculateTenantBill() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new CalculateUtilityService());
		c.addCommand(new CalculateFormulaService());
		c.addCommand(new CalculateFinalResult());
		return c;
	}
	public static Chain getAssignTicketChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new AssignTicketCommand());
		return c;
	}
	
	public static Chain getTicketActivitiesChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTicketActivitesCommand());
		return c;
	}
	
	public static Chain getTicketStatusListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketStatus());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketStatusListCommand());
		return c;
	}
	
	public static Chain getTicketPriorityListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketPriorityListCommand());
		return c;
	}
	
	public static Chain getTicketCategoryListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketCategoryListCommand());
		return c;
	}
	
	public static Chain getAddUserCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddUserCommand());
		return c;
	}
	
	public static Command getChangeUserStatusCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ChangeUserStatusCommand());
		return c;
	}
	
	public static Command getChangeTeamStatusCommand(){
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ChangeTeamStatusCommand());
		return c;
	}
	
	public static Command getUpdateUserCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateUserCommand());
		return c;
	}
	
	public static Command getDeleteUserCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteUserCommand());
		return c;
	}
	
	public static Command getAddUserMobileSettingCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddUserMobileSettingCommand());
		return c;
	}
	public static Command getDeleteUserMobileSettingCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new RemoveUserMobileSettingCommand());
		return c;
	}
	
	public static Command getGetTasksOfTicketCommand() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTasksOfTicketCommand());
		return c;
	}
	
	public static Command getAddGroupCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateWorkOrderFieldsCommand());
		c.addCommand(new AddGroupCommand());
		c.addCommand(new AddGroupMembersCommand());
		return c;
	}
	
	public static Command getAddRoleCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddRoleCommand());
		c.addCommand(new AddPermissionsCommand());
		return c;		
	}

	
	public static Command getUpdateGroupCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateGroupCommand());
		c.addCommand(new AddGroupMembersCommand());
		return c;
	}
	
	public static Command getUpdateRoleCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateRoleCommand());
		c.addCommand(new AddPermissionsCommand());
		return c;
	}
	
	public static Command getDeleteGroupCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteGroupCommand());
		return c;
	}
	public static Command getDeleteRoleCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteRoleCommand());
		return c;
	}

	public static Chain getNewWorkOrderChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		return c;
	}
	
//	public static Chain getAddWorkOrderWithTicketChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(getAddTicketChain());
//        c.addCommand(getAddWorkOrderChain());
//        //c.addCommand(new AddTicketActivityCommand());
//		return c;
//	}
	
	public static Chain getDeleteWorkOrderChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new SplitDependentTicketsCommand());
		c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE));
		return c;
	}
	
	public static Chain getWorkOrderDataChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static Chain getNewWorkOrderRequestChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		return c;
	}
	
	public static Chain getAddWorkOrderRequestChain() {
		Chain c = FacilioChain.getTransactionChain();
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
		return c;
	}
	
	public static Chain getWorkOrderRequestDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderRequestCommand());
		return c;
	}
	
	public static Chain getWorkOrderRequestListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetWorkOrderRequestListCommand());
		return c;
	}
	
	public static Chain getDeleteWorkOrderRequestChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new SplitDependentTicketsCommand());
		c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain getUpdateWorkOrderRequestChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateWorkOrderRequestCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new AddWOFromRequestCommand());
		return c;
	}
	
	public static Chain getDeleteAlarmChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new RemoveAlarmFromEventCommand());
		c.addCommand(new SplitDependentTicketsCommand());
		c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain getAddAlarmTemplateChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddAlarmTemplateCommand());
		return c;
	}
	
	public static Chain getUpdateAlarmResourceChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateAlarmAssetCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain getReadingAlarmsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForReadingAlarm());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateSortingQueryCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new getRelatedEvents());
		return c;
	}
	
	public static Chain getNewTaskChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		return c;
	}
	
	public static Chain getAddTaskChain() {
		FacilioChain c = (FacilioChain) FacilioChain.getTransactionChain();
		//c.addCommand(getAddTicketChain());
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new ValidateTasksCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddTaskCommand());
		c.addCommand(new AddTaskOptionsCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddTaskTicketActivityCommand());
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static Command getDeleteTaskChain() {
		FacilioChain c = (FacilioChain) FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new DeleteTaskCommand());
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
		return c;
	}
	
	public static Chain getTaskDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTaskCommand());
		c.addCommand(new GetTaskInputDataCommand());
		return c;
	}
	
	public static Chain getTaskListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTaskListCommand());
		c.addCommand(new GetTaskInputDataCommand());
		return c;
	}
	
	public static Chain getRelatedTasksChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new GetRelatedTasksCommand());
		c.addCommand(new GetTaskInputDataCommand());
		return c;
	}
	
	public static Chain getRelatedMultipleTasksChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new GetMultipleRelatedTasksCommand());
		c.addCommand(new GetTaskInputDataCommand());
		return c;
	}
	
	public static Chain addTaskSectionChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddTaskSectionCommand());
		c.addCommand(new UpdateTaskWithSectionCommand());
		return c;
	}
	
	public static Chain getPortalInfoChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadPortalInfoCommand());
		return c;
	}
	
	public static Chain updatePortalInfoChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new HandlePortalInfoCommand());
		return c;
	}
	public static Chain updatePortalSSOChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new HandlePortalSSOCommand());
		return c;
	}
	
	public static Chain addReadingMetaDataEntry() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	public static Command getAllLocationsCommand() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain addLocationChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static Chain updateLocationChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static Chain deleteLocationChain() {
		
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static Chain getLocationChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetLocationCommand());
		return c;
	}
	
	public static Command getAllSkillsCommand() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new GetAllSkillsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain addSkillChain()
	{
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new AddSkillCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static Command updateSkillCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new UpdateSkillCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	
	
	public static Command deleteSkillCommand(){
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		//c.addCommand(SetTableNamesCommand.getForSkill());
		//c.addCommand(new DeleteSkillCommand());
		return c;
	}
	
	public static Chain getAllCampusChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllCampusCommand());
		return c;
	}
	
	public static Chain getNewCampusChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static Chain getAddCampusChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateCampusFieldsCommand());
		c.addCommand(new AddLocationCommand());
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddCampusCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	
	public static Chain getUpdateCampusChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateCampusFieldsCommand());
		c.addCommand(updateLocationChain());
		c.addCommand(new UpdateBaseSpaceCommand());
		return c;
	}
	
	public static Chain deleteSpaceChain () {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GenericDeleteForSpaces());
		return c;
	}
	public static Chain deleteTenantChain () {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteTenantCommand());
		c.addCommand(new DeleteTenantZonesCommand());
		return c;
	}
	public static Chain getCampusDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetCampusCommand());
		return c;
	}
	
	public static Chain getSiteReportCardsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new GetSiteReportCards());
		return c;
	}
	
	public static Chain getTenantReportCardsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTenants());
		c.addCommand(new GetTenantReportCards());
		return c;
	}
	
	public static Chain getTenantReadingCardsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTenants());
		c.addCommand(new GetTenantReadingCardsCommand());
		return c;
	}
	
	public static Chain getAllBuildingChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllBuildingCommand());
		return c;
	}
	
	public static Chain getBuildingReportCardsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new GetBuildingReportCards());
		return c;
	}
	
	public static Chain getNewBuildingChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static Chain getAddBuildingChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateBuildingFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddBuildingCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	
	public static Chain getUpdateBuildingChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateBuildingCommand());
		return c;
	}
	
	
	public static Chain getBuildingDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetBuildingCommand());
		return c;
	}
	
	public static Chain AddEnergyMeterChain(){
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddEnergyMeterCommand());
		return c;
	}

	public static Chain LeedDetailsPageChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LeedBuildingDetailsCommand());
		return c;
	}
	
	public static Chain addConsumptionDataChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddConsumptionForLeed());
		//c.addCommand(new AddConsumptionData());
		return c;
	}
	
	public static Chain FetchAssetsFromArcChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new FetchArcAssetsCommand());
		return c;
	}
	
	public static Chain getAllFloorChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllFloorCommand());
		return c;
	}
	
	public static Chain getNewFloorChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static Chain getAddFloorChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateFloorFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddFloorCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	
	public static Chain getFloorDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetFloorCommand());
		return c;
	}
	
	public static Chain getFloorReportCardsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new GetFloorReportCards());
		return c;
	}
	public static Chain getAllSpaceChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetAllSpaceCommand());
		return c;
	}
	
	public static Chain getIndependentSpaceChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetIndependentSpaceCommand());
		return c;
	}
	
	public static Chain getNewSpaceChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static Chain getAddSpaceChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateSpaceFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddSpaceCommand());
		c.addCommand(getCategoryReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	
	public static Chain getSpaceDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetSpaceCommand());
		return c;
	}
	
	public static Chain getSpaceReportCardsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new GetSpaceReportCards());
		return c;
	}
	
	public static Chain getAllZoneChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllZoneCommand());
		return c;
	}
	
	public static Chain getAllZoneChildrenChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllZoneChildrenCommand());
		return c;
	}
	
	public static Chain getNewZoneChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static Chain getAddZoneChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateZoneFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddZoneCommand());
		return c;
	}
	
	public static Chain getUpdateZoneChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateZoneFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateZoneCommand());
		return c;
	}
	
	public static Chain getZoneDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetZoneCommand());
		return c;
	}
	
	public static Chain getNewAssetChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static Chain getWorkflowRulesChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetWorkflowRulesCommand());
		return c;
	}
	
	public static Chain getWorkflowRuleOfTypeChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetWorkFlowOfRuleTypeCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		return c;
	}
	
	public static Chain getUpdateWorkflowRuleAction() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateWorkFlowRuleAction());
		return c;
	}
	
	public static Chain getAddTemplateOfWorkflowRuleAction() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new CreateActionTemplateForWorkflowCommand());
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new UpdateTemplateInAction());
		c.addCommand(new DeleteActionTemplateCommand());
		return c;
	}
	
	public static Chain getDeleteAssetChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new DeleteResourceCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain getAssetListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
//		c.addCommand(SetTableNamesCommand.getForAsset());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new SetModuleForSpecialAssetsCommand());
//		c.addCommand(new LoadAssetFields());
		c.addCommand(new LoadViewCommand());
//		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetAssetListCommand());
		return c;
	}
	
	public static Chain getAssetDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new SetModuleForSpecialAssetsCommand());
		c.addCommand(new LoadAssetFields());
		c.addCommand(new GetAssetDetailCommand());
		c.addCommand(new GetAssetRelationCountCommand());
		return c;
	}
	
	public static Chain getAssetReportCardsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new GetAssetReportCards());
		return c;
	}
	
	public static Chain getAddAttachmentChain() {
		FacilioChain c = (FacilioChain) FacilioChain.getTransactionChain();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		c.addCommand(new AddAttachmentRelationshipCommand());
		c.addCommand(new AddAttachmentTicketActivityCommand());
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateAttachmentCountChain());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static Chain getAddFileChain() {
		FacilioChain c = (FacilioChain) FacilioChain.getTransactionChain();
		c.addCommand(new AddFileCommand());
		c.addCommand(new FileContextCommand());
		return c;
	}
	
	public static Chain getAddEnergyMeterChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddAdditionalPropsForEnergyMeterCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new AddVirtualMeterRelCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(getCategoryReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	
	public static Chain updateEnergyMeterChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateAdditionalPropsForEnergyMeterCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new AddVirtualMeterRelCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain getEnergyMeterListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new GenerateCondtionsFromFiltersCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain getTotalConsumptionBySiteChain() {
		Chain c = FacilioChain.getTransactionChain();
	    c.addCommand(new GetTotalConsumptionBySiteCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}

	public static Chain getAddEnergyMeterPurposeChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeterPurpose());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain getVirtualMeterChildrenChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetVirtualMeterChildrenCommand());
		return c;
	}
	
	public static Chain getAttachmentsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAttachmentsCommand());
		return c;
	}
	
	public static Chain getDeleteAttachmentChain() {
		FacilioChain c = (FacilioChain) FacilioChain.getTransactionChain();
		c.addCommand(new DeleteAttachmentCommand());
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateAttachmentCountChain());
		return c;
	}
	
	public static Chain getAllAreaChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateSortingQueryCommand());
		c.addCommand(new GetAllAreaCommand());
		return c;
	}
	
	public static Chain getBaseSpaceChildrenChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetBaseSpaceChildrenCommand());
		return c;
	}
	
	public static Chain getImportReadingChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ConstructVirtualSheetForReadingsImport());
		c.addCommand(new InsertReadingCommand());
		c.addCommand(new WriteSkippedToFileCommand());
		c.addCommand(new SendEmailCommand());
		return c;
	}
	
	public static Chain parseReadingDataForImport() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DataParseForLogsCommand());
		c.addCommand(new InsertImportDataIntoLogCommand());
		return c;
	}
	
	public static Chain parseImportData() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DataParseForImportCommand());
		c.addCommand(new InsertImportDataIntoLogCommand());
		return c;
	}
	
	public static Chain processImportData() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetZoneCommand());
		return c;
	}
	
	
	
	public static Chain getEmailSettingChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadSupportEmailsCommand());
		c.addCommand(new LoadEmailSettingCommand());
		return c;
	}
	
	public static Chain getUpdateEmailSettingChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateEmailSettingCommand());
		return c;
	}
	
	
	public static Chain getSupportEmailChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetSupportEmailCommand());
		return c;
	}
	
	public static Chain getAddSupportEmailChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddSupportEmailCommand());
		return c;
	}
	
	public static Chain getAddControllerChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddControllerCommand());
		return c;
	}
	
	public static Chain getControllerSettingsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadControllerSettingsCommand());
		return c;
	}
	public static Chain getUpdateSupportEmailChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateSupportEmailCommand());
		System.out.println("........ c"+ c);
		return c;
	}
	
	public static Chain getUpdateNotificationSettingChain(){
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateNotificationSettings());
		System.out.println("....Notification");

		return c;
	}
	public static Chain getDeleteSupportEmailChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteSupportEmailCommand());
		return c;
	}
	
	public static Chain getTemplateChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTemplateCommand());
		return c;
	}
	
	public static Chain deleteTemplateChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteTemplateCommand());
		return c;
	}
	
	public static Chain getAddWorkorderTemplateChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new CreateWorkorderTemplateCommand());
		return c;
	}
	
	public static Chain updateWorkorderTemplateChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteTemplateCommand());
		c.addCommand(new CreateWorkorderTemplateCommand());
		return c;
	}
	
	public static Chain addTaskGroupTemplateChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new CreateTaskGroupTemplateCommand());
		return c;
	}
	
	public static Chain updateTaskGroupTemplateChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteTemplateCommand());
		c.addCommand(new CreateTaskGroupTemplateCommand());
		return c;
	}

	public static Chain getAddPreventiveMaintenanceChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		c.addCommand(new ValidatePMTriggersCommand());
		c.addCommand(new ValidateTasksCommand());
		c.addCommand(new AddPMReadingsForTasks());
		c.addCommand(new CreateWorkorderTemplateCommand());	  // template addition
		c.addCommand(new AddPreventiveMaintenanceCommand());  // PM addition
		c.addCommand(new AddResourceReadingRelCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		c.addCommand(new AddPMReminderCommand());
		c.addCommand(new AddPMTriggerCommand());
		c.addCommand(new AddTaskSectionTriggersCommand());
		c.addCommand(new AddPMRelFieldsCommand());
		c.addCommand(new SchedulePMCommand());
		c.addCommand(new scheduleBeforePMRemindersCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddValidationRulesCommand());
		return c;
	}
	
	public static Chain getAddNewPreventiveMaintenanceChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		c.addCommand(new ValidateNewPMTriggersCommand());
		c.addCommand(new ValidateNewTasksCommand());
		c.addCommand(new AddPMReadingsForTasks());
		c.addCommand(new CreateWorkorderTemplateCommand());	  // template addition
		c.addCommand(new AddPreventiveMaintenanceCommand());  // PM addition
		c.addCommand(new AddResourceReadingRelCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		c.addCommand(new AddPMReminderCommand());
		c.addCommand(new AddPMTriggerCommand());
		c.addCommand(new AddTaskSectionTriggersCommand());
		c.addCommand(new AddPMRelFieldsCommand());
		c.addCommand(new ScheduleCreateWOJob());
		// c.addCommand(new scheduleBeforePMRemindersCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddValidationRulesCommand());
		return c;
	}

	public static Chain getDeleteScheduledWorkOrderChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new SplitDependentTicketsCommand());
		c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		return c;
	}

	public static Chain getUpdatePreventiveMaintenanceChain() {
		Chain c = FacilioChain.getTransactionChain();
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
		c.addCommand(new AddPMTriggerCommand());
//		c.addCommand(new DeletePMRemindersCommand());
		c.addCommand(new AddTaskSectionTriggersCommand());
		c.addCommand(new AddPMReminderCommand());
		c.addCommand(new AddPMRelFieldsCommand());
		c.addCommand(new SchedulePMCommand());
		c.addCommand(new scheduleBeforePMRemindersCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddValidationRulesCommand());
		return c;
	}

	public static Chain getUpdateNewPreventiveMaintenanceChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		
		c.addCommand(new GetPreventiveMaintenanceCommand());
		c.addCommand(new ValidateNewPMTriggersCommand());
		c.addCommand(new ValidateNewTasksCommand());
		c.addCommand(new AddPMReadingsForTasks());
		c.addCommand(new CreateWorkorderTemplateCommand());
		c.addCommand(new UpdatePreventiveMaintenanceCommand());
		c.addCommand(new AddResourceReadingRelCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		c.addCommand(new DeletePMAndDependenciesCommand(false));
		c.addCommand(new AddPMTriggerCommand());
//		c.addCommand(new DeletePMRemindersCommand());
		c.addCommand(new AddTaskSectionTriggersCommand());
		c.addCommand(new AddPMReminderCommand());
		c.addCommand(new AddPMRelFieldsCommand());
		c.addCommand(new BlockPMEditOnWOGeneration());
		c.addCommand(new ScheduleCreateWOJob());
		// c.addCommand(new scheduleBeforePMRemindersCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddValidationRulesCommand());
		return c;
	}

	public static Chain getUpdateNewPreventiveMaintenanceJobChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateNewPreventiveMaintenanceJobCommand());
		return c;
	}
	
	public static Chain getUpdatePreventiveMaintenanceJobChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdatePreventiveMaintenanceJobCommand());
		return c;
	}
	
	public static Chain getPreventiveMaintenanceSummaryChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new PreventiveMaintenanceSummaryCommand());
		c.addCommand(new GetPMWorkOrders());
		c.addCommand(new GetTaskInputDataCommand());
		return c;
	}

	public static Chain getNewPreventiveMaintenanceSummaryChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new NewPreventiveMaintenanceSummaryCommand());
		c.addCommand(new GetPMWorkOrders());
		c.addCommand(new GetTaskInputDataCommand());
		return c;
	}

	public static Chain getDeletePreventiveMaintenanceChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetPreventiveMaintenanceCommand());
		c.addCommand(new BlockPMEditOnWOGeneration(true, false));
		c.addCommand(new DeletePMAndDependenciesCommand(true));
		return c;
	}
	
	public static Chain getGetUpcomingPreventiveMaintenanceListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetUpcomingPreventiveMaintenanceCommand());
		return c;
	}
	
	public static Chain getGetPMJobListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetPMJobsCommand());
		return c;
	}

	public static Chain getGetNewPMJobListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetScheduledWOCommand());
		return c;
	}
	
	public static Chain getGetPreventiveMaintenanceListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetPreventiveMaintenanceCommand());
		return c;
	}
	
	public static Chain getExecutePMReminderChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetPMAndPMReminderCommand());
		c.addCommand(new GetWOForPMReminderCommand());
		c.addCommand(new ExecutePMReminderCommand());
		return c;
	}
	
	public static Chain getTemplatesOfTypeChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTemplatesOfTypeCommand());
		return c;
	}
	
	public static Chain getAddTemplateChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddTemplateCommand());
		return c;
	}
	
	public static Chain getDeleteWorkflowRuleChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteWorkflowRuleCommand());
		return c;
	}
	
	public static Chain getReadingRulesOfFieldsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetReadingRulesFromFieldsCommand());
		return c;
	}
	
	public static Chain runThroughFilters() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain getAddAlarmEMailNotifierChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddOrUpdateAlarmEMailTemplateCommand());
		c.addCommand(new UpdateAlarmCreationActionCommand());
		return c;
	}
	
	public static Chain getAlarmCreationRulesChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadAlarmCreationRulesCommand());
		return c;
	}
	
	public static Chain getAddAlarmSMSNotifierChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddOrUpdateAlarmSMSTemplateCommand());
		c.addCommand(new UpdateAlarmCreationActionCommand());
		return c;
	}
	
	public static Chain getViewListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetViewListCommand());
		return c;
	}
	
	public static Chain getAddViewChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new AddCVCommand());
		c.addCommand(new CustomizeViewColumnCommand());
		return c;
	}
	
	public static Chain getViewDetailsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateFilterFromCriteriaCommand());		
		return c;
	}
	
	public static Chain getViewsCustomizeChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new CustomizeViewCommand());
		c.addCommand(new GetViewListCommand());
		return c;
	}
	
	public static Chain getViewCustomizeColumnChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new CustomizeViewColumnCommand());
		return c;
	}
	
	public static Chain getViewCustomizeSortColumnsChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new SaveSortFieldsCommand());
		return c;
	}
	
	public static Chain getUpdateFieldChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateFieldCommand());
		return c;
	}
	
	public static Chain getdeleteFieldsChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteFieldsCommand());
		return c;
	}
	
	public static Chain getGetFieldsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	public static Chain fetchExportModuleChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetExportModuleCommand());
		return c;
	}
	
	public static Chain sendModuleMailChain () {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(FacilioChainFactory.fetchExportModuleChain());
		c.addCommand(new SendReadingReportMailCommand());
		return c;
	}
	public static Chain getAllSpaceCategoriesChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;	
	}
	
	public static Chain addSpaceCategoryChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static Chain updateSpaceCategoryChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
//		
//		c.addCommand(new EditSpaceCategoryCommand());
//		return c;		
	}
	
	public static Chain deleteSpaceCategoryChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
		
//		c.addCommand(new DeleteSpaceCategoryCommand());
//		return c;		
	}
	
	public static Chain getUpdateReadingChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new RestrictUneditablePropsInFieldCommand());
		c.addCommand(getUpdateFieldChain());
		c.addCommand(new AddValidationRulesCommand());
		return c;
	}
	
	public static Chain getAssetReadingsChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetAssetSpecifcReadingsCommand());
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static Chain getGetLatestAssetReadingValuesChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(getAssetReadingsChain());
		c.addCommand(new GetLatestReadingValuesCommand());
		return c;
	}
	public static Chain getResourcesOccupantReadingValuesChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetResourcesLatestReadingValuesCommand());
		return c;
	}
	
	public static Chain getSpaceReadingsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetSpaceSpecifcReadingsCommand());
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static Chain getGetLatestSpaceReadingValuesChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new GetLatestReadingValuesCommand());
		return c;
	}
	
	public static Chain getCategoryReadingsChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static Chain getAllFieldsChain () {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllFieldsCommand());
		return c;
	}
	
	public static Chain getAllCategoryReadingsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllCategoryReadingsCommand());
		c.addCommand(new GetAllAssetSpecificReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static Chain getSiteSpecificReadingsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetSiteSpecificReadingsCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}


	public static Chain getReadingsForSpaceTypeChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetAllSpaceTypeReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static Chain addResourceRDMChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetReadingFieldsCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	
	public static Chain getGetLatestReadingValuesChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(getCategoryReadingsChain());
		c.addCommand(new GetLatestReadingValuesCommand());
		return c;
	}
	
	public static Chain getGetReadingValuesChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetLatestReadingValuesCommand());
		return c;
	}
	
	public static Chain getAddPhotosChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddPhotosCommand());
		return c;
	}
	
	public static Chain getUploadPhotosChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UploadPhotosCommand());
		c.addCommand(new AddPhotosCommand());
		return c;
	}
	
	public static Chain justUploadPhotosChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UploadPhotosCommand());
		return c;
	}
	
	public static Chain getReadingFromImageChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTextsFromImageCommand());
		c.addCommand(new FilterReadingsFromImageCommand());
		return c;
	}
	
	public static Chain getUpdateDefaultSpacePhotoChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateDefaultSpacePhotoCommand());
		return c;
	}
	
	public static Chain getPhotosChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetPhotosCommand());
		return c;
	}
	
	public static Chain getNotesChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetNotesCommand());
		return c;
	}
	
	public static Chain getEnableMobileDashboardChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new EnableMobileDashboardCommand());
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
//		return c;
//	}
	
	public static Chain addBaseLineChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateBaseLineCommand());
		c.addCommand(new AddBaseLineCommand());
		return c;
	}
	
	public static Chain getBaseLinesOfSpaceChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetBaseLinesForSpaceCommand());
		return c;
	}
	
	public static Chain getAllBaseLinesChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllBaseLinesCommand());
		return c;
	}
	
	public static Chain addReportBaseLinesChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddReportBaseLinesCommand());
		return c;
	}
	
	public static Chain updateReportBaseLinesChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteExistingReportBaseLineCommand());
		c.addCommand(new AddReportBaseLinesCommand());
		return c;
	}
	
	public static Chain getAllFormulasOfTypeChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllFormulasOfTypeCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static Chain updateFormulaChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateFormulaCommand());
		c.addCommand(new AddValidationRulesCommand());
		return c;
	}
	
	public static Chain deleteFormulaChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteFormulaCommand());
		return c;
	}
	
	public static Chain updateReportColumnSequence() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateReportColumnSequence());
		return c;
	}
	
	public static Chain getAddTicketCategoryChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static Chain getUpdateTicketCategoryChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	public static Chain getDeleteTicketCategoryChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	public static Chain getAddTicketPriorityChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static Chain getUpdateTicketPriorityChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static Chain getUpdateAlarmSeverityChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarmSeverity());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static Chain getUpdateAlarmSeveritiesChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarmSeverity());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		return c;
	}
	
	
	public static Chain getUpdateTicketPrioritiesChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		return c;
	}
	
	public static Chain getDeleteTicketPriorityChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	public static Chain getDeleteAlarmSeverityChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarmSeverity());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	public static Chain getAddTicketTypeChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	public static Chain getUpdateTicketTypeChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static Chain getDeleteTicketTypeChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static Chain getAddAssetCategoryChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new AddAssetCategoryModule());
		TransactionChainFactory.commonAddModuleChain(c);
		c.addCommand(new UpdateCategoryAssetModuleIdCommand());
		return c;
	}
	
	public static Chain getUpdateAssetCategoryChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		
		return c;
	}
	
	public static Chain getDeleteAssetCategoryChain() {		
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateAssetCategoryDeletion());
		c.addCommand(SetTableNamesCommand.getForAssetCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static Chain getAddAssetDepartmentChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetDepartment());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static Chain getUpdateAssetDepartmentChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetDepartment());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static Chain getDeleteAssetDepartmentChain() {		
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateAssetDepartmentDeletion());
		c.addCommand(SetTableNamesCommand.getForAssetDepartment());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static Chain getAddAssetTypeChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static Chain getUpdateAssetTypeChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static Chain getDeleteAssetTypeChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateAssetTypeDeletion());
		c.addCommand(SetTableNamesCommand.getForAssetType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static Chain getAddAlarmSeverityChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarmSeverity());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static Chain addModuleDataChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain updateModuleDataChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain deleteModuleDataChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static Chain calculateBenchmarkValueChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new CalculateBenchmarkValueCommand());
		return c;
	}
	
	public static Chain getAllShiftsCommand() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllShiftsCommand());
		return c;
	}
	
	public static Chain getAddShiftCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddShiftCommand());
		return c;
	}
	
	public static Chain getUpdateShiftCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateShiftCommand());
		return c;
	}
	
	public static Chain getDeleteShiftCommand() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteShiftCommand());
		return c;
	}
	
	public static Chain calculateCostChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new FetchCostDataCommand());
		c.addCommand(new CalculateCostCommand());
		return c;
	}
	
	public static Chain addTenantChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTenants());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddTenantCommand());
		c.addCommand(new AddTenantUserCommand());
		return c;
	}
	
	public static Chain addTenantUserChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddTenantUserCommand());
		return c;
	}
	
	public static Chain updateTenantChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateTenantCommand());
		return c;
	}
	
	public static Chain updateTenantPrimaryContactChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdatePrimaryContactCommand());
		return c;
	}
	
	public static Chain toggleStatusChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new ToggleTenantStatusCommand());
		return c;
	}
	
	public static Chain addRateCardChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddRateCardCommand());
		return c;
	}
	
	public static Chain updateRateCardChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateRateCardCommand());
		return c;
	}
	
	public static Chain addOfflineSyncErrorChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddOfflineSyncErrorCommand());
		return c;
	}
	
	public static Chain getFormMetaChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetFormMetaCommand());
		return c;
	}
	
	public static Chain getFormFieldsChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetFormFieldsCommand());
		return c;
	}
	
	public static Chain getMLModelBuildingChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetReadingsForMLCommand());
		c.addCommand(new ApplyCriteriaForMLCommand());
		c.addCommand(new GenerateMLModelCommand());
		c.addCommand(new AddReadingsForMLCommand());
		c.addCommand(new TriggerAlarmForMLCommand());
		c.addCommand(new ApplyRuleForMLCommand());
		return c;
	}
	
	public static Chain enableAnomalyDetectionChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new EnableAnomalyDetectionCommand());
		return c;
	}
	
	
	public static Chain getNewInventoryChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForInventory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getPermaLinkTokenChain() {
		Chain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetPermaLinkTokenCommand());
		return c;
	}
	
	public static Chain getTenantListChain() {
		Chain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetTenantListCommand());
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
		Chain c = FacilioChain.getTransactionChain();
	
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
}