package com.facilio.bmsconsole.commands;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsole.commands.IAMUserManagement.*;
import com.facilio.bmsconsole.commands.form.HandleFormFieldsCommand;
import com.facilio.bmsconsoleV3.commands.shift.AssignShiftToUserCommand;
import com.facilio.bmsconsoleV3.commands.plannedmaintenance.GetScheduledWoCommandV3;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.elasticsearch.command.DeleteDataFromESCommand;
import com.facilio.leed.commands.AddConsumptionForLeed;
import com.facilio.leed.commands.AddEnergyMeterCommand;
import com.facilio.leed.commands.FetchArcAssetsCommand;
import com.facilio.leed.commands.LeedBuildingDetailsCommand;
import com.facilio.permission.commands.AddOrUpdatePermissionSetsForPeopleCommand;
import com.facilio.v3.commands.AddPublicFileCommand;
import com.facilio.v3.commands.DeletePublicFileCommand;
import com.facilio.v3.commands.AddPublicUrlForFileCommand;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class FacilioChainFactory {
    private static Logger LOGGER = LogManager.getLogger(FacilioChainFactory.class.getName());

	public static FacilioChain addDefaultReportChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddDefaultReportCommand());
		return c;
	}

	public static FacilioChain getAddEnergyDataChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		//c.addCommand(SetTableNamesCommand.getForEner);
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}

	public static FacilioChain getAssetActionChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new PerformAssetAction());
		return c;
		
	}
	public static FacilioChain getPickListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadMainFieldCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateCriteriaFromClientCriteriaCommand());
		c.addCommand(new GetPickListCommand());
		return c;
	}
	
	public static FacilioChain getUpdateTicketChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateTicketCommand());
		return c;
	}
	
	public static FacilioChain getTicketDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketCommand());
		return c;
	}
	
	public static FacilioChain getTicketListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketListCommand());
		return c;
	}
	public static FacilioChain calculateTenantBill() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new CalculateUtilityService());
		c.addCommand(new CalculateFormulaService());
		c.addCommand(new CalculateFinalResult());
		return c;
	}
	public static FacilioChain getAssignTicketChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicket());
		c.addCommand(new AssignTicketCommand());
		return c;
	}
	
	public static FacilioChain getTicketActivitiesChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTicketActivitesCommand());
		return c;
	}
	
	public static FacilioChain getTicketStatusListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketStatus());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketStatusListCommand());
		return c;
	}
	
	public static FacilioChain getTicketPriorityListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketPriorityListCommand());
		return c;
	}
	
	public static FacilioChain getTicketCategoryListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTicketCategoryListCommand());
		return c;
	}
	
	public static FacilioChain getAddUserCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddUserCommand());
		c.addCommand(new AddEmployeeTypePeopleForUserAdditionCommand());
		c.addCommand(new AddorUpdateUserScopingCommand());
		c.addCommand(new AddOrUpdatePermissionSetsForPeopleCommand());
		c.addCommand(new AssignShiftToUserCommand());
		return c;
	}
	public static Command getChangeTeamStatusCommand(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ChangeTeamStatusCommand());
		return c;
	}
	
	public static Command getAddUserMobileSettingCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddUserMobileSettingCommand());
		return c;
	}
	public static Command getDeleteUserMobileSettingCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new RemoveUserMobileSettingCommand());
		return c;
	}
	
	public static Command getGetTasksOfTicketCommand() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTasksOfTicketCommand());
		return c;
	}
	
	public static Command getAddGroupCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateWorkOrderFieldsCommand());
		c.addCommand(new AddGroupCommand());
		c.addCommand(new SetOuIdInPeopleGroupMemberCommand());
		c.addCommand(new AddGroupMembersCommand());
		return c;
	}
	
	public static Command getAddRoleCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddRoleCommand());
		c.addCommand(new AddPermissionsCommand());
		return c;		
	}
	
	public static Command getAddWebTabRoleCommmand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddRoleCommand());
		c.addCommand(new AddNewPermissionsForRoleCommand());
		return c;		
	}
	
	public static Command getUpdateGroupCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateGroupCommand());
		c.addCommand(new AddGroupMembersCommand());
		return c;
	}
	
	public static Command getUpdateRoleCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateRoleCommand());
		c.addCommand(new AddPermissionsCommand());
		return c;
	}

	public static Command getUpdateWebTabRoleCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateRoleCommand());
		c.addCommand(new AddNewPermissionsForRoleCommand());
		return c;
	}
	
	public static Command getDeleteGroupCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteGroupCommand());
		return c;
	}
	public static Command getDeleteRoleCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteRoleCommand());
		return c;
	}

	public static FacilioChain getNewWorkOrderChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		return c;
	}
	
//	public static FacilioChain getAddWorkOrderWithTicketChain() {
//		FacilioChain c = getDefaultChain();
//		c.addCommand(getAddTicketChain());
//        c.addCommand(getAddWorkOrderChain());
//        //c.addCommand(new AddTicketActivityCommand());
//		return c;
//	}
	
	public static FacilioChain getDeleteWorkOrderChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new SplitDependentTicketsCommand());
		// c.addCommand(new DeleteTicketDependenciesCommand()); // Disabling this command since we are marking as deleted
		c.addCommand(new DeleteTicketCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION));
		c.addCommand(new DeleteDataFromESCommand());
		return c;
	}
	
	public static FacilioChain getWorkOrderDataChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static FacilioChain getNewWorkOrderRequestChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		return c;
	}
	
	public static FacilioChain getAddWorkOrderRequestChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
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
	
	public static FacilioChain getWorkOrderRequestDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderRequestCommand());
		return c;
	}
	
	public static FacilioChain getWorkOrderRequestListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetWorkOrderRequestListCommand());
		return c;
	}
	
	public static FacilioChain getDeleteWorkOrderRequestChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new SplitDependentTicketsCommand());
		// c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static FacilioChain getUpdateWorkOrderRequestChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderRequest());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateWorkOrderRequestCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new AddWOFromRequestCommand());
		return c;
	}
	
	public static FacilioChain getDeleteAlarmChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new RemoveAlarmFromEventCommand());
		c.addCommand(new RemoveAssetBreakdownCommand());
		c.addCommand(new updateAssetDownTimeDetailsCommand());
		c.addCommand(new SplitDependentTicketsCommand());
		c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static FacilioChain getAddAlarmTemplateChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddAlarmTemplateCommand());
		return c;
	}

	public static FacilioChain getPortalUsersListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetPortalUsersListCommand());
		return c;
	}
	
	public static FacilioChain getUpdateAlarmResourceChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateAlarmAssetCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static FacilioChain deleteNotesChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new FetchNoteCommand());
		c.addCommand(new DeleteCommentSharingCommand());
		c.addCommand(new DeleteNoteCommand());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static FacilioChain getReadingAlarmsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
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
	
	public static FacilioChain getNewTaskChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadTicketFieldsCommand());
		return c;
	}
	
	public static FacilioChain getAddTaskChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		//c.addCommand(getAddTicketChain());
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new ValidateTasksCommand());
		c.addCommand(new VerifyApprovalCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddTaskCommand());
		c.addCommand(new AddTaskOptionsCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static FacilioChain getDeleteTaskChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new DeleteTaskCommand());
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
		return c;
	}
	
	public static FacilioChain getTaskDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetTaskCommand());
		c.addCommand(new GetTaskInputDataCommand());
		return c;
	}
	
	public static FacilioChain getRelatedTasksChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new GetRelatedTasksCommand());
		c.addCommand(new GetTaskInputDataCommand());
		c.addCommand(new SiUnitConversionToSelectedReadingUnit());
		return c;
	}
	
	public static FacilioChain getRelatedMultipleTasksChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new GetMultipleRelatedTasksCommand());
		c.addCommand(new GetTaskInputDataCommand());
		c.addCommand(new SiUnitConversionToSelectedReadingUnit());
		return c;
	}
	
	public static FacilioChain addTaskSectionChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddTaskSectionCommand());
		c.addCommand(new UpdateTaskWithSectionCommand());
		return c;
	}
	
	public static FacilioChain getPortalInfoChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadPortalInfoCommand());
		return c;
	}
	
	public static FacilioChain updatePortalInfoChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new HandlePortalInfoCommand());
		return c;
	}
	public static FacilioChain updatePortalSSOChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new HandlePortalSSOCommand());
		return c;
	}
	
	public static FacilioChain addReadingMetaDataEntry() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	public static Command getAllLocationsCommand() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static FacilioChain addLocationChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static FacilioChain updateLocationChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteLocationChain() {
		
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getLocationChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForLocation());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetLocationCommand());
		return c;
	}
	
	public static Command getAllSkillsCommand() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new GetAllSkillsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static FacilioChain addSkillChain()
	{
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new AddSkillCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static Command updateSkillCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new UpdateSkillCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	
	
	public static Command deleteSkillCommand(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSkill());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		//c.addCommand(SetTableNamesCommand.getForSkill());
		//c.addCommand(new DeleteSkillCommand());
		return c;
	}
	
	public static FacilioChain getAllCampusChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllCampusCommand());
		return c;
	}
	
	public static FacilioChain getNewCampusChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static FacilioChain getAddCampusChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateCampusFieldsCommand());
		c.addCommand(new AddLocationCommand());
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddCampusCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		c.addCommand(new UpdateEventListForStateFlowCommand());
		c.addCommand(new SetSiteRecordForRollUpFieldCommand());
		c.addCommand(new ExecuteRollUpFieldCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand(false)
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ConstructAddCustomActivityCommand());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static FacilioChain getUpdateCampusChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateCampusFieldsCommand());
		c.addCommand(updateLocationChain());
		c.addCommand(new FacilioCommand() {
			@Override
			public boolean executeCommand(Context context) throws Exception {
				String moduleName = (String) context.get(FacilioConstants.ContextNames.SPACE_TYPE);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
				BaseSpaceContext baseSpace = (BaseSpaceContext) context.get(FacilioConstants.ContextNames.BASE_SPACE);
				baseSpace = (BaseSpaceContext) RecordAPI.getRecord(moduleName, baseSpace.getId());
				CommonCommandUtil.addToRecordMap((FacilioContext) context, moduleName, baseSpace);
				return false;
			}
		});
		c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
		c.addCommand(new VerifyApprovalCommand());
		c.addCommand(new UpdateBaseSpaceCommand());
		c.addCommand(new UpdateEventListForStateFlowCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new SetBaseSpaceRecordForRollUpFieldCommand());
		c.addCommand(new ExecuteRollUpFieldCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new SetModuleNameForActivity());
		c.addCommand(new ConstructUpdateCustomActivityCommand());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static FacilioChain deleteSpaceChain () {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new SetDeleteBaseSpaceRecordForRollUpFieldCommand());
		c.addCommand(new GenericDeleteForSpaces());
		c.addCommand(new ExecuteRollUpFieldCommand());
		return c;
	}
	public static FacilioChain deleteTenantChain () {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteTenantCommand());
		c.addCommand(new DeleteTenantZonesCommand());
		return c;
	}
	
	
	public static FacilioChain getCampusDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetCampusCommand());
		return c;
	}
	
	public static FacilioChain getSiteReportCardsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new GetSiteReportCards());
		return c;
	}
	
	public static FacilioChain getTenantReportCardsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTenants());
		c.addCommand(new GetTenantReportCards());
		return c;
	}
	
	public static FacilioChain getTenantReadingCardsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTenants());
		c.addCommand(new GetTenantReadingCardsCommand());
		return c;
	}
	
	public static FacilioChain getAllBuildingChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllBuildingCommand());
		return c;
	}
	
	public static FacilioChain getBuildingReportCardsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSite());
		c.addCommand(new GetBuildingReportCards());
		return c;
	}
	
	public static FacilioChain getNewBuildingChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}

	public static FacilioChain getAddBuildingChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddBuildingCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new UpdateEventListForStateFlowCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		c.addCommand(new SetBuildingRecordForRollUpFieldCommand());
		c.addCommand(new ExecuteRollUpFieldCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand(false)
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ConstructAddCustomActivityCommand());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static FacilioChain getUpdateBuildingChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateBuildingCommand());
		c.addCommand(new SetBuildingRecordForRollUpFieldCommand());
		c.addCommand(new ExecuteRollUpFieldCommand());
		return c;
	}
	
	
	public static FacilioChain getBuildingDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForBuilding());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetBuildingCommand());
		return c;
	}
	
	public static FacilioChain AddEnergyMeterChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddEnergyMeterCommand());
		return c;
	}

	public static FacilioChain LeedDetailsPageChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LeedBuildingDetailsCommand());
		return c;
	}
	
	public static FacilioChain addConsumptionDataChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddConsumptionForLeed());
		//c.addCommand(new AddConsumptionData());
		return c;
	}
	
	public static FacilioChain FetchAssetsFromArcChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new FetchArcAssetsCommand());
		return c;
	}
	
	public static FacilioChain getAllFloorChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllFloorCommand());
		return c;
	}
	
	public static FacilioChain getNewFloorChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static FacilioChain getAddFloorChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateFloorFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddFloorCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new UpdateEventListForStateFlowCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		c.addCommand(new SetFloorRecordForRollUpFieldCommand());
		c.addCommand(new ExecuteRollUpFieldCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand(false)
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ConstructAddCustomActivityCommand());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static FacilioChain getFloorDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetFloorCommand());
		return c;
	}
	
	public static FacilioChain getFloorReportCardsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new GetFloorReportCards());
		return c;
	}
	public static FacilioChain getAllSpaceChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetAllSpaceCommand());
		return c;
	}
	
	public static FacilioChain getIndependentSpaceChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetIndependentSpaceCommand());
		return c;
	}
	
	public static FacilioChain getNewSpaceChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static FacilioChain getAddSpaceChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateSpaceFieldsCommand());
		c.addCommand(new SetSpaceCategoryModuleDetailsCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddSpaceCommand());
		c.addCommand(new ConstructAddCustomActivityCommand());
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new UpdateEventListForStateFlowCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		c.addCommand(new SetSpaceRecordForRollUpFieldCommand());
		c.addCommand(new ExecuteRollUpFieldCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand(false)
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static FacilioChain getSpaceDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpace());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetSpaceCommand());
		return c;
	}
	
	public static FacilioChain getSpaceReportCardsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForFloor());
		c.addCommand(new GetSpaceReportCards());
		return c;
	}
	
	public static FacilioChain getAllZoneChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllZoneCommand());
		return c;
	}
	
	public static FacilioChain getAllZoneChildrenChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAllZoneChildrenCommand());
		return c;
	}
	
	public static FacilioChain getNewZoneChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static FacilioChain getAddZoneChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateZoneFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddZoneCommand());
		return c;
	}
	
	public static FacilioChain getUpdateZoneChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateZoneFieldsCommand());
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateZoneCommand());
		return c;
	}
	
	public static FacilioChain getZoneDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetZoneCommand());
		return c;
	}
	
	public static FacilioChain getNewAssetChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static FacilioChain getWorkflowRulesChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetWorkflowRulesCommand());
		return c;
	}
	
	public static FacilioChain getWorkflowRuleOfTypeChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetWorkFlowOfRuleTypeCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		return c;
	}
	
	public static FacilioChain getUpdateWorkflowRuleAction() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateWorkFlowRuleAction());
		return c;
	}
	
	public static FacilioChain getAddTemplateOfWorkflowRuleAction() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new CreateActionTemplateForWorkflowCommand());
		c.addCommand(new AddTemplateCommand());
		c.addCommand(new UpdateTemplateInAction());
		c.addCommand(new DeleteActionTemplateCommand());
		return c;
	}
	
	public static FacilioChain getDeleteAssetChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new DeleteResourceCommand());
		c.addCommand(new RotatingItemQuantityRollUpCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static FacilioChain getAssetListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
//		c.addCommand(SetTableNamesCommand.getForAsset());
//		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new SetModuleForSpecialAssetsCommand());
//		c.addCommand(new LoadAssetFields());
		c.addCommand(new LoadViewCommand());
//		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateCriteriaFromClientCriteriaCommand());
		c.addCommand(new AddLookupFieldMetaList(false));
		c.addCommand(new GetAssetListCommand());
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
		return c;
	}
	
	public static FacilioChain getAssetDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new SetModuleForSpecialAssetsCommand());
		c.addCommand(new LoadAssetFields());
		c.addCommand(new GetAssetDetailCommand());
		c.addCommand(new GetAssetHazardsCommand());
		c.addCommand(new GetAssetRelationCountCommand());
		return c;
	}

	public static FacilioChain getControllerModuleName(){
        FacilioChain c = FacilioChain.getNonTransactionChain();
        c.addCommand(new SetModuleForSpecialAssetsCommand());
        return c;
    }
	
	public static FacilioChain getAssetReportCardsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAsset());
		c.addCommand(new GetAssetReportCards());
		return c;
	}
	
	public static FacilioChain getAddAttachmentChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		c.addCommand(new AddAttachmentRelationshipCommand());
		c.addCommand(new AddAttachmentTicketActivityCommand());
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateAttachmentCountChain());
		c.addCommand(new AddActivitiesCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand(false)
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION))
		);
		return c;
	}
	
	public static FacilioChain getAddAttachmentFromFileIdsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AttachmentContextCommand());
		c.addCommand(new AddAttachmentRelationshipCommand());
		c.addCommand(new AddAttachmentTicketActivityCommand());
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateAttachmentCountChain());
		c.addCommand(new AddActivitiesCommand());
		return c;
	}
	
	public static FacilioChain getAddFileChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddFileCommand());
		c.addCommand(new FileContextCommand());
		return c;
	}

	public static FacilioChain getDeleteFileChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteFileCommand());
		return c;
	}

	public  static FacilioChain getDeletePublicFileChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeletePublicFileCommand());
		return  c;
	}
	public  static FacilioChain getAddPublicUrlForFileChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddPublicUrlForFileCommand());
		return  c;
	}
	public  static FacilioChain getAddPublicFileChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddPublicFileCommand());
		return  c;
	}
	public static FacilioChain getAddEnergyMeterChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddAdditionalPropsForEnergyMeterCommand());
		c.addCommand(new AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMF());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new AddVirtualMeterRelCommand());
		c.addCommand(new CreateFormulaFromVMCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(getCategoryReadingsChain());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	
	public static FacilioChain updateEnergyMeterChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMF());
		c.addCommand(new UpdateAdditionalPropsForEnergyMeterCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new AddVirtualMeterRelCommand());
		c.addCommand(new CreateFormulaFromVMCommand(true));
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static FacilioChain getEnergyMeterListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeter());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new GenerateCondtionsFromFiltersCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static FacilioChain getTotalConsumptionBySiteChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
	    c.addCommand(new GetTotalConsumptionBySiteCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}

	public static FacilioChain getAddEnergyMeterPurposeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEnergyMeterPurpose());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static FacilioChain getVirtualMeterChildrenChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetVirtualMeterChildrenCommand());
		return c;
	}
	
	public static FacilioChain getAttachmentsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAttachmentsCommand());
		return c;
	}

	public static FacilioChain getModuleAttachmentsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new ValidateModuleAttachments());
		c.addCommand(new GetAttachmentsCommand());
		return c;
	}

	public static FacilioChain getDeleteAttachmentChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteAttachmentCommand());
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateAttachmentCountChain());
		return c;
	}
	
	public static FacilioChain getAllAreaChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateSortingQueryCommand());
		c.addCommand(new GetAllAreaCommand());
		return c;
	}
	
	public static FacilioChain getBaseSpaceChildrenChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetBaseSpaceChildrenCommand());
		return c;
	}
	
	
	public static FacilioChain processImportData() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForZone());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetZoneCommand());
		return c;
	}
	
	public static FacilioChain getEmailSettingChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadSupportEmailsCommand());
		c.addCommand(new LoadEmailSettingCommand());
		return c;
	}
	
	public static FacilioChain getUpdateEmailSettingChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateEmailSettingCommand());
		return c;
	}
	
	
	public static FacilioChain getSupportEmailChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetSupportEmailCommand());
		return c;
	}
	
	public static FacilioChain getAddSupportEmailChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddSupportEmailCommand());
		c.addCommand(new AddSupportEmailToEmailFromAddressCommand());
		return c;
	}
	
	public static FacilioChain getAddControllerChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddControllerCommand());
		return c;
	}
	
	public static FacilioChain getUpdateSupportEmailChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateSupportEmailCommand());
		return c;
	}
	
	public static FacilioChain getUpdateNotificationSettingChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateNotificationSettings());
		System.out.println("....Notification");

		return c;
	}
	public static FacilioChain getDeleteSupportEmailChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteSupportEmailCommand());
		return c;
	}
	
	public static FacilioChain getTemplateChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTemplateCommand());
		return c;
	}
	
	public static FacilioChain deleteTemplateChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteTemplateCommand());
		return c;
	}
	
	public static FacilioChain getAddWorkorderTemplateChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new CreateWorkorderTemplateCommand());
		return c;
	}
	
	public static FacilioChain updateWorkorderTemplateChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteTemplateCommand());
		c.addCommand(new CreateWorkorderTemplateCommand());
		return c;
	}
	
	public static FacilioChain addTaskGroupTemplateChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new CreateTaskGroupTemplateCommand());
		return c;
	}
	
	public static FacilioChain updateTaskGroupTemplateChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteTemplateCommand());
		c.addCommand(new CreateTaskGroupTemplateCommand());
		return c;
	}

	public static FacilioChain getAddNewPreventiveMaintenanceChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
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
		c.addCommand(new SchedulePreOpenWOCreateCommand());
		// c.addCommand(new scheduleBeforePMRemindersCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddValidationRulesCommand());
		return c;
	}

	public static FacilioChain getDeleteScheduledWorkOrderChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new SplitDependentTicketsCommand());
		// c.addCommand(new DeleteTicketDependenciesCommand());
		c.addCommand(new DeleteTicketCommand());
		return c;
	}

	public static FacilioChain getUpdateNewPreventiveMaintenanceChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
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
		c.addCommand(new SchedulePreOpenWOCreateCommand());
		// c.addCommand(new scheduleBeforePMRemindersCommand());
		c.addCommand(new UpdateReadingDataMetaCommand());
		c.addCommand(new AddValidationRulesCommand());
		return c;
	}

	public static FacilioChain getUpdateNewPreventiveMaintenanceJobChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateNewPreventiveMaintenanceJobCommand());
		return c;
	}
	public static FacilioChain getPreventiveMaintenanceReadingsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new PreventiveMaintenanceReadingsCommand());
		return c;
	}
	public static FacilioChain getNewPreventiveMaintenanceSummaryChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new NewPreventiveMaintenanceSummaryCommand());
		c.addCommand(new GetPMWorkOrders());
		c.addCommand(new GetTaskInputDataCommand());
		return c;
	}
	
	public static FacilioChain fetchRelatedWorkordersChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetPMWorkOrders());
		return c;
	}

	public static FacilioChain getResourcePlannerChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetPMResourcePlanners());
		return c;
	}
	
	public static FacilioChain fetchPreventiveMaintenanceDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new NewPreventiveMaintenanceSummaryCommand());
		c.addCommand(new GetTaskInputDataCommand());
		return c;
	}

	public static FacilioChain getDeletePreventiveMaintenanceChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetPreventiveMaintenanceCommand());
		c.addCommand(new DeletePMAndDependenciesCommand(true));
		c.addCommand(new BlockPMEditOnWOGeneration(true, false, false));
		c.addCommand(new DeletePMCommand());
		return c;
	}
	

	public static FacilioChain getGetNewPMJobListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetScheduledWOCommand());
		return c;
	}
	
	public static FacilioChain getGetPreventiveMaintenanceListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetPreventiveMaintenanceCommand());
		return c;
	}
	
	public static FacilioChain getExecutePMReminderChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetPMAndPMReminderCommand());
		c.addCommand(new GetWOForPMReminderCommand());
		c.addCommand(new ExecutePMReminderCommand());
		return c;
	}
	
	public static FacilioChain getTemplatesOfTypeChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTemplatesOfTypeCommand());
		return c;
	}
	
	public static FacilioChain getAddTemplateChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddTemplateCommand());
		return c;
	}
	
	public static FacilioChain getReadingRulesOfFieldsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetReadingRulesFromFieldsCommand());
		return c;
	}
	
	public static FacilioChain runThroughFilters() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ExecuteAllWorkflowsCommand());
		return c;
	}
	
	public static FacilioChain getAddAlarmEMailNotifierChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddOrUpdateAlarmEMailTemplateCommand());
		c.addCommand(new UpdateAlarmCreationActionCommand());
		return c;
	}
	
	public static FacilioChain getAlarmCreationRulesChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadAlarmCreationRulesCommand());
		return c;
	}
	
	public static FacilioChain getAddAlarmSMSNotifierChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddOrUpdateAlarmSMSTemplateCommand());
		c.addCommand(new UpdateAlarmCreationActionCommand());
		return c;
	}
	
	public static FacilioChain getViewListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetViewListCommand());
		return c;
	}
	
	public static FacilioChain getAddViewChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new SetViewDefaultParameters());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new AddCVCommand());
		return c;
	}
	
	public static FacilioChain getViewDetailsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateFilterFromCriteriaCommand());		
		return c;
	}
	
	public static FacilioChain getViewsCustomizeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new CustomizeViewCommand());
		c.addCommand(new GetViewListCommand());
		return c;
	}
	
	public static FacilioChain getViewsListCustomizeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new CustomizeViewCommand());
		c.addCommand(new GetViewListCommand());
		return c;
	}
	
	public static FacilioChain getViewGroupsCustomizeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new CustomizeViewGroupsCommand());
		c.addCommand(new GetViewListCommand());
		return c;
	}
	
	
	public static FacilioChain getViewCustomizeColumnChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new CustomizeViewColumnCommand());
		return c;
	}
	
	public static FacilioChain getViewCustomizeSortColumnsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new SaveSortFieldsCommand());
		return c;
	}

	public static FacilioChain getDeleteViewCustomizeSortColumnsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteSortFieldsCommand());
		return c;
	}
	
	public static FacilioChain getUpdateFieldChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateFieldCommand());
		return c;
	}

	public static FacilioChain changeNameToLocalIdChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ChangeNameLocalIdCommand());
		return c;
	}
	
	public static FacilioChain getdeleteFieldsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteFieldsCommand());
		return c;
	}
	
	public static FacilioChain getGetFieldsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		return c;
	}
	
	public static FacilioChain getGetFieldsWithTemplateChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetFormListCommand());
		c.addCommand(new FetchFieldsTemplates());
		return c;
	}

	public static FacilioChain getGetFieldUsageChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetFormListCommand());
		c.addCommand(new FetchFieldUsageInForms());
		return c;
	}
	
	public static FacilioChain fetchExportModuleChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
//		c.addCommand(new GetExportModuleCommand());
		c.addCommand(new ExportCommand());
		return c;
	}
	

	public static FacilioChain getAllSpaceCategoriesChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;	
	}
	
	public static FacilioChain addSpaceCategoryChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static FacilioChain updateSpaceCategoryChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
//		
//		c.addCommand(new EditSpaceCategoryCommand());
//		return c;		
	}
	
	public static FacilioChain deleteSpaceCategoryChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSpaceCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
		
//		c.addCommand(new DeleteSpaceCategoryCommand());
//		return c;		
	}
	
	public static FacilioChain getUpdateReadingChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new RestrictUneditablePropsInFieldCommand());
		c.addCommand(getUpdateFieldChain());
		c.addCommand(new AddValidationRulesCommand());
		return c;
	}
	
	public static FacilioChain getAssetReadingsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetAssetSpecifcReadingsCommand());
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static FacilioChain getGetLatestAssetReadingValuesChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(getAssetReadingsChain());
		c.addCommand(new GetLatestReadingValuesCommand());
		return c;
	}
	public static FacilioChain getResourcesOccupantReadingValuesChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetResourcesLatestReadingValuesCommand());
		return c;
	}
	
	public static FacilioChain getSpaceReadingsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetSpaceSpecifcReadingsCommand());
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static FacilioChain getGetLatestSpaceReadingValuesChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(getSpaceReadingsChain());
		c.addCommand(new GetLatestReadingValuesCommand());
		return c;
	}
	
	public static FacilioChain getCategoryReadingsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetCategoryReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}

	public static FacilioChain getAllModulesWithDateField() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetModulesWithDateFieldCommand());
		return c;
	}
	
	public static FacilioChain getAllFieldsChain () {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllFieldsCommand());
		return c;
	}
	
	public static FacilioChain getAllCategoryReadingsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllCategoryReadingsCommand());
		c.addCommand(new GetAllAssetSpecificReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static FacilioChain getSiteSpecificReadingsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetSiteSpecificReadingsCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}


	public static FacilioChain getReadingsForSpaceTypeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetAllSpaceTypeReadingsCommand());
		c.addCommand(new GetReadingFieldsCommand());
		return c;
	}
	
	public static FacilioChain addResourceRDMChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetReadingFieldsCommand());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}
	
	public static FacilioChain getGetLatestReadingValuesChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(getCategoryReadingsChain());
		c.addCommand(new GetLatestReadingValuesCommand());
		return c;
	}
	
	public static FacilioChain getGetReadingValuesChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetLatestReadingValuesCommand());
		return c;
	}
	
	public static FacilioChain getAddPhotosChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddPhotosCommand());
		return c;
	}
	
	public static FacilioChain getUploadPhotosChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UploadPhotosCommand());
		c.addCommand(new AddPhotosCommand());
		c.addCommand(new AddPrerequisitePhotoActivityCommand());
		c.addCommand(new AddActivitiesCommand());
		c.addCommand(new UpdatePrerequisiteStatusCommand());
		return c;
	}
	
	public static FacilioChain getDeletePhtotosChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new DeletePhotosCommand());
		c.addCommand(new AddPrerequisitePhotoActivityCommand());
		c.addCommand(new AddActivitiesCommand());
		c.addCommand(new UpdatePrerequisiteStatusCommand());
		return c;
	}
	
	public static FacilioChain justUploadPhotosChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UploadPhotosCommand());
		return c;
	}
	
	public static FacilioChain getReadingFromImageChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetTextsFromImageCommand());
		c.addCommand(new FilterReadingsFromImageCommand());
		return c;
	}
	
	public static FacilioChain getUpdateDefaultResourcePhotoChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateDefaultResourcePhotoCommand());
		return c;
	}
	
	public static FacilioChain getDeleteDefaultResourcePhotoChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new DeleteDefaultResourcePhotoCommand());
		return c;
	}
	
	public static FacilioChain getPhotosChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetPhotosCommand());
		return c;
	}
	
	public static FacilioChain getNotesChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetNotesCommand());
		c.addCommand(new FetchCommentMentionsCommand());
		c.addCommand(new FetchCommentSharingCommand());
		c.addCommand(new FetchCommentAttachmentsCommand());
		return c;
	}
	
	public static FacilioChain getEnableMobileDashboardChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new EnableMobileDashboardCommand());
		return c;
	}
	
//	public static FacilioChain getReportData() {
//		FacilioChain c = getDefaultChain();
//		c.addCommand(new SetFieldsCommand());
//		c.addCommand(new SetCriteriaCommand());
//		c.addCommand(new SetUserFilterCommand());
//		c.addCommand(new SetGroupByCommand());
//		c.addCommand(new SetLimitAndOrderCommand());
//		c.addCommand(new FetchData());
//		c.addCommand(new ProcessResult());
//		return c;
//	}
	
	public static FacilioChain addBaseLineChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateBaseLineCommand());
		c.addCommand(new AddBaseLineCommand());
		return c;
	}
	
	public static FacilioChain getBaseLinesOfSpaceChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetBaseLinesForSpaceCommand());
		return c;
	}
	
	public static FacilioChain getAllBaseLinesChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllBaseLinesCommand());
		return c;
	}
	
	public static FacilioChain addReportBaseLinesChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddReportBaseLinesCommand());
		return c;
	}
	
	public static FacilioChain updateReportBaseLinesChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteExistingReportBaseLineCommand());
		c.addCommand(new AddReportBaseLinesCommand());
		return c;
	}
	
	public static FacilioChain deleteFormulaChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteFormulaCommand());
		return c;
	}
	
	public static FacilioChain updateReportColumnSequence() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateReportColumnSequence());
		return c;
	}
	
	public static FacilioChain getAddTicketCategoryChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getUpdateTicketCategoryChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	public static FacilioChain getDeleteTicketCategoryChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	public static FacilioChain getAddTicketPriorityChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new SetPriorityNameCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getUpdateTicketPriorityChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getUpdateAlarmSeverityChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarmSeverity());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getUpdateAlarmSeveritiesChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarmSeverity());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		return c;
	}
	
	
	public static FacilioChain getUpdateTicketPrioritiesChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getDeleteTicketPriorityChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketPriority());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	public static FacilioChain getDeleteAlarmSeverityChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarmSeverity());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	public static FacilioChain getAddTicketTypeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	public static FacilioChain getUpdateTicketTypeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getDeleteTicketTypeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTicketType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}

	public static FacilioChain getAddAssetCategoryChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new AddAssetCategoryModule());
		c.addCommand(TransactionChainFactory.commonAddModuleChain());
		c.addCommand(new UpdateCategoryAssetModuleIdCommand());
		return c;
	}

	public static FacilioChain justAddAssetCategoryChain(){
		FacilioChain chain =FacilioChain.getTransactionChain();
		chain.addCommand(new GenericAddModuleDataCommand());
		return chain;
	}
	
	public static FacilioChain getUpdateAssetCategoryChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		
		return c;
	}
	
	public static FacilioChain getDeleteAssetCategoryChain() {		
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateAssetCategoryDeletion());
		c.addCommand(SetTableNamesCommand.getForAssetCategory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getAddAssetDepartmentChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetDepartment());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getUpdateAssetDepartmentChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetDepartment());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getDeleteAssetDepartmentChain() {		
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateAssetDepartmentDeletion());
		c.addCommand(SetTableNamesCommand.getForAssetDepartment());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getAddAssetTypeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getUpdateAssetTypeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getDeleteAssetTypeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ValidateAssetTypeDeletion());
		c.addCommand(SetTableNamesCommand.getForAssetType());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain getAddAlarmSeverityChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAlarmSeverity());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		return c;
	}
	
	public static FacilioChain addModuleDataChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new SetLocalIDCommand());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new GenericAddSubModuleDataCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ConstructAddCustomActivityCommand());
		c.addCommand(new ForkChainToInstantJobCommand(false)
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
//		c.addCommand(new ExecuteAllWorkflowsCommand());

		c.addCommand(new ExecuteRollUpFieldCommand());
		c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.CUSTOM_ACTIVITY));
		return c;
	}
	
	public static FacilioChain updateModuleDataChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new GenericAddSubModuleDataCommand());

		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand(false)
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
//		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new ConstructUpdateCustomActivityCommand());
		c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.CUSTOM_ACTIVITY));
		c.addCommand(new ExecuteRollUpFieldCommand());
		return c;
	}

	public static FacilioChain getFieldsByModuleType() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetFieldsByModuleType());
		return c;
	}

	public static FacilioChain getFieldsByAccessType() {
		FacilioChain c = FacilioChain.getTransactionChain(); // Why does this needs transaction chain?
		c.addCommand(new GetFieldsByAccessType());
		return c;
	}
	
	public static FacilioChain deleteModuleDataChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new DeleteRecordRuleJobOnRecordDeletionCommand());
		c.addCommand(new ExecuteRollUpFieldCommand());
		return c;
	}
	
	public static FacilioChain calculateBenchmarkValueChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new CalculateBenchmarkValueCommand());
		return c;
	}
	
	public static FacilioChain getAllShiftsCommand() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllShiftsCommand());
		return c;
	}
	
	public static FacilioChain getAddShiftCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddShiftCommand());
		return c;
	}
	
	public static FacilioChain getUpdateShiftCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateShiftCommand());
		return c;
	}
	
	public static FacilioChain getDeleteShiftCommand() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteShiftCommand());
		return c;
	}
	
	public static FacilioChain calculateCostChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new FetchCostDataCommand());
		c.addCommand(new CalculateCostCommand());
		return c;
	}
	
	public static FacilioChain addTenantChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTenants());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new AddTenantCommand());
		c.addCommand(new AddTenantUserCommand());
		return c;
	}
	
	
	public static FacilioChain addTenantLogoChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddTenantLogoCommand());
		return c;
	}
	
	public static FacilioChain updateTenantChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateTenantCommand());
		c.addCommand(new AddTenantUserCommand());
		return c;
	}
	
	
	public static FacilioChain updatePrimaryContactChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdatePrimaryContactCommand());
		return c;
	}
	
	public static FacilioChain toggleStatusChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ToggleTenantStatusCommand());
		return c;
	}
	
	public static FacilioChain addRateCardChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddRateCardCommand());
		return c;
	}
	
	public static FacilioChain updateRateCardChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateRateCardCommand());
		return c;
	}
	
	public static FacilioChain addOfflineSyncErrorChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddOfflineSyncErrorCommand());
		return c;
	}
	
	public static FacilioChain getFormMetaChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetFormMetaCommand());
		c.addCommand(new HandleFormFieldsCommand());
		c.addCommand(new GetFormRuleFields());
		return c;
	}

	public static FacilioChain getFormFieldsChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetFormFieldsCommand());
		return c;
	}
	
	public static FacilioChain getMLModelBuildingChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetReadingsForMLCommand());
		c.addCommand(new ApplyCriteriaForMLCommand());
		c.addCommand(new GenerateMLModelCommand());
		c.addCommand(new AddReadingsForMLCommand());
		c.addCommand(new TriggerAlarmForMLCommand());
		c.addCommand(new ApplyRuleForMLCommand());
		return c;
	}
	
	public static FacilioChain addMLReadingChain(){
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddReadingsForMLCommand());
		c.addCommand(new TriggerAlarmForMLCommand());
		c.addCommand(new ApplyRuleForMLCommand());
		return c;
	}
	
	public static FacilioChain addHistoricMLReadingsChain(){
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddReadingsForMLCommand());
		return c;
	}
	
	public static FacilioChain addMLServiceChain(boolean isDefault) {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new ValidateMLServiceCommand());
		if(isDefault) {
			c.addCommand(new ConstructMLModelDetails());
		}
		c.addCommand(new ConstructReadingForMLServiceCommand());
		c.addCommand(new InitMLServiceCommand());
		c.addCommand(new UpdateJobDetailsForMLCommand());
		c.addCommand(new TriggerMLServiceJobCommand());
		return c;
	}
	
	public static FacilioChain activateMLServiceChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new ActivateMLServiceCommand());
		return c;
	}
	
	public static FacilioChain addEnergyPredictionchain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddEnergyPredictionCommand());
		return c;
	}
	
	public static FacilioChain addLoadPredictionchain(){
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddLoadPredictionCommand());
		return c;
	}
	
	public static FacilioChain enableAnomalyDetectionChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new EnableAnomalyDetectionCommand());
		return c;
	}
	public static FacilioChain addAhuOptimizationchain(){
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddAhuOptimizationCommand());
		return c;
	}
	public static FacilioChain addPressurePredictionchain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new AddPressurePredictionCommand());
		return c;
	}
	public static FacilioChain getNewInventoryChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForInventory());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static FacilioChain getPermaLinkTokenChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetPermaLinkTokenCommand());
		return c;
	}
	
	public static FacilioChain getTenantListChain() {
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenerateCriteriaFromClientCriteriaCommand());
		c.addCommand(new GetTenantListCommand());
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
		return c;
	}


	
	public static FacilioChain deleteVisitorChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForVisitor());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteVisitorInvitesChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForVisitorInvites());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteVisitorLoggingChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForVisitorLogging());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteInviteVisitorRelChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForVisitorInviteRel());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteContactsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForContacts());
		c.addCommand(new DeleteContactUsersCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteInsuranceChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForInsurance());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteWatchListChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWatchList());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteWorkPermitChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkPermit());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}


	public static FacilioChain getPermaLinkDetailsChain() {
		FacilioChain chain = FacilioChain.getTransactionChain();
		chain.addCommand(new GetPermalinkDetailsCommand());
		return chain;
	}
	
	public static FacilioChain deleteOccupantsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForOccupants());
		c.addCommand(new DeleteContactUsersCommand());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteDocumentsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteDocumentsCommand());
		return c;
	}
	
	public static FacilioChain deleteSafetyPlanChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSafetyPlan());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteHazardsChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForHazard());
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new DeleteHazardsFromAssociatedModulesCommand());
		return c;
	}
	
	public static FacilioChain deletePrecautionChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForPrecaution());
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new DeleteHazardPrecautionCommand());
		return c;
	}
	
	public static FacilioChain deleteSafetyPlanHazardChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForSafetyPlanHazards());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteHazardPrecautionListChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForHazardPrecaution());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteWorkorderHazardListChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderHazard());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteAssetHazardListChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForAssetHazard());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}
	
	public static FacilioChain deleteClientListChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForClient());
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new DeleteClientContactsCommand());
		c.addCommand(deleteClientContactChain());

		return c;
	}
	
	public static FacilioChain deletePeopleChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForPeople());
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new DeletePeopleUsersCommand());
		return c;
	}
	
	public static FacilioChain deleteVendorContactChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForVendorContact());
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new DeletePeopleUsersCommand());
		return c;
	}
	
	
	public static FacilioChain deleteTenantContactChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForTenantContact());
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new DeletePeopleUsersCommand());
		
		return c;
	}
	
	public static FacilioChain deleteEmployeeChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForEmployee());
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new DeletePeopleUsersCommand());
		
		return c;
	}
	
	public static FacilioChain deleteClientContactChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(SetTableNamesCommand.getForClientContact());
		c.addCommand(new GenericDeleteModuleDataCommand());
		c.addCommand(new DeletePeopleUsersCommand());
		
		return c;
	}
	public static FacilioChain getFloorPlanChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new getFloorPlanCommand());
		return c;
	}

	public static FacilioChain v2DeleteTenantChain () {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteTenantCommand());
		c.addCommand( new RemoveAssociatedTenantUnitCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new DeleteTenantContactsCommand());
		c.addCommand(deleteTenantContactChain());
		return c;
	}

	public static FacilioChain getEnableOauth2Chain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new EnableOauth2Command());
		return c;
	}

	public static FacilioChain getCreateAPIClient() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new CreateAPIClient());
		return c;
	}

	public static FacilioChain getListAPIClient() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ListAPIClients());
		return c;
	}

	public static FacilioChain getDeleteClient() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteClient());
		return c;
	}
	public static FacilioChain getPpmJobListChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetScheduledWoCommandV3());
		return c;
	}

	public static FacilioChain getScopedTeamAndUsersChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new GetScopedTeamAndUsersCommand());
		return c;
	}
	public static FacilioChain addUserChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddPeopleCommand());
		c.addCommand(new AddIAMUserCommand());
		c.addCommand(new AddPeopleRelatedDataCommand());
		return c;
	}
	public static FacilioChain updateUserChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdatePeopleCommand());
		c.addCommand(new UpdateIAMUserCommand());
		c.addCommand(new UpdatePeopleRelatedDataCommand());
		return c;
	}
	public static FacilioChain deleteUserChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new DeleteIAMUserCommand());
		c.addCommand(new UpdatePortalAccessCommand());
		return c;
	}
	public static FacilioChain addOrUpdatePortalUserChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new AddOrUpdatePortalUserCommand());
		return c;
	}
	public static FacilioChain revokeAppAccessChain(){
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new RevokeAppAccessCommand());
		c.addCommand(new UpdatePortalAccessCommand());
		return c;
	}
}