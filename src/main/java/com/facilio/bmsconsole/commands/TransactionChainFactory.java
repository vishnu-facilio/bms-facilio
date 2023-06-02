package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AddActivitiesCommand;
import com.facilio.agent.ConfigureAgentCommand;
import com.facilio.agent.ConfigureControllerCommand;
import com.facilio.agent.DeleteAgentCommand;
import com.facilio.agent.commands.*;
import com.facilio.agent.integration.AddIntegrationCommand;
import com.facilio.agent.integration.UpdateIntegrationCommand;
import com.facilio.agentv2.commands.*;
import com.facilio.agentv2.iotmessage.AddAndSendIotMessageCommand;
import com.facilio.agentv2.point.AddPointCommand;
import com.facilio.agentv2.point.ConfigurePointCommand;
import com.facilio.agentv2.point.EditPointCommand;
import com.facilio.agentv2.sqlitebuilder.AgentSqliteMakerCommand;
import com.facilio.banner.commands.CloseBannerCommand;
import com.facilio.bmsconsole.ModuleSettingConfig.command.AddGlimpseCommand;
import com.facilio.bmsconsole.ModuleSettingConfig.command.GetModuleSettingConfigDetailsCommand;
import com.facilio.bmsconsole.ModuleSettingConfig.command.GetModuleSettingConfigurationCommand;
import com.facilio.bmsconsole.actions.GetModuleFromReportContextCommand;
import com.facilio.bmsconsole.actions.PurchaseOrderCompleteCommand;
import com.facilio.bmsconsole.commands.data.PopulateImportProcessCommand;
import com.facilio.bmsconsole.commands.people.UpdateScopingForPeopleCommand;
import com.facilio.bmsconsole.commands.reservation.CreateExternalAttendeesCommand;
import com.facilio.bmsconsole.commands.reservation.CreateInternalAttendeesCommand;
import com.facilio.bmsconsole.commands.reservation.ValidateAndSetReservationPropCommand;
import com.facilio.bmsconsole.localization.translation.AddOrUpdateTranslationCommand;
import com.facilio.bmsconsole.workflow.rule.GetOfflineStateTransitionCommand;
import com.facilio.bmsconsole.workflow.rule.GetRegisterOrUnRegisterOfflineRecordCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsole.workflow.rule.impact.AddOrUpdateAlarmImpactCommand;
import com.facilio.bmsconsole.workflow.rule.impact.util.AlarmImpactAPI;
import com.facilio.bmsconsoleV3.commands.*;
import com.facilio.bmsconsoleV3.commands.imap.SaveMailMessageCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.UseInventoryRequestLineItemsCommandV3;
import com.facilio.bmsconsoleV3.commands.jobplan.AddJobPlanTasksForWoCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.BulkAddJobPlanTasksCommand;
import com.facilio.bmsconsoleV3.commands.quotation.AssociateQuotationTermsCommand;
import com.facilio.bmsconsoleV3.commands.quotation.DisAssociateQuotationTermsCommand;
import com.facilio.bmsconsoleV3.commands.quotation.SendQuotationMailCommand;
import com.facilio.bmsconsoleV3.commands.shift.AssignShiftToUserCommand;
import com.facilio.bmsconsoleV3.signup.employeePortalApp.AddEmployeePortalDefaultForms;
import com.facilio.bmsconsoleV3.signup.employeePortalApp.AddEmployeePortalDefaultViews;
import com.facilio.bmsconsoleV3.signup.maintenanceApp.*;
import com.facilio.cb.command.*;
import com.facilio.chain.FacilioChain;
import com.facilio.classification.command.UpdateClassificationStatusCommand;
import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.commands.AddConnectedAppCommand;
import com.facilio.connectedapp.commands.AddDefaultConnectedAppFilesCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.commands.*;
import com.facilio.delegate.command.AddOrUpdateDelegateCommand;
import com.facilio.delegate.command.DeleteUserDelegationCommand;
import com.facilio.delegate.command.SendDelegationMailCommand;
import com.facilio.elasticsearch.command.PushDataToESCommand;
import com.facilio.emailtemplate.command.AddOrUpdateEmailStructureCommand;
import com.facilio.emailtemplate.command.DeleteEmailStructureCommand;
import com.facilio.energystar.command.*;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand;
import com.facilio.events.commands.NewExecuteEventRulesCommand;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.fields.relations.CalculateDependencyCommand;
import com.facilio.mv.command.*;
import com.facilio.ns.command.DeleteRuleNamespacesCommand;
import com.facilio.permission.commands.AddOrUpdatePermissionSetsForPeopleCommand;
import com.facilio.permission.commands.DefaultPermissionSetCommand;
import com.facilio.readingkpi.commands.ExecuteSchKpiOfACategoryCommand;
import com.facilio.readingkpi.commands.FetchIntervalsAndCalculateKpiCommand;
import com.facilio.readingrule.command.DeleteReadingRuleActionsCommand;
import com.facilio.readingrule.command.DeleteReadingRuleCommand;
import com.facilio.readingrule.command.FetchRuleRootCauseCommand;
import com.facilio.readingrule.command.GetRulesForRootCauseCommand;
import com.facilio.readingrule.faultimpact.command.DeleteFaultImpactFromReadingRuleCommand;
import com.facilio.readingrule.faulttowo.command.*;
import com.facilio.relation.command.AddOrUpdateRelationCommand;
import com.facilio.relation.command.DeleteRelationCommand;
import com.facilio.storm.command.StormHistoricalProxyCommand;
import com.facilio.storm.command.StormInstructionPublishCommand;
import com.facilio.trigger.context.TriggerType;
import com.facilio.weekends.*;
import com.facilio.workflows.command.*;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class TransactionChainFactory {

	private static FacilioChain getDefaultChain() {
		return FacilioChain.getTransactionChain();
    }

		public static FacilioChain getOrgSignupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddDefaultLicenseCommand());
			c.addCommand(new AddDefaultModulesCommand());
			c.addCommand(new AddDefaultUnitsCommand());
			c.addCommand(new AddDefaultGraphicsCommand());
			c.addCommand(new AddDefaultWoStateflowCommand());
			c.addCommand(new AddEventModuleCommand());					//eventModule.sql
			c.addCommand(new AddOrgInfoCommand());
			c.addCommand(new CreateAppSuperAdminCommand());
//			c.addCommand(new AddCommonModuleWidgetsCommand());
			c.addCommand(new AddSignupDataCommandV3());
			c.addCommand(new AddEmployeeTypePeopleForUserAdditionCommand());
			c.addCommand(new AddDefaultBundleCommand());
			c.addCommand(new AddDefaultWoTimelineCommand());
//			c.addCommand(new AddMaintenanceAppConfigCommand());
			//c.addCommand(new AddDefaultWoTimelineCommand());
			c.addCommand(addMaintenanceApplication());
			c.addCommand(addEmployeePortalChain());
			c.addCommand(addScopingChain());
			c.addCommand(new AssignShiftToUserCommand());
			c.addCommand(new DefaultPermissionSetCommand());
			return c;
		}

	public static FacilioChain addMaintenanceApplication() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddMaintenanceApplicationLayout());
		c.addCommand(new AddMaintenanceApplicationDefaultViews());
		c.addCommand(new AddMaintenanceApplicationDefaultForms());
		c.addCommand(new AddDefaultRolesMaintenanceApp());
		c.addCommand(new AddMaintenanceAppRelatedApplicationsCommand());
		return c;
	}

		public static FacilioChain runDefaultFieldsMigration() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new RunDefaultFieldsMigration());
			return c;
		}

		public static FacilioChain runModuleMigrationMigration() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new RunModuleMigrationCommand());
			return c;
		}

		public static FacilioChain getpopulateDefaultChatBotIntentChain() {
			FacilioChain c = getDefaultChain();

			c.addCommand(new ParseDefaultChatBotIntentCommand());
			c.addCommand(new AddOrUpdateIntentToMLCommand());
			c.addCommand(new GetOrAddCurrentActiveModel());
			c.addCommand(new PopulateDefaultChatBotIntentCommand());
			return c;
		}

		public static FacilioChain getAddNotesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddNotesCommand());
			c.addCommand(new AddMentionsCommand());
			c.addCommand(new AddCommentSharingCommand());
			c.addCommand(new AddCommentAttachmentsCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteNoteWorkflowCommand())
					.addCommand(new NotifyCommentMentions()));
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		public static FacilioChain getUpdateNotesChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateNotesCommand());
			c.addCommand(new setUpdateNoteActivitiesCommand());
			c.addCommand(new deleteAllNoteSubordinates());
			c.addCommand(new AddMentionsCommand());
			c.addCommand(new AddCommentSharingCommand());
			c.addCommand(new AddCommentAttachmentsCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteNoteWorkflowCommand())
					.addCommand(new NotifyCommentMentions()));
			c.addCommand(new AddActivitiesCommand());

			return c;
		}

		public  static FacilioChain updateNotesSharing()
		{
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteCommentSharingCommand());
			c.addCommand(new AddCommentSharingCommand());
			c.addCommand(new UpdateNotesCommand());
			return c;
		}

		public static FacilioChain getUpdateTicketNotesChain() {
			FacilioChain c1 = getDefaultChain();
			c1.addCommand(new UpdateNotesCountCommand());
			return c1;
		}

		public static FacilioChain getUpdateTaskCountChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateTaskCountCommand());
			return c;
		}

		public static FacilioChain getUpdateAttachmentCountChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateAttachmentCountUpdateCommand());
			return c;
		}
		public static FacilioChain getAddRelationShipChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddRelationshipCommand());
			return c;
		}

		public static FacilioChain getUpdateRelationShipChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateRelationshipCommand());
			return c;
		}

		public static FacilioChain getAddRelatedAssetsChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddRelatedAssetsCommand());
			return c;
		}

		public static FacilioChain getUpdateRelatedAssetsChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateRelatedAssetsCommand());
			return c;
		}

		public static FacilioChain getDeleteRelatedAssetsChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteRelatedAssetsCommand());
			return c;
		}

		public static FacilioChain getDeleteRelationshipChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteRelationshipCommand());
			return c;
		}

		public static FacilioChain historicalFormulaCalculationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new HistoricalFormulaRunCalculationCommand());
			return c;
		}

		public static FacilioChain runThroughHistoricalRuleChain() {
			return runThroughHistoricalRuleChain(false);
		}

		public static FacilioChain runThroughHistoricalRuleChain(boolean isNewReadingRule) {
			FacilioChain c = getDefaultChain();
			if(isNewReadingRule) {
				c.addCommand(new StormHistoricalProxyCommand());
			} else {
				c.addCommand(new RunThroughHistoricalRuleCommand());
			}
			return c;
		}

		public static FacilioChain initiateStormInstructionExecChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new StormInstructionPublishCommand());
			return c;
		}
		public static FacilioChain runThroughSensorRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new RunThroughSensorRuleCommand());
			return c;
		}

//		public static FacilioChain executeSensorRuleChain() {
//			FacilioChain c = getDefaultChain();
//			c.addCommand(new ExecuteSensorRuleHistoryCommand());
//			return c;
//		}

		public static FacilioChain historicalScheduledRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddHistoricalScheduledRuleJobCommand());
			return c;
		}

		public static FacilioChain getTempAddPreOpenedWorkOrderChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new BulkPMSettingsCommand());
			c.addCommand(SetTableNamesCommand.getForWorkOrder());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new BulkAddWorkOrderCommand());
			c.addCommand(new BulkAddAttachmentRelationshipCommand());
			c.addCommand(getTempAddTaskChain());
			return c;
		}

		public static FacilioChain getBulkWorkOrderImportChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ProcessWorkOrderImportCommand());
			c.addCommand(getTempAddPreOpenedWorkOrderChain());
//			c.addCommand(getWorkOrderWorkflowsChain(false));
			return c;
		}

		public static FacilioChain getTempAddTaskChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new BulkAddJobPlanTasksCommand());
			c.addCommand(new BulkTaskUniqueIdCommand());
			c.addCommand(new BulkTaskReadingFieldCommand());
			c.addCommand(new BulkAddTaskSectionsCommand());
			c.addCommand(new BulkAddActionForTaskCommand());
			c.addCommand(new BulkAddTasksCommand());
			c.addCommand(new BulkAddTaskOptionsCommand());
			c.addCommand(new UpdateReadingDataMetaCommand());
			//c.addCommand(new BulkAddTaskDefaultValueReadingsCommand());
			c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
			c.addCommand(new BulkUpdateTaskReadingInfoCommand());
			return c;
		}

		public static FacilioChain getAddPreOpenedWorkOrderChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PMSettingsCommand());
			c.addCommand(new GetFormMetaCommand());
			c.addCommand(new ValidateFormCommand());
			c.addCommand(new AddRequesterCommand());
			c.addCommand(SetTableNamesCommand.getForWorkOrder());
			c.addCommand(new ValidateWorkOrderFieldsCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddWorkOrderCommand());
			c.addCommand(new AddAttachmentCommand());
			c.addCommand(new AttachmentContextCommand());
			c.addCommand(new AddAttachmentRelationshipCommand());
			c.addCommand(getAddNewTasksChain());
			return c;
		}


		public static FacilioChain v2AddTenantChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateTenantSpaceCommand());
			c.addCommand(SetTableNamesCommand.getForTenants());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddTenantCommand());
			c.addCommand(new AddTenantUserCommand());
			c.addCommand(new AddTenantSpaceRelationCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ConstructAddCustomActivityCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}


		public static FacilioChain v2UpdateTenantChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateTenantSpaceCommand());
			c.addCommand(new UpdateTenantCommand());
			c.addCommand(new AddTenantUserCommand());
			c.addCommand(new AddTenantSpaceRelationCommand());
			c.addCommand(new AddTenantUnitSpaceRelationCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ConstructUpdateCustomActivityCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain v2fetchTenantDetails() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTenants());
			c.addCommand(new AssociateTenantToContactCommand());
			c.addCommand(new GetTenantDetailCommand());
			c.addCommand(new SetTenantSpaceAndContactsCommand());
			c.addCommand(new LookupPrimaryFieldHandlingCommand());
			return c;
		}


		public static FacilioChain v2disassociateSpaceChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DisassociateTenantSpaceRelationCommand());
			c.addCommand(new DisassociateTenantUnitSpaceRelationCommand());

			return c;
		}

		public static FacilioChain getWorkOrderWorkflowsChain(boolean sendNotification) {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand (new ExecuteAllWorkflowsCommand (RuleType.SATISFACTION_SURVEY_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SURVEY_ACTION_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_CUSTOM_CHANGE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
//			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ExecuteSLAWorkFlowsCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));

			c.addCommand(new ExecuteAllTriggersCommand(TriggerType.MODULE_TRIGGER));
			c.addCommand(new ExecuteScoringRulesCommand());

			if (sendNotification) {
				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 218L) {
					c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION));
				} else {
					c.addCommand(new ForkChainToInstantJobCommand(false)
							.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION)));
				}
			}
			return c;
		}
		public static FacilioChain getAddWorkOrderChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PMSettingsCommand());
			//c.addCommand(new GetFormMetaCommand());
			//c.addCommand(new ValidateFormCommand());
			c.addCommand(new AddRequesterCommand());
			c.addCommand(SetTableNamesCommand.getForWorkOrder());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new ValidateWorkOrderFieldsCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new UpdateEventListForStateFlowCommand());
			c.addCommand(new AddWorkOrderCommand());
			c.addCommand(new GenericAddSubModuleDataCommand());
			c.addCommand(new AddAttachmentCommand());
			c.addCommand(new AttachmentContextCommand());
			c.addCommand(new AddAttachmentRelationshipCommand());
			c.addCommand(new AddTicketActivityCommand());
			c.addCommand(getAddTasksChain());
			c.addCommand(new AddPrerequisiteApproversCommand());
			c.addCommand(new FacilioCommand() {
				@Override
				public boolean executeCommand(Context context) throws Exception {
					context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
					context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(context.get(FacilioConstants.ContextNames.WORK_ORDER)));
					return false;
				}
			});
			c.addCommand(getWorkOrderWorkflowsChain(true));
			c.addCommand(new AddOrUpdateSLABreachJobCommand(true));
			c.addCommand(new AddActivitiesCommand());
			c.addCommand(new PushDataToESCommand());
			return c;
		}

		public static FacilioChain getAddActivitiesCommand() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(new AddActivitiesCommand());
			return chain;
		}
		public static FacilioChain getAddNewTasksChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new ValidateNewTasksCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddTaskSectionsCommand());
			c.addCommand(new AddActionForTaskCommand());
			c.addCommand(new AddTasksCommand());
			c.addCommand(new AddTaskOptionsCommand());
			c.addCommand(new UpdateReadingDataMetaCommand());
			c.addCommand(new AddTaskDefaultValueReadingsCommand());
			c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
			c.addCommand(new UpdateTaskReadingInfoCommand());
			// c.addCommand(new AddTaskTicketActivityCommand());
//			c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
			return c;
		}

		public static FacilioChain getAddTasksChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new AddJobPlanTasksForWoCommand());
			c.addCommand(new ValidateTasksCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddTaskSectionsCommand());
			c.addCommand(new AddActionForTaskCommand());
			c.addCommand(new AddTasksCommand());
			c.addCommand(new AddTaskOptionsCommand());
			c.addCommand(new UpdateReadingDataMetaCommand());
			// c.addCommand(new AddTaskDefaultValueReadingsCommand());
			c.addCommand(new UpdateTaskReadingInfoCommand());
			// c.addCommand(new AddTaskTicketActivityCommand());
//			c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

	/**
	 * Creates a new Workorder
	 *
	 * @deprecated use V3 Update instead.
	 */
	@Deprecated
	public static FacilioChain getUpdateWorkOrderChain() throws Exception {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new FetchOldWorkOrdersCommand());
		c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
		c.addCommand(new VerifyApprovalCommand());
		c.addCommand(new UpdateEventListForStateFlowCommand());
		c.addCommand(new AddRequesterCommand());
		c.addCommand(new UpdateWorkOrderCommand());
		c.addCommand(new BackwardCompatibleStateFlowUpdateCommand());
		c.addCommand(new ToVerifyStateFlowTransitionForStart());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new SendNotificationCommand());
		c.addCommand(new UpdateTransactionEventTypeCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SATISFACTION_SURVEY_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SURVEY_ACTION_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.TRANSACTION_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
		c.addCommand(new AddOrUpdateSLABreachJobCommand(false));
		c.addCommand(new ExecuteSLACommitmentWorkflowsCommand());
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ExecuteSpecificWorkflowsCommand(RuleType.CUSTOM_BUTTON));
		c.addCommand(new ExecuteAllTriggersCommand(TriggerType.MODULE_TRIGGER));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION));
		c.addCommand(new ClearAlarmOnWOCloseCommand());
		c.addCommand(new ExecuteTaskFailureActionCommand());
		c.addCommand(new ConstructTicketNotesCommand());
		c.addCommand(getAddNotesChain());
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		c.addCommand(new AddAttachmentRelationshipCommand());
		c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain addOrUpdateReadingReportChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateReadingAnalyticsReportCommand());
			c.addCommand(new AddOrUpdateReportCommand());
			return c;
		}

		public static FacilioChain addOrUpdateReportChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateReportCommand());
			return c;
		}

		public static FacilioChain addWorkOrderReportChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateWorkOrderAnalyticsReportCommand());
			c.addCommand(new AddOrUpdateReportCommand());
			return c;
		}

		public static FacilioChain getAddPhotoChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UploadPhotosCommand());
			return c;
		}
		public static FacilioChain getEditControllerChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FetchControllerSettingCommand());
			c.addCommand(new EditControllerSettingsCommand());
			c.addCommand(new PublishControllerPropertyToIoTCommand());
			return c;
		}

		public static FacilioChain getDeleteControllerChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteControllerCommand());
			return c;
		}

		public static FacilioChain readingToolsDeltaCalculationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ReadingToolCommand());
			return c;
		}

		public static FacilioChain readingToolsDuplicateRemoveChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ReadingToolsDuplicateRemoveCommand());
			return c;
		}

		public static FacilioChain demoRollUpChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DemoRollUpCommand());
			return c;
		}

		public static FacilioChain demoRollUpYearlyChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DemoRollUpYearlyCommand());
			return c;
		}

		public static FacilioChain demoSingleRollUpYearlyChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DemoSingleRollUpYearlyCommand());
			return c;
		}

		public static FacilioChain demoAlarmPropagationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DemoAlarmPropagationCommand());
			return c;
		}

		public static FacilioChain demoRollUpOneTimeJobChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DemoRollUpOneTimeJobCommand());
			return c;
		}

		public static FacilioChain deleteMessageQueueChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteMessageQueueJobsCommand());
			return c;
		}

		public static FacilioChain openUnOpenedPMs() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new OpenUnOpenedWos());
			return c;
		}

		public static FacilioChain deltaCalculationChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AdminDeltaCalculationCommand());
			return c;
		}

		public static FacilioChain removeDuplicatesChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AdminRemoveDuplicationCommand());
			return c;
		}

		public static FacilioChain editAgent(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConfigureAgentCommand());
			return c;
		}

		public static FacilioChain updateAgentTable(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateAgentTableCommand());
		return c;
		}

		public static FacilioChain updateAgent(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateAgentDetailsCommand());
		return c;
		}

        public static FacilioChain getUpdateQueryChain(){
	    FacilioChain c = getDefaultChain();

	    return c;
        }

        public static FacilioChain getUpdateAgentMessageChain(){
	    FacilioChain c = getDefaultChain();
	    c.addCommand(new UpdateAgentMessageCommand());
	    return c;
        }

        public static FacilioChain getAddAgentMessageChain(){
	    FacilioChain c = getDefaultChain();
	    c.addCommand(new AddAgentMessageCommand());
        return c;
        }

        public static FacilioChain getAddAgentMetricsChain(){
	    FacilioChain c = getDefaultChain();
	    c.addCommand(new AddAgentMetricsCommand());
	    return c;
        }

        public static FacilioChain getUpdateAgentMetricsChain(){
	    FacilioChain c = getDefaultChain();
	    c.addCommand(new UpdateAgentMetricsCommand());
	    return c;
        }

        public static FacilioChain addLogChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddLogChainCommand());
			return c;
		}

	public static FacilioChain getAgentEditChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AgentEditCommand());
		return c;
	}

	public static FacilioChain getAddAgentChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AgentAddCommand());
		return c;
	}

	public static FacilioChain deleteAgent() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteAgentCommand());
		return c;
	}

	public static FacilioChain deleteAgentV2() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteAgentJobCommand());
		c.addCommand(new DeleteAgentV2Command());
		return c;
	}

	public static FacilioChain updateAckChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AckUpdateCommand());
		return c;
		}

		public static FacilioChain addNewAgent(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddAgentCommand());
		return c;
		}

		public static FacilioChain createWattChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddIntegrationCommand());
		return c;
		}
		public static FacilioChain updateWattStatusChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateIntegrationCommand());
		return c;
		}

		public static FacilioChain controllerEdit(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConfigureControllerCommand());
			return c;
		}
		public static FacilioChain updateScheduledReportsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddTemplateCommand());
			c.addCommand(new DeleteScheduledReportsCommand(true));
			c.addCommand(new ScheduleV2ReportCommand());
			return c;
		}

		public static FacilioChain deleteScheduledReportsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteScheduledReportsCommand(true));
			return c;
		}

		public static FacilioChain getExportModuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExportCommand());
			return c;
		}

		public static FacilioChain getExportPointsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GenerateCriteriaFromFilterCommand());
			c.addCommand(new ExportPointsCommand());
			return c;
		}


		public static FacilioChain sendModuleMailChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(FacilioChainFactory.fetchExportModuleChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}

		public static FacilioChain addWorkflowRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddWorkflowRuleCommand());
			c.addCommand(new AddActionsForWorkflowRule());
			c.addCommand(new AddOrUpdateJobEntryForScheduledReadingRuleCommand());
			return c;
		}

		public static FacilioChain ValidateAndaddWorkflowRuleChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateTransactionRuleCommand());
			c.addCommand(addWorkflowRuleChain());
			return c;
		}
	public static FacilioChain getDelWorkflowRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetWorkflowRuleCommand());
		chain.addCommand(new WorkflowRuleDeleteCommand());
		return chain;
	}

		public static FacilioChain addApprovalRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConstructApprovalRuleCommand());
			c.addCommand(addWorkflowRuleChain());
			c.addCommand(new AddApproverActionRelCommand());
			return c;
		}

		public static FacilioChain getAddScheduledActionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddActionsForWorkflowRule());
			c.addCommand(new AddScheduledActionCommand());
			return c;
		}

		public static FacilioChain getUpdateScheduledActionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new EditScheduledActionCommand());
			return c;
		}

		public static FacilioChain editViewChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetDBViewCommand());
			c.addCommand(new CheckEditAccessCommand());
			c.addCommand(new GenerateCriteriaFromFilterCommand());
			c.addCommand(new AddCVCommand());
			return c;
		}
		public static FacilioChain deleteViewChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetDBViewCommand());
			c.addCommand(new CheckEditAccessCommand());
			c.addCommand(new DeleteViewCommand());
			return c;
		}

		public static FacilioChain getViewFromIdChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetDBViewCommand());
			return c;
		}
		public static FacilioChain updateWorkflowRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateWorkflowRuleCommand());
			c.addCommand(new DeleterOldRuleActionsCommand());
			c.addCommand(new AddActionsForWorkflowRule());
			c.addCommand(new AddOrUpdateJobEntryForScheduledReadingRuleCommand());
			return c;
		}

	public static FacilioChain ValidateAndupdateWorkflowRuleChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateTransactionRuleCommand());
		c.addCommand(updateWorkflowRuleChain());
		return c;
	}

		public static FacilioChain addAlarmRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddAlarmRuleCommand());
			c.addCommand(new AddActionForAlarmRuleCommand());
			c.addCommand(new AddOrUpdateJobEntryForScheduledReadingRuleCommand());
			c.addCommand(new AddReadingAlarmRuleCommand());
			return c;
		}

		public static FacilioChain deleteReadingRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteReadingRuleCommand());
			c.addCommand(new DeleteRuleNamespacesCommand());
			c.addCommand(new DeleteWorkflowCommand());
            c.addCommand(new DeleteReadingRuleActionsCommand());
			return c;
		}

		public static FacilioChain fetchRcaRules() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetRulesForRootCauseCommand());
			return c;
		}
		public static FacilioChain addReadingAlarmRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddReadingAlarmRuleCommand());
			return c;
		}

		public static FacilioChain fetchRootCause() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FetchRuleRootCauseCommand());
			return c;
		}

		public static FacilioChain addControlGroupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddControlGroupCommand());
			c.addCommand(new AddControlGroupSpaceCommand());
			c.addCommand(new AddControlGroupInclExclCommand());
			return c;
		}

		public static FacilioChain addOrDeleteFaultImpactChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteFaultImpactFromReadingRuleCommand());
			c.addCommand(new AddFaultImpactRelationCommand());
			return c;
		}

		public static FacilioChain getImpactForRuleChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(new GetImpactForNewRuleCommand());
			return chain;
		}


		public static FacilioChain updateControlGroupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateControlGroupCommand());
			c.addCommand(new AddControlGroupSpaceCommand());
			c.addCommand(new AddControlGroupInclExclCommand());
			return c;
		}

		public static FacilioChain deleteControlGroupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteControlGroupCommand());
			return c;
		}

		public static FacilioChain updateReadingAlarmRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateReadingAlarmRuleCommand());
			return c;
		}

		public static FacilioChain updateReadingDataMetaChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateRDMCommand());
			return c;
		}

		public static FacilioChain getDeleteWorkflowRuleChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new DeleteWorkflowRuleCommand());
			return c;
		}

		public static FacilioChain updateAlarmRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateAlarmRuleCommand());
			c.addCommand(new AddActionForAlarmRuleCommand());
			c.addCommand(new AddOrUpdateJobEntryForScheduledReadingRuleCommand());
			c.addCommand(new AddReadingAlarmRuleCommand());
			return c;
		}

		public static FacilioChain updateVersionedWorkflowRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateWorkflowRuleCommand());
			c.addCommand(addWorkflowRuleChain());
			return c;
		}

		public static FacilioChain updateApprovalRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConstructApprovalRuleCommand());
			c.addCommand(updateVersionedWorkflowRuleChain());
			c.addCommand(new AddApproverActionRelCommand());
			return c;
		}

		public static FacilioChain getAddAssetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new SetAssetCategoryCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMF());
			c.addCommand(new ValidateQrValueCommand());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ConstructAddAssetActivityCommand());
			c.addCommand(new AddRotatingItemToolCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ForkChainToInstantJobCommand(false)
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
	//		c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(FacilioChainFactory.getCategoryReadingsChain());
			c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
			c.addCommand(new AddActivitiesCommand());
			c.addCommand(new PushDataToESCommand());
			return c;
		}

		public static FacilioChain getTagAssetASRotatingChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddRotatingItemToolCommand());
			return c;
		}

		public static FacilioChain getCommonExecuteWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ForkChainToInstantJobCommand(false)
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			return c;
		}

		public static FacilioChain getExecuteWorkflowByTypeChain(RuleType ruleType) {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecuteAllWorkflowsCommand(ruleType));
			return c;
		}

		public static FacilioChain getExecuteSLARulesChain(Boolean isAdd) {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(new ExecuteSLACommitmentWorkflowsCommand());
			chain.addCommand(new AddOrUpdateSLABreachJobCommandV3(isAdd));
			return chain;
		}

		public static FacilioChain getUpdateAssetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddCategoryOnAssetUpdateCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMF());
			c.addCommand(new ValidateQrValueCommand());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new UpdateEventListForStateFlowCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new ConstructUpdateCustomActivityCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ForkChainToInstantJobCommand(false)
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
	//		c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(FacilioChainFactory.getCategoryReadingsChain());
			c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}


		public static FacilioChain getUpdateQrChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateQrCommand());
			return c;
		}

		public static FacilioChain getImportChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ProcessImportCommand());
			c.addCommand(new SwitchToAddResourceChain());
			c.addCommand(new ImportFieldRollupCommand());
			return c;
		}


		public static FacilioChain getImportPointsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ProcessImportPointsCommand());
			c.addCommand(new SwitchToAddResourceChain());
			return c;
		}

		public static FacilioChain parseReadingDataForImport() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ParseDataForReadingLogsCommand());
			c.addCommand(new InsertImportDataIntoLogCommand());
			return c;
		}

		public static FacilioChain parseImportData() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GenericParseDataForImportCommand());
			c.addCommand(new SpecificValidationCheckForImportCommand());
			c.addCommand(new InsertImportDataIntoLogCommand());
			return c;
		}


		public static FacilioChain parseImportPointsData() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PointsParseDataForImportCommand());
			c.addCommand(new GenericImportDataIntoPointsLogCommand());
			return c;
		}

		public static FacilioChain getImportReadingChain() {
			FacilioChain c = getDefaultChain();

			c.addCommand(new ConstructVirtualSheetForReadingsImport());
			c.addCommand(new InsertReadingCommand());
			c.addCommand(new WriteSkippedToFileCommand());
			c.addCommand(new SendEmailCommand());
			return c;
		}


		public static FacilioChain getGenericImportChain() {
			FacilioChain c= getDefaultChain();
			c.addCommand(new PopulateImportProcessCommand());
			c.addCommand(new UpdateBaseAndResourceCommand());
			c.addCommand(new UpdateResourceForSiteImportCommand());
			c.addCommand(new InsertReadingDataMetaForImport());
			c.addCommand(TransactionChainFactory.getbimImportUpdateChain());
			c.addCommand(new SendEmailCommand());
			return c;
		}

		public static FacilioChain getBulkAssetImportChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new SeperateToCategoriesCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new BulkPushAssetCommands());
			c.addCommand(new UpdateBaseAndResourceCommand());
			c.addCommand(new InsertReadingDataMetaForImport());
			c.addCommand(TransactionChainFactory.getbimImportUpdateChain());
			c.addCommand(new SendEmailCommand());
			return c;
		}

		public static FacilioChain scheduledRuleExecutionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FetchScheduledRuleMatchingRecordsCommand());
			c.addCommand(new ExecuteSingleWorkflowRuleCommand());
			return c;
		}

		public static FacilioChain getScheduledRuleJobsExecutionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DisableActiveScheduledRuleMetaJobsCommand());
			c.addCommand(new FetchScheduledRuleMatchingRecordsCommand());
			c.addCommand(new CreateScheduleRuleJobsCommand());
			return c;
		}

		public static FacilioChain executeScheduledRuleJobChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FetchSingleMatchingRecordScheduledRuleCommand());
			c.addCommand(new ExecuteSingleWorkflowRuleCommand());
			c.addCommand(new UpdateScheduleRuleJobMetaCommand());
			return c;
		}

		public static FacilioChain previousRecordRuleExecutionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FetchPreviousRecordsRuleMatchingRecordsCommand());
			c.addCommand(new ExecuteSingleWorkflowRuleCommand());
			return c;
		}

		public static FacilioChain recordRuleExecutionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FetchRuleMatchingSpecificRecordCommand());
			c.addCommand(new ExecuteSingleWorkflowRuleCommand());
			return c;
		}

		public static FacilioChain controllerActivityAndWatcherChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateCheckPointAndAddControllerActivityCommand());
			c.addCommand(new CheckAndStartWatcherCommand());
			return c;
		}

		public static FacilioChain getAddAlarmFromEventChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ProcessAlarmCommand());
			c.addCommand(getAddAlarmChain());
			return c;
		}
		public static FacilioChain addBusinessHourChain () {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new AddBusinessHourCommand());
			c.addCommand(new AddSingleDayBusinessHourCommand());
			c.addCommand(new UpdateBusinessHourInResourceCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		public static FacilioChain updateBusinessHoursChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new UpdateBusinessHoursCommand());
			c.addCommand(new DeleteSingleDayBusinessHoursCommand());
			c.addCommand(new AddSingleDayBusinessHourCommand());
			// c.addCommand(new AddHistoricalOperationalAlarmCommand());
			return c;
		}
		public static FacilioChain updateBusinessHourInResourceChain () {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new UpdateBusinessHourInResourceCommand());
			c.addCommand(new AddActivitiesCommand());
			c.addCommand(new AddHistoricalOperationalAlarmCommand());
			return c;
		}
		public static FacilioChain deleteBusinessHoursChain () {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new DeleteBusinessHourCommand());
			c.addCommand(new DeleteSingleDayBusinessHoursCommand());
			return c;
		}



		public static FacilioChain getAddAlarmChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForAlarm());
			c.addCommand(new AddAlarmCommand());
			c.addCommand(new AddMLOccurrenceCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.REPORT_DOWNTIME_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.READING_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.CONTROL_ACTION_READING_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.MODULE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.PM_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false, RuleType.ALARM_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION));
//			c.addCommand(new AddAlarmFollowersCommand());
			c.addCommand(new ConstructTicketNotesCommand());
			c.addCommand(getAddNotesChain());
			return c;
		}

		public static FacilioChain updateAlarmFromJsonChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ProcessAlarmCommand());
			c.addCommand(getUpdateAlarmChain());
			return c;
		}

		public static FacilioChain getUpdateAlarmChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForAlarm());
			c.addCommand(new UpdateAlarmCommand());
			c.addCommand(new AddMLOccurrenceCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.MODULE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.PM_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.READING_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.CONTROL_ACTION_READING_ALARM_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ALARM_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION))
				.addCommand(new AddClearCommentInWoOnAlarmClearCommand())
			);
			c.addCommand(new ConstructTicketNotesCommand());
			c.addCommand(getAddNotesChain());
			c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.ALARM_ACTIVITY));
			return c;
		}

		public static FacilioChain getAddWoFromAlarmChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddWOFromAlarmCommand());
			c.addCommand(getAddWorkOrderChain());
			c.addCommand(new UpdateWoIdInAlarmCommand());
			c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.ALARM_ACTIVITY));
			return c;
		}

		public static FacilioChain addSystemModuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddDefaultFieldsForSystemModulesCommand());
			c.addCommand(commonAddModuleChain());
			return c;
		}


		public static FacilioChain getAddModuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateCustomModuleCommand());
			c.addCommand(new AddSystemFieldsCommand());
			c.addCommand(commonAddModuleChain());
			c.addCommand(new AddCustomModuleDataFailureClassModuleRelationship());
			c.addCommand(new AddDefaultFormForCustomModuleCommand());
			c.addCommand(new AddDefaultStateFlowCommand());
			c.addCommand(new AddSubModulesSystemFieldsCommad());
			c.addCommand(new CreateDefaultViewCommand());
			c.addCommand(commonAddModuleChain());
//			c.addCommand(new CreateDefaultAndTemplatePageCommand());
//			c.addCommand(new CreateCustomModuleDefaultSubModuleCommand());
			return c;
		}

		public static FacilioChain getUpdateModuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateModuleCommand());
			return c;
		}

		public static FacilioChain getModuleSettingChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetModuleSettingCommand());
			c.addCommand(new GetModuleSettingConfigDetailsCommand());
			return c;
		}

		public static FacilioChain getModuleSettingConfigurationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetModuleSettingConfigurationCommand());
			return c;
		}
		public static FacilioChain getUpdateModuleSettingChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateModuleSettingCommand());
			return c;
		}

		public static FacilioChain getUpdateFeatureLockChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateFeatureLockCommand());
			return c;
		}

		public static FacilioChain getModuleRecordsDuplicateChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(ReadOnlyChainFactory.fetchModuleDataDetailsChain());
			c.addCommand(new RecordsDuplicateCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
			c.addCommand(new ModuleBasedSpecificCommand());
			c.addCommand(new ConstructActivityForBulkRecordsCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain insertReadingDataMetaForBulkResourceDuplication() {
			FacilioChain c = getDefaultChain();
			c.addCommand(FacilioChainFactory.getCategoryReadingsChain());
			c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
			return c;
		}

		public static FacilioChain getAddReadingsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateReadingModulesCommand());
			c.addCommand(commonAddModuleChain());
			return c;
		}

		public static FacilioChain commonAddModuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddModulesCommand());
//			c.addCommand(new SetColumnNameForNewCFsCommand());
			c.addCommand(new AddFieldsCommand());
			return c;
		}

		public static FacilioChain addDefaultSystemFields() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddDefaultSystemFieldsCommand());
			return c;
		}

		public static FacilioChain getAddCategoryReadingChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetCategoryModuleCommand());
			c.addCommand(getAddReadingsChain());
			c.addCommand(new AddCategoryReadingRelCommand());
			c.addCommand(new GetCategoryResourcesCommand());
			c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
			c.addCommand(new SetValidationRulesContextCommand());
			c.addCommand(new AddValidationRulesCommand());
			return c;
		}

		public static FacilioChain addResourceReadingChain() {
			return addResourceReadingChain(false);
		}

		public static FacilioChain addResourceReadingChain(boolean isModuleAlreadyCreated) {
			FacilioChain c = getDefaultChain();
			if(!isModuleAlreadyCreated){
				c.addCommand(getAddReadingsChain());
			}
			c.addCommand(new AddResourceReadingRelCommand());
			c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
			return c;
		}
	public static FacilioChain addMlReadingChain(boolean isModuleAlreadyCreated) {
		FacilioChain c = getDefaultChain();
		if(!isModuleAlreadyCreated){
			c.addCommand(getAddReadingsChain());
		}
		c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
		return c;
	}

		public static FacilioChain getAddFieldsChain() {
			FacilioChain c = getDefaultChain();
//			c.addCommand(new SetColumnNameForNewCFsCommand());
			c.addCommand(new AddFieldsCommand());
			return c;
		}

		public static FacilioChain getAddRollUpFieldsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateRollUpFieldDependenciesCommand());
			c.addCommand(new AddFieldsCommand());
			c.addCommand(new AddRollUpFieldsCommand());
			return c;
		}

		public static FacilioChain getUpdateRollUpFieldsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateFieldCommand());
			c.addCommand(new UpdateRollUpFieldsCommand());
			return c;
		}

		public static FacilioChain getSubModuleLookUpFieldsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FetchSubModuleLookUpFields());
			return c;
		}

		public static FacilioChain getRollUpFieldsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetRollUpFieldsCommand());
			return c;
		}

		public static FacilioChain addFormulaFieldChain() {
			return addFormulaFieldChain(false);
		}

		public static FacilioChain addFormulaFieldChain(boolean isModuleAlreadyCreated) {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateFormulaFieldDependenciesCommand());
			c.addCommand(new GetCategoryModuleCommand());
			c.addCommand(new SetFormulaReadingsTableNameCommand());
			if(!isModuleAlreadyCreated) {
				c.addCommand(getAddReadingsChain());
			}
			c.addCommand(new AddResourceReadingRelCommand());
			c.addCommand(new AddCategoryReadingRelCommand());
			c.addCommand(new GetCategoryResourcesCommand());
			c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
			c.addCommand(new AddFormulaFieldCommand());
			c.addCommand(new SetValidationRulesContextCommand());
			c.addCommand(new AddValidationRulesCommand());
			c.addCommand(new AddKPIViolationRuleCommand());
			c.addCommand(new SetFormulaFieldResourceStatusCommand());
			c.addCommand(new ConstructFormulaDependenciesCommand());
			return c;
		}

		public static FacilioChain updateFormulaChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateFormulaCommand());
			c.addCommand(new AddValidationRulesCommand());
			//c.addCommand(new AddKPIViolationRuleCommand());
			c.addCommand(new SetFormulaFieldResourceStatusCommand(true));
			c.addCommand(new ConstructFormulaDependenciesCommand());
			return c;
		}

		public static FacilioChain onlyAddOrUpdateReadingsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetReadingDataMetaCommand());
			c.addCommand(new ReadingUnitConversionCommand());
			c.addCommand(new EnergyDataDeltaCalculationCommand());
			c.addCommand(new CalculateDeltaCommand());
			c.addCommand(new CalculateDependencyCommand());
			c.addCommand(new CalculateAggregatedEnergyConsumptionCommand());
			c.addCommand(new CalculatePreFormulaCommand());
			c.addCommand(new ExecuteValidationRule());
			c.addCommand(new AddOrUpdateReadingValuesCommand());
			c.addCommand(new ValidateAndSetResetCounterMetaCommand());
			c.addCommand(new AddResetCounterMetaCommand());
			c.addCommand(new AddMarkedReadingValuesCommand());
//			c.addCommand(new SpaceBudIntegrationCommand());	//For RMZ-SpaceBud com.facilio.agentIntegration
			c.addCommand(new ReadingUnitConversionToDisplayUnit());
			return c;
		}


	public static FacilioChain getAddOrUpdateKPICommand() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddOrUpdateKPICommand());
		return c;
	}


	public static FacilioChain getDeleteKPICommand() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteKPICommand());
		return c;
	}

	public static FacilioChain getPMReadingCorrectionChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidatePMReadingCorrection());
		c.addCommand(new AddPMCorrectiveReadingsContext());
		c.addCommand(new UpdateTaskInputValuesCommand());
		c.addCommand(new ReadingUnitConversionToRdmOrSiUnit());
		c.addCommand(TransactionChainFactory.onlyAddOrUpdateReadingsChain());
		c.addCommand(new AddTaskReadingCorrectionActivityCommand());
		c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.WORKORDER_ACTIVITY));
		return c;
	}


		public static FacilioChain getUpdateTaskChain() {
			FacilioChain c = getDefaultChain();
//			c.addCommand(new ToVerifyStateFlowForParticularRecord());
			c.addCommand(new ValidatePrerequisiteStatusForTaskUpdateCommnad());
			c.addCommand(new VerifyApprovalCommand());
			c.addCommand(new ValidateAndCreateValuesForInputTaskCommand());
			c.addCommand(new HandleReadingMeterResetValidations());
			c.addCommand(new InValidateAndClearTaskReading());
			c.addCommand(new ValidateReadingInputForTask());
			c.addCommand(new ReadingUnitConversionToRdmOrSiUnit());
			c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
			c.addCommand(new ReadingUnitConversionToSiUnit());
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new UpdateTaskCommand());
			c.addCommand(new UpdateClosedTasksCounterCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new AddActivitiesCommand());
			c.addCommand(new UpdateRdmWithLatestInputUnit());
			c.addCommand(new SiUnitConversionToEnteredReadingUnit());
//			c.addCommand(getAddOrUpdateReadingValuesChain());
			return c;
		}

		public static FacilioChain getDeleteTaskAttachmentChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteAttachmentCommand());
			c.addCommand(new AddActivitiesCommand());


			return c;
		}

	public static FacilioChain getUpdatePreRequestChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateTaskCommand());
		c.addCommand(new AddActivitiesCommand());
		c.addCommand(new SiUnitConversionToEnteredReadingUnit());
		return c;
	}
		public static FacilioChain getProcessDataChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ProcessDataCommand());
			c.addCommand(new ModeledDataCommand());
			c.addCommand(new UnModeledDataCommand());
			c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
			return c;
		}

	public static FacilioChain getTimeSeriesProcessChainV2() throws Exception {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateDataCommandStatus());
		chain.addCommand(new ProcessDataCommandV2());
		chain.addCommand(new UpdateLastRecordedValueAndFilterPointsCommand());
		chain.addCommand(new ModeledDataCommandV2());
		chain.addCommand(new UnModeledDataCommandV2());
		chain.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
		return chain;
	}

	public static FacilioChain getAddCustomDataChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ProcessCustomDataCommand());
		chain.addCommand(new AddCustomDataCommand());

		return chain;
	}

	public static FacilioChain addModuleBulkDataChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GenericAddModuleDataListCommand());
		chain.addCommand(new ExecuteAllWorkflowsCommand());
		return chain;
	}

		public static FacilioChain getProcessHistoricalDataChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new HistoricalReadingsCommand());
			c.addCommand(new BulkModeledReadingCommand());
			c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
			return c;
		}

		public static FacilioChain getInstanceAssetMappingChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new InstanceAssetMappingCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new RecommendedRuleCommand()));
			return c;
		}
		public static FacilioChain updateAutoCommissionCommand() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AutoCommissionCommand());
//			c.addCommand(new updateControllerDataCommand());
			return c;
		}


		public static FacilioChain getMarkUnmodeledInstanceChain() {
			FacilioChain c = getDefaultChain();
//			c.addCommand(new MarkUnmodeledInstanceCommand());
			c.addCommand(new PublishConfigMsgToIoTCommand());
			return c;
		}

		public static FacilioChain getSubscribeInstanceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new SubscribeInstanceIoTCommand());
			return c;
		}

		public static FacilioChain getUnSubscribeInstanceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UnsubscribeInstanceIoTCommand());
			return c;
		}

		public static FacilioChain discoverControllerChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DiscoverControllerIoTCommand());
			return c;
		}

		public static FacilioChain getChangeNewPreventiveMaintenanceStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ChangePreventiveMaintenanceStatusCommand());
			c.addCommand(new DeletePMAndDependenciesCommand(false, true));
			c.addCommand(new AddPMTriggerCommand(true));
			c.addCommand(new AddPMReminderCommand(true));
			c.addCommand(new SetMissingRelInResourcePlannersCommand());
			c.addCommand(new CreateTaskSectionTriggerRelCommand());
			c.addCommand(new AddPMRelFieldsCommand(true));
			c.addCommand(new BlockPMEditOnWOGeneration(true, true, false));
			c.addCommand(new SchedulePreOpenWOCreateCommand(true, true));
			c.addCommand(new SchedulePreOpenWODeleteCommand(true));
			return c;
		}

		public static FacilioChain getChangeNewPreventiveMaintenanceStatusChainForMig() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ChangePreventiveMaintenanceStatusCommand());
			c.addCommand(new DeletePMAndDependenciesCommand(false, true));
			c.addCommand(new AddPMTriggerCommand(true));
			c.addCommand(new AddPMReminderCommand(true));
			c.addCommand(new SetMissingRelInResourcePlannersCommand());
			c.addCommand(new AddPMRelFieldsCommand(true));
			c.addCommand(new ScheduleNewPMCommand(true));
			return c;
		}

		public static FacilioChain getPMMigration(long pmId) {
			FacilioChain c = getDefaultChain();
			c.addCommand(getChangeNewPreventiveMaintenanceStatusChainForMig());
			c.addCommand(new ResetContext(pmId));
			c.addCommand(getChangeNewPreventiveMaintenanceStatusChainForMig());
			return c;
		}

		public static FacilioChain getNewExecutePreventiveMaintenanceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PreparePMForMultipleAsset());
			c.addCommand(new ExecutePMCommand());
			c.addCommand(new ResetNewTriggersCommand());
			return c;
		}

		public static FacilioChain getExecutePMsChain() {		// from Bulk Execute
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecutePMsCommand());
			c.addCommand(new SchedulePostPMRemindersCommandForBulkExecutePm());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getMigrateReadingDataChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new MigrateReadingDataCommand());
			return c;
		}

		public static FacilioChain getSetReadingInputValuesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new SetReadingInputValuesCommand());
			return c;
		}

		public static FacilioChain getResetReadingsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateResetReadingsCommand());
			c.addCommand(new UpdateResetCounterMetaCommand());
			c.addCommand(new GetResetCounterMetaCommand());
			c.addCommand(new CalculateDeltaInReadingResetCommand());
			c.addCommand(new UpdateDeltaInRDMInReadingResetCommand());
            c.addCommand(new AddResetAssetActivityCommand());
            c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain scheduleReportChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddTemplateCommand());
			c.addCommand(new ScheduleV2ReportCommand());
			return c;
		}

		public static FacilioChain getExportReportFileChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
			c.addCommand(new GetExportReportFileCommand());
			return c;
		}

		public static FacilioChain sendReportMailChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(getExportReportFileChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}

		public static FacilioChain getExportAnalyticsFileChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(ReadOnlyChainFactory.newFetchReadingReportChain());
			c.addCommand(new GetExportReportFileCommand());
			return c;
		}

		public static FacilioChain sendAnalyticsMailChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(getExportAnalyticsFileChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}

		public static FacilioChain getExportModuleReportFileChain(boolean fetchData) {
			FacilioChain c = getDefaultChain();
			if (fetchData) {
				c.addCommand(new GenerateCriteriaFromFilterCommand());
				c.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
			}
			c.addCommand(new GetModuleFromReportContextCommand());
			c.addCommand(new GetExportModuleReportFileCommand());
			return c;
		}

		public static FacilioChain sendModuleReportMailChain(boolean fetchData) {
			FacilioChain c = getDefaultChain();
			c.addCommand(getExportModuleReportFileChain(fetchData));
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}
		public static FacilioChain getExportpivotReportFileChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(ReadOnlyChainFactory.fetchPivotReportChain());
		c.addCommand(new ExportPivotReport());
		return c;
		}
		public static FacilioChain sendPivotReportMailChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(getExportpivotReportFileChain());
		c.addCommand(new SendReadingReportMailCommand());
		return c;
		}

		public static FacilioChain getExportModuleAnalyticsFileChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConstructReportData());
			c.addCommand(getExportModuleReportFileChain(true));
			return c;
		}

		public static FacilioChain sendModuleAnalyticsMailChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(getExportModuleAnalyticsFileChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}


	    public static FacilioChain getAddWidgetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddWidgetCommand());
			return c;
		}

	    public static FacilioChain getUpdateWidgetsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateWidgetsCommand());
			return c;
		}
	    public static FacilioChain getAddDashboardChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddDashboardCommand());
			return c;
		}
	    public static FacilioChain getAddDashboardTabChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddDashboardTabCommand());
			return c;
		}
	    public static FacilioChain getUpdateDashboardTabsListChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateDashboardTabListCommand());
			return c;
		}
	    public static FacilioChain getUpdateDashboardChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DuplicateDashboardForBuildingCommand());
			c.addCommand(new UpdateDashboardWithWidgetCommand());
			c.addCommand(new EnableMobileDashboardCommand());

			return c;
		}

	    public static FacilioChain getUpdateDashboardTabChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateDashboardTabWithWidgetCommand());
			return c;
		}
	    public static FacilioChain getUpdateDashboardsChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new DuplicateBaseSpaceForDashboard());
			c.addCommand(new UpdateDashboardsCommand());
			return c;
		}

	    public static FacilioChain getUpdateDashboardPublishChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new updateDashboardPublishCommand());
			return c;
	    }

	    public static FacilioChain getDeleteDashboardChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new DeleteDashboardCommand());
			return c;
		}

	    public static FacilioChain getDeleteDashboardTabChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new DeleteDashboardTabCommand());
			return c;
		}

	    public static FacilioChain getAddDashboardFolderChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddDashboardFolderCommand());
			return c;
		}
	    public static FacilioChain getUpdateDashboardFolderChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateDashboardFolderCommand());
			return c;
		}
	    public static FacilioChain getDeleteDashboardFolderChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteDashboardFolderCommand());
			return c;
		}

		public static FacilioChain executeScheduledReadingRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecuteScheduledReadingRuleCommand());
			return c;
		}

		public static FacilioChain executeScheduledAlarmTriggerChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecuteScheduledAlarmTriggerCommand());
			return c;
		}

		public static FacilioChain getAddInventoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventory());
			// c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddInventoryCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static FacilioChain getUpdateInventoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventory());
			c.addCommand(new UpdateInventoryCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static FacilioChain getDeleteInventoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventory());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static FacilioChain getAddInventoryVendorChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryVendor());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static FacilioChain getUpdateInventoryVendorChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryVendor());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static FacilioChain getDeleteVendorChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryVendor());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static FacilioChain getAddInventoryCategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryCategory());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static FacilioChain getUpdateInventoryCategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryCategory());
			c.addCommand(new GenericUpdateModuleDataCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static FacilioChain getDeleteInventoryCategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryCategory());
			c.addCommand(new GenericDeleteModuleDataCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static FacilioChain getAddOrUdpateWorkorderPartChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderParts());
			c.addCommand(new AddOrUpdateWorkorderPartCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static FacilioChain getDeleteWorkorderPartChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderParts());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddWorkorderCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			return c;
		}

		public static FacilioChain getAddStoreRoomChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForStoreRoom());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new DeleteSitesForStoreRoomCommad());
			c.addCommand(new AddSitesForStoreRoomCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getUpdateStoreRoomChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForStoreRoom());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new DeleteSitesForStoreRoomCommad());
			c.addCommand(new AddSitesForStoreRoomCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getAddItemTypeCategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesCategory());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getUpdateItemTypeCategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesCategory());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getDeleteItemTypesCategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesCategory());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddItemTypesStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypeStatus());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getUpdateItemTypesStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypeStatus());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getDeleteItemTypesStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypeStatus());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddItemTypesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypes());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());

			return c;
		}

		public static FacilioChain getUpdateItemTypesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypes());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateConnectedAppChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateConnectedAppCommand());
			return c;
		}

		public static FacilioChain getAddClientSideConnectedAppInternalHostingChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddConnectedAppCommand());
			c.addCommand(new AddDefaultConnectedAppFilesCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateConnectedAppSAMLChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateConnectedAppSAMLCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateConnectedAppWidgetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateConnectedAppWidgetCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateVariableChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateVariableCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateConnectedAppConnectorChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateConnectedAppConnectorCommand());
			return c;
		}

		public static FacilioChain getDeleteConnectedAppChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteConnectedAppCommand());
			return c;
		}

		public static FacilioChain getAddToolTypesCategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesCategory());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getUpdateToolTypesCategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesCategory());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getDeleteToolTypesCategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesCategory());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddToolsStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesStatus());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getUpdateToolsStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesStatus());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getDeleteToolsStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesStatus());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddToolStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolStatus());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getUpdateToolStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolStatus());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getDeleteToolStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolStatus());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddToolTypesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypes());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());

			return c;
		}

		public static FacilioChain getBulkAddToolTypesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypes());
			c.addCommand(new GenericAddModuleDataListCommand());
			return c;
		}

		public static FacilioChain getUpdateToolTypesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypes());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());

			return c;
		}

		public static FacilioChain getAddVendorChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVendors());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new GenericAddSubModuleDataCommand());
			c.addCommand(new AddVendorContactsCommand());
			c.addCommand(new AddInsuranceVendorRollUpsCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ConstructAddCustomActivityCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getConstructUpdateActivitiesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConstructUpdateCustomActivityCommand());
			c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.CUSTOM_ACTIVITY));
			return c;
		}

		public static FacilioChain getUpdateVendorChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVendors());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new GenericAddSubModuleDataCommand());
			c.addCommand(new AddVendorContactsCommand());
			c.addCommand(new AddInsuranceVendorRollUpsCommand());
			c.addCommand(new LoadVendorLookUpCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
			c.addCommand(new ConstructUpdateCustomActivityCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getDeleteVendorsChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVendors());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddItemStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemStatus());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getUpdateItemStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemStatus());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getItemStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemStatus());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItem());
			c.addCommand(new AddItemCommandV3());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getAddPurchasedItemChain());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getAddBulkItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItem());
			c.addCommand(new BulkItemAdditionCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getAddBulkPurchasedItemChain());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			return c;
		}

		public static FacilioChain getUpdateItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItem());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(getAddPurchasedItemChain());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getAddPurchasedItemChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedItem());
			c.addCommand(new GetAddPurchasedItemCommandV3());
			c.addCommand(getAddOrUpdateItemStockTransactionChain());
			return c;
		}


		public static FacilioChain getAddBulkPurchasedItemChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedItem());
			c.addCommand(new AddPurchasedItemsForBulkItemAddCommand());
			c.addCommand(getAddOrUpdateItemStockTransactionChain());
			return c;
		}

		public static FacilioChain getAddOrUpdateItemStockTransactionChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new AddOrUpdateItemStockTransactionsCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			return c;
		}

		public static FacilioChain getUpdateItemQuantityRollupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateItemQuantityCommandV3());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE));

			c.addCommand(getUpdateItemTypeQuantityRollupChain());
			return c;
		}

		public static FacilioChain getUpdateItemTypeQuantityRollupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ItemTypeQuantityRollupCommandV3());
			return c;
		}

		public static FacilioChain getUpdateInventoryCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedItem());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			return c;
		}

		public static FacilioChain getDeleteInventoryCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedItem());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getAddOrUdpateWorkorderItemsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderItems());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new AddOrUpdateWorkorderItemsCommand());
			c.addCommand(new UpdateReservedQuantityCommand());
			c.addCommand(new LoadWorkorderItemLookUpCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new ItemTransactionRemainingQuantityRollupCommand());
			c.addCommand(new AddActivitiesCommand());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static FacilioChain getDeleteWorkorderItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderItems());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new RequestedLineItemQuantityRollUpCommand());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new DeleteWorkorderItemCommand());
			c.addCommand(new ItemTransactionRemainingQuantityRollupCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static FacilioChain getAddToolChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTool());
			c.addCommand(new AddToolCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getAddPurchasedToolChain());
//			c.addCommand(getAddOrUpdateToolStockTransactionChain());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			return c;
		}

		public static FacilioChain getBulkAddToolChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTool());
			c.addCommand(new BulkToolAdditionCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddBulkToolStockTransactionsCommandV2());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			return c;
		}

		public static FacilioChain getUpdateToolChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTool());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddOrUdpateWorkorderToolsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderTools());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new AddOrUpdateWorkorderToolsCommand());
			c.addCommand(new LoadWorkorderToolLookupCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new AddActivitiesCommand());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static FacilioChain getAddOrUdpateWorkorderLabourChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderLabour());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new AddOrUpdateWorkorderLabourCommand());
			c.addCommand(new LoadWorkorderLabourLookupCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			//c.addCommand(getUpdateAvailabilityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}


		public static FacilioChain getUpdatetoolQuantityRollupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ToolQuantityRollUpCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE));
			c.addCommand(getUpdateToolTypeQuantityRollupChain());
			return c;
		}

		public static FacilioChain getUpdateAvailabilityRollupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new LabourAvailabilityRollUpCommand());
			return c;
		}

		public static FacilioChain getUpdateToolTypeQuantityRollupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ToolTypeQuantityRollupCommand());
			return c;
		}

		public static FacilioChain getDeleteWorkorderToolsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderTools());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new RequestedLineItemQuantityRollUpCommand());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new DeleteWorkorderToolCommand());
			c.addCommand(new ToolTransactionRemainingQuantityRollupCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static FacilioChain getDeleteWorkorderLabourChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderLabour());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteWorkorderLabourCommand());
			//c.addCommand(getUpdateAvailabilityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}
		public static FacilioChain getAddOrUpdateWorkorderCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			return c;
		}

		public static FacilioChain getUpdateWorkorderCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			return c;
		}

		public static FacilioChain getDeleteWorkorderCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			return c;
		}

		public static FacilioChain getAssetFromQRChain() {
			FacilioChain c = getDefaultChain();
			//c.addCommand(new ParseQRValueCommand());
			c.addCommand(new FetchAssetFromQRValCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new LoadAssetFields());
			c.addCommand(new GetAssetDetailCommand());
			c.addCommand(new UpdateGeoLocationCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getAddItemTypesVendorsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
			c.addCommand(new GenericAddModuleDataListCommand());
			return c;
		}

		public static FacilioChain getUpdateItemTypesVendorsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
			c.addCommand(new GenericUpdateListModuleDataCommand());
			return c;
		}

		public static FacilioChain getDeleteItemTypesVendorsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateItemTransactionsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new AddOrUpdateManualItemTransactionCommand());
			c.addCommand(getItemTransactionRemainingQuantityRollupChain());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new UpdateRequestedItemIssuedQuantityCommand());
			c.addCommand(new AddActivitiesCommand());

			return c;
		}

		public static FacilioChain getAdjustmentItemTransactionsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new AdjustmentItemTransactionCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new AddActivitiesCommand());

			return c;
		}

		public static FacilioChain getItemTransactionRemainingQuantityRollupChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new ItemTransactionRemainingQuantityRollupCommand());
			return c;
		}
		public static FacilioChain getDeleteItemTransactionsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteItemTransactionCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getItemTransactionRemainingQuantityRollupChain());
			c.addCommand(getUpdateItemQuantityRollupChain());
			return c;
		}

		public static FacilioChain getUpdateInventoryTransactionsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			return c;
		}

		public static FacilioChain getAddPurchasedToolChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedTool());
			c.addCommand(new GetAddPurchasedToolCommand());
			c.addCommand(getAddOrUpdateToolStockTransactionChain());
			return c;
		}

		public static FacilioChain getDeletePurchasedToolsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedTool());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateToolStockTransactionChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
			c.addCommand(new AddOrUpdateToolStockTransactionsCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			return c;
		}

		public static FacilioChain getAddOrUdpateToolTransactionsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
			c.addCommand(new AddOrUpdateManualToolTransactionsCommand());
			c.addCommand(getToolTransactionRemainingQuantityRollupChain());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new UpdateRequestedToolIssuedQuantityCommand());
			c.addCommand(new AddActivitiesCommand());


			return c;
		}

		public static FacilioChain getToolTransactionRemainingQuantityRollupChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
			c.addCommand(new ToolTransactionRemainingQuantityRollupCommand());
			return c;
		}

		public static FacilioChain getDeleteToolTransactChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteToolTransactionCommand());
			c.addCommand(getToolTransactionRemainingQuantityRollupChain());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			return c;
		}

		public static FacilioChain getSetItemAndToolTypeForStoreRoomChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new SetItemAndToolTypeForStoreRoomCommandV3());
			return c;
		}

		public static FacilioChain getApproveRejectWorkorderItemsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
//			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new ApproveOrRejectItemCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			c.addCommand(new AddActivitiesCommand());

			return c;
		}

		public static FacilioChain getApproveRejectManualItemsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new ApproveOrRejectItemCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getApproveRejectWorkorderToolsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
//			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new ApproveOrRejectToolCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static FacilioChain getApproveRejectManualToolsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
//			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new ApproveOrRejectToolCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			return c;
		}

		public static FacilioChain getAddLabourChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabour());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getDeleteLabourChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabour());
			c.addCommand(new DeleteLabourCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}


		public static FacilioChain getUpdateLabourChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabour());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddPurchaseRequestChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseRequest());
			chain.addCommand(new AddOrUpdatePurchaseRequestCommand());
			chain.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			chain.addCommand(getPurchaseRequestTotalCostChain()); //update purchase request total cost
			chain.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));

			return chain;
		}

		public static FacilioChain getUpdatePurchaseRequestStatusChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseRequest());
			chain.addCommand(new UpdateBulkPurchaseRequestStatusCommand());
			chain.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));

			return chain;
		}

		public static FacilioChain getPurchaseRequestDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseRequest());
			c.addCommand(new DeletePurchaseRequestCommand());
			return c;
		}

		public static FacilioChain getAddPurchaseRequestLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseRequestLineItem());
			c.addCommand(new AddOrUpdatePurchaseRequestLineItemCommand());
			c.addCommand(getPurchaseRequestTotalCostChain()); //update purchase request total cost
			return c;
		}

		public static FacilioChain getDeletePurchaseRequestLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseRequestLineItem());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddPurchaseOrderChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseOrder());
			chain.addCommand(new UpdateTransactionEventTypeCommand());
			chain.addCommand(new AddOrUpdatePurchaseOrderCommand());
			chain.addCommand(new RollUpReceivableDeliveryTimeCommand());
			chain.addCommand(new AssociateDefaultTermsToPoCommand());
			chain.addCommand(getPurchaseOrderTotalCostChain()); //update purchase order total cost
			chain.addCommand(new AddPurchaseRequestOrderRelation());
			chain.addCommand(new PurchaseRequestPurchaseOrderLookUpsCommand());
			chain.addCommand(new GenericGetModuleDataListCommand());
			chain.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			chain.addCommand(new ExecuteAllWorkflowsCommand(RuleType.TRANSACTION_RULE));

			chain.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));


			return chain;
		}

		public static FacilioChain getPurchaseOrderDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseOrder());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.TRANSACTION_RULE));
			c.addCommand(new DeleteReceivableByPoIdCommand());
			return c;
		}

		public static FacilioChain getAddPurchaseOrderLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseOrderLineItem());
			c.addCommand(new AddOrUpdatePurchaseOrderLineItemCommand());
			c.addCommand(getPurchaseOrderTotalCostChain()); //update purchase order total cost
			return c;
		}

		public static FacilioChain getUpdatePurchaseOrderStatusChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseOrder());
			chain.addCommand(new UpdateBulkPurchaseOrderStatusCommand());
			chain.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));

			return chain;
		}

		public static FacilioChain getDeletePurchaseOrderLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseOrderLineItem());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateReceiptsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForReceipt());
			c.addCommand(new AddOrUpdateReceiptCommand());
			c.addCommand(getPurchaseOrderLineItemQuantityRecievedRollUpChain());
			c.addCommand(getPurchaseOrderQuantityRecievedRollUpChain());
			c.addCommand(getPurchaseOrderAutoCompleteChain());
			return c;
		}

		public static FacilioChain getAddOrUpdateReceivablesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForReceivables());
			c.addCommand(new AddOrUpdateReceivablesCommand());
			return c;
		}

		public static FacilioChain getConvertPRToPOChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConvertPRToPOCommand());
			return c;
		}
		public static FacilioChain getPurchaseRequestTotalCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PurchaseRequestTotalCostRollUpCommand());
			return c;
		}

		public static FacilioChain getPurchaseOrderTotalCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PurchaseOrderTotalCostRollUpCommand());
			return c;
		}

		public static FacilioChain getPurchaseOrderQuantityRecievedRollUpChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PurchaseOrderQuantityRecievedRollUpCommand());
			return c;
		}

		public static FacilioChain getPurchaseOrderLineItemQuantityRecievedRollUpChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PurchaseOrderLineItemQuantityRollUpCommand());
			return c;
		}

		public static FacilioChain getPurchaseOrderAutoCompleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PurchaseOrderAutoCompleteCommand());
			c.addCommand(getAddOrUpdateItemTypeVendorChain());
			c.addCommand(getAddOrUpdateToolTypeVendorChain());
			c.addCommand(getBulkAddToolChain());
			c.addCommand(getAddBulkItemChain());
			c.addCommand(new UpdateServiceVendorPriceCommand());
			return c;
		}

		public static FacilioChain getPurchaseOrderCompleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PurchaseOrderCompleteCommand());
			c.addCommand(getAddOrUpdateItemTypeVendorChain());
			c.addCommand(getAddOrUpdateToolTypeVendorChain());
			c.addCommand(getBulkAddToolChain());
			c.addCommand(getAddBulkItemChain());
			c.addCommand(new UpdateServiceVendorPriceCommand());
			return c;
		}

		public static FacilioChain getPendingPOLineItemsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetPendingPoLineItemsCommand());
			return c;
		}

		public static FacilioChain getReceivedPOLineItemsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetReceivedPoLineItemsCommand());
			return c;
		}

		public static FacilioChain getPOOnInventoryTypeIdChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetPurchaseOrdersListOnInventoryTypeIdCommand());
			return c;
		}


		public static FacilioChain getAddOrUpdateItemTypeVendorChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateItemTypeVendorCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateToolTypeVendorChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateToolVendorCommand());
			return c;
		}

		public static FacilioChain getImportItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ImportItemCommand());
			c.addCommand(getAddBulkItemChain());
			return c;
		}

		public static FacilioChain getImportToolChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ImportToolCommand());
			c.addCommand(getBulkAddToolChain());
			return c;
		}

		public static FacilioChain getAddFormCommand() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddFormCommand());
			c.addCommand(new AddSystemFieldsCommand());
			c.addCommand(new AddFormForCustomModuleCommand());
			c.addCommand(new AddFormSiteRelationCommand());

			return c;
		}

		public static FacilioChain getAddDefaultFormRule(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddDefaultFormRuleSignupCommand());
			return c;
		}

		public static FacilioChain getAddGlimpseChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddGlimpseCommand());
			return c;
		}

		public static FacilioChain getAddSubformChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateSubFormRequestCommand());
			c.addCommand(new GenerateSubformLinkNameCommand());
			c.addCommand(getAddFormCommand());
			return c;
		}

		public static FacilioChain getUpdateFormChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new EditFormCommand());
			c.addCommand(new DeleteFormSiteRelationCommand());
			c.addCommand(new AddFormSiteRelationCommand());
			return c;
		}

		public static FacilioChain getUpdateFormFieldChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateFormFieldCommand());
			return c;
		}

		public static FacilioChain getUpdateFormFieldAndModuleFieldChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateFormFieldCommand());
			c.addCommand(new UpdateFormFieldCommand());
			c.addCommand(new UpdateFieldCommand());
			return c;
		}

		public static FacilioChain getUpdateFormFieldsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateFormFieldsCommand());
			return c;
		}

	public static FacilioChain getUpdateFormListFieldsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateFormListFieldsCommand());
		return c;
	}

		public static FacilioChain getUpdateFormSectionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateFormSectionCommand());
			return c;
		}

		public static FacilioChain getDeleteFormChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteFormCommand());
			c.addCommand(new DeleteFormSiteRelationCommand());
			return c;
		}

		public static FacilioChain getAddPurchaseContractChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseContract());
			chain.addCommand(new AddOrUpdatePurchaseContractCommand());
			chain.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());
			chain.addCommand(getPurchaseContractTotalCostChain()); //roll up for calculating total cost


			return chain;
		}

		public static FacilioChain getPurchaseContractDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseContract());
			c.addCommand(new DeletePurchaseContractCommand());
			c.addCommand(new DeletePreferenceRulesOnRecordDeletionCommand()); // changed the module name to contracts
			return c;
		}

		public static FacilioChain getAddPurchaseContractLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseContractLineItem());
			c.addCommand(new AddOrUpdatePurchaseContractLineItemCommand());
			c.addCommand(getPurchaseContractTotalCostChain()); //roll up for calculating total cost
			return c;
		}

		public static FacilioChain getActivePurchaseContractPrice() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetActiveContractPriceCommand());
			return c;
		}

		public static FacilioChain getDeletePurchaseContractLineItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseContractLineItem());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		public static FacilioChain getAddLabourContractChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForLabourContract());
			chain.addCommand(new AddOrUpdateLabourContractCommand());
			chain.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());

			//rollup to update the cost per hour in the actual labour module
			chain.addCommand(new UpdateLabourCostRollUpCommand());

		    //rollup might be needed to update purchase contract total cost -- need to be discussed
			return chain;
		}

		public static FacilioChain getLabourContractDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabourContract());
			c.addCommand(new DeleteLabourContractCommand());
			c.addCommand(new DeleteRecordRuleJobOnRecordDeletionCommand());

			return c;
		}

		public static FacilioChain getAddLabourContractLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabourContractLineItem());
			c.addCommand(new AddOrUpdateLabourContractLineItemCommand());
			//rollup might be needed to update contract total cost -- need to be discussed
			return c;
		}

		public static FacilioChain getDeleteLabourContractLineItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabourContractLineItem());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		public static FacilioChain getUpdatePurchaseContractStatusChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseContract());
			chain.addCommand(new UpdateBulkPurchaseContractStatusCommand());
			chain.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());

			return chain;
		}
		public static FacilioChain getUpdateLabourContractStatusChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForLabourContract());
			chain.addCommand(new UpdateBulkLabourContractStatusCommand());
			chain.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());

			return chain;
		}

		public static FacilioChain getPurchaseContractTotalCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PurchaseContractTotalCostRollupCommand());
			return c;
		}

		public static FacilioChain getAddPoLineItemSerialNumbersChain () {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(new AddSerialNumberForPoLineItemsCommand());
			chain.addCommand(SetTableNamesCommand.getForPoLineItemSerialNumber());
			chain.addCommand(new GenericAddModuleDataListCommand());
			return chain;
		}

		public static FacilioChain getUpdatePoLineItemSerialNumbersChain () {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPoLineItemSerialNumber());
			chain.addCommand(new GenericUpdateModuleDataCommand());
			return chain;
		}
		public static FacilioChain getDeletePoLineItemSerialNumbersChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPoLineItemSerialNumber());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getAddGatePassChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGatePass());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(getApproveRejectWorkorderToolsChain());
			c.addCommand(new AddGatePassLineItemsCommand());
			return c;
		}

		public static FacilioChain getUpdateStateTransitionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GenericGetModuleDataDetailCommand());
			c.addCommand(new UpdateStateCommand());
			c.addCommand(new UpdateFieldDataCommand());
			c.addCommand(new AddActivitiesCommand());
//			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getAvailableApprovalState() {
			FacilioChain c = getDefaultChain();
			c.addCommand(getAvailableState());
			c.addCommand(new GetAvailableApproverListCommand());
			return c;
		}

		public static FacilioChain getAvailableState() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddFieldsAndLookupFieldsInContextCommand());
			c.addCommand(new GenericGetModuleDataDetailCommand());
			c.addCommand(new GetAvailableStateCommand());
			return c;
		}

		public static FacilioChain getConfirmationDialogChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GenericGetModuleDataDetailCommand());
			c.addCommand(new GetConfirmationDialogFromApproverWorkflowRuleCommand());
			c.addCommand(new GetMatchedConfirmationDialogCommand());
			return c;
		}

	public static FacilioChain getOfflineStateTransitionChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetOfflineStateTransitionCommand());
		return c;
	}
		public static FacilioChain getRegisterOrUnRegisterOfflineRecordChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetRegisterOrUnRegisterOfflineRecordCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateStateFlowTransition() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConstructStateFlowTransitionCommand());
			c.addCommand(new AddOrUpdateStateTransitionCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateStateFlow() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateStateFlowCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateStateChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateStateCommand());
			return c;
		}

		public static FacilioChain getAddConnectionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddConnectionCommand());
			return c;
		}

		public static FacilioChain getUpdateConnectionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateConnectionCommand());
			c.addCommand(new GetAccessTokenForConnectionCommand());
			return c;
		}

		public static FacilioChain getInvalidateConnectionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new InvalidateConnectionCommand());
			return c;
		}

		public static FacilioChain getDeleteConnectionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteConnectionCommand());
			return c;
		}

		public static FacilioChain getAddTemplateToRules () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConvertToRulesCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateInventoryRequestChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
			chain.addCommand(new ValidateInventoryRequestUpdateCommand());
			chain.addCommand(new AddOrUpdateInventoryRequestCommand());
			chain.addCommand(new GenericGetModuleDataListCommand());
			chain.addCommand(new ChangeApprovalStatusForModuleDataCommand());
			chain.addCommand(new VerifyApprovalCommand());
			chain.addCommand(new ExecuteStateFlowCommand());
			chain.addCommand(new UpdateStateForModuleDataCommand());
			chain.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			chain.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
			chain.addCommand(new ExecuteAllWorkflowsCommand());
			return chain;
		}

		public static FacilioChain getIssueInventoryRequestChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
			chain.addCommand(new AddOrUpdateInventoryRequestCommand());
			chain.addCommand(new GenericGetModuleDataListCommand());
			chain.addCommand(new ExecuteStateFlowCommand());
			chain.addCommand(new UpdateStateForModuleDataCommand());
			chain.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			chain.addCommand(new ExecuteAllWorkflowsCommand());
			chain.addCommand(new LoadItemTransactionEntryInputCommand());
			chain.addCommand(getAddOrUpdateItemTransactionsChain());
			//rollups work with record_list object in the context
			chain.addCommand(new CopyToToolTransactionCommand());
			chain.addCommand(getAddOrUdpateToolTransactionsChain());
			return chain;

		}
		public static FacilioChain getInventoryRequestDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryRequest());
			c.addCommand(new DeleteInventoryRequestCommand());
			return c;
		}

		public static FacilioChain getAddInventoryRequestLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryRequest());
			c.addCommand(new AddOrUpdateInventoryRequestLineItemCommand());
			return c;
		}

		public static FacilioChain getDeleteInventoryRequestLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryRequest());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getUseLineItemsForItemsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UseInventoryRequestLineItemsCommandV3());
			c.addCommand(getAddOrUdpateWorkorderItemsChain());
			return c;
		}

		public static FacilioChain getUseLineItemsForToolsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UseInventoryRequestLineItemsCommand());
			c.addCommand(getAddOrUdpateWorkorderToolsChain());
			return c;
		}



		public static FacilioChain getAddOrUpdateGatePassChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGatePass());
			c.addCommand(new GetAddOrUpdateGatePassCommand());
			return c;
		}

		public static FacilioChain getGatePassDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGatePass());
			c.addCommand(new DeleteGatePassCommand());
			return c;
		}

		public static FacilioChain getAddJobPlanChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddJobPlanCommand());
			return c;
		}

		public static FacilioChain getUpdateJobPlanChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateJobPlanCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateShipmentChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForShipment());
			chain.addCommand(new AddOrUpdateShipmentCommand());
			return chain;
		}
		public static FacilioChain getDeleteShipmentChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipment());
			c.addCommand(new GetDeleteShipmentCommand());
			return c;
		}


		public static FacilioChain getDeleteStateFlowTransition() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteStateFlowTransition());
			return c;
		}

		public static FacilioChain getRearrangeStateFlows() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ChangeTransitionExecutionOrderCommand());
			return c;
		}

		public static FacilioChain getChangeStatusForStateflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ChangeStateFlowStatusCommand());
			c.addCommand(new ChangeTransitionExecutionOrderCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateServiceChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForServices());
			chain.addCommand(new AddOrUpdateServiceCommand());
			return chain;
		}

		public static FacilioChain getChangeStatusForClassificationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateClassificationStatusCommand());

			return c;
		}

		public static FacilioChain getServiceDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForServices());
			c.addCommand(new DeleteServiceCommand());
			return c;
		}

		public static FacilioChain getUpdateWarrantyContractStatusChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForWarrantyContract());
			chain.addCommand(new UpdateBulkWarrantyContractCommand());
			chain.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());
			return chain;
		}

		public static FacilioChain getAddWarrantyContractChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForWarrantyContract());
			chain.addCommand(new AddOrUpdateWarrantyContractCommand());
			chain.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());
			chain.addCommand(getWarrantContractTotalCostChain());
			return chain;
		}

		public static FacilioChain getWarrantyContractDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWarrantyContract());
			c.addCommand(new DeleteWarrantyContractCommand());
			c.addCommand(new DeletePreferenceRulesOnRecordDeletionCommand());
			return c;
		}
		public static FacilioChain getPMPlannerSettingsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdatePMPlannerSettingsCommand());
			return c;
		}
		public static FacilioChain getAddShipmentLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipmentLineItems());
			c.addCommand(new AddOrUpdateShipmentLineItemCommand());
			return c;
		}

		public static FacilioChain getDeleteShipmentLineItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipmentLineItems());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getReceiveShipmentChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipment());
			c.addCommand(new AddOrUpdateShipmentCommand());
			c.addCommand(getReceiveShipmentInventoryChain());
		    return c;
		}

		public static FacilioChain getReceiveShipmentInventoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ReceiveShipmentCommand());
			c.addCommand(getBulkAddShipmentToolChain());
			c.addCommand(getAddBulkItemChain());
			c.addCommand(new AddShipmentRotatingAssetsCommand());
			return c;
		}
		public static FacilioChain getStageShipmentChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipment());
			c.addCommand(new AddOrUpdateShipmentCommand());
			c.addCommand(getStageShipmentInventoryChain());
			return c;
		}

		public static FacilioChain getBulkAddShipmentToolChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTool());
			c.addCommand(new BulkToolAdditionCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddBulkToolStockTransactionsCommand());
			c.addCommand(new AddBulkRotatingToolShipmentTransactionCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			return c;
		}
		public static FacilioChain getStageShipmentInventoryChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new StageShipmentCommand());
			c.addCommand(new LoadTransactionInputForShipmentCommand());
	        c.addCommand(getAddOrUpdateItemTransactionsChain());
	        //rollups work with record_list object in the context
	        c.addCommand(new LoadToolTransactionsCommand());
	        c.addCommand(getAddOrUdpateToolTransactionsChain());
	        c.addCommand(new DeleteShipmentRotatingAssetCommand());
	        return c;

		}

		public static FacilioChain getTransferShipmentChain() {

			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateShipmentRecordForDirecttransferCommand());
			c.addCommand(getStageShipmentInventoryChain());
		    c.addCommand(getReceiveShipmentInventoryChain());
		    return c;
		}


		public static FacilioChain addShiftUserMappingChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddShiftUserMappingCommand());
			return c;
		}

		public static FacilioChain getAddAttendanceTransactionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForAttendanceTransaction());
			c.addCommand(new AddAttendanceCommand());
			c.addCommand(new GenericAddModuleDataCommand());
			return c;
		}
		public static FacilioChain markAbsentChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetUsersForShiftCommand());
			c.addCommand(new MarkAsAbsentOrLeaveCommand());
			return c;
		}

		public static FacilioChain addOrUpdateBreakChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateBreakCommand());
			return c;
		}

		public static FacilioChain deleteBreakChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteBreakCommand());
			return c;
		}

		public static FacilioChain getAddBreakTransactionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForBreakTransaction());
			c.addCommand(new AddOrUpdateBreakTransactionCommand());
			return c;
		}

		public static FacilioChain getAddAssetDowntimeChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetAssetDownTimeDetailsCommand());
			c.addCommand(new GetLastBreakDownFromCurrentSourceCommand());
			c.addCommand(new ValidateAssetBreakdownCommand());
			c.addCommand(new AddAssetBreakDownCommand());
			c.addCommand(new updateAssetDownTimeDetailsCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getAttendanceTransitionState() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ShowStateForAttendanceCommand());
			return c;
		}

		public static FacilioChain getAddMVProjectChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateMVProjectCommand());
			c.addCommand(new AddMVProjectCommand());
			c.addCommand(new AddMVBaselineCommand());
			c.addCommand(new AddMVAdjustmentCommand());
			c.addCommand(new ConstructBaselineFormulaWithAdjustmentCommand());
			c.addCommand(new ConstructTargetedConsumptionCommand());
			c.addCommand(new ConstructSavedConsumptionCommand());
			c.addCommand(new ConstructCumulativeConsumptionCommand());
			c.addCommand(new ConstructPercentageSavingCommand());
			c.addCommand(new ScheduleMVFormulaCalculationJob());					// always have this as last command
			return c;
		}

		public static FacilioChain getUpdateMVProjectChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateMVProjectCommand());
			c.addCommand(new UpdateMVProjectCommand());
			c.addCommand(new UpdateMVBaselineCommand());
			c.addCommand(new UpdateMVAdjustmentCommand());
			c.addCommand(new ConstructBaselineFormulaWithAdjustmentCommand());
			c.addCommand(new ConstructTargetedConsumptionCommand());
			c.addCommand(new ConstructSavedConsumptionCommand());
			c.addCommand(new ConstructCumulativeConsumptionCommand());
			c.addCommand(new ConstructPercentageSavingCommand());
			c.addCommand(new ScheduleMVFormulaCalculationJob());					// always have this as last command
			return c;
		}

		public static FacilioChain getUpdateMVProjectMetaChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateMVProjectCommand());
			c.addCommand(new UpdateMVProjectCommand());
			return c;
		}

		public static FacilioChain getAddMVBaselineChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddMVBaselineCommand());
			return c;
		}

		public static FacilioChain getAddMVAdjustmentChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddMVAdjustmentCommand());
			return c;
		}


		public static FacilioChain getDeleteMVProjectChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteMVProjectCommand());
			return c;
		}

		public static FacilioChain getAddWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateWorkflowCommand());
			c.addCommand(new AddWorkflowCommand());
			return c;
		}
		public static FacilioChain getUpdateWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateWorkflowCommand());
			c.addCommand(new UpdateWorkflowCommand());
			return c;
		}
		public static FacilioChain getDeleteWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteWorkflowCommand());
			return c;
		}

		public static FacilioChain getAddWorkflowNameSpaceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddNameSpaceCommand());
			return c;
		}
		public static FacilioChain getUpdateWorkflowNameSpaceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateNameSpaceCommand());
			return c;
		}
		public static FacilioChain getDeleteWorkflowNameSpaceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteNameSpaceCommand());
			return c;
		}

		public static FacilioChain getAddWorkflowUserFunctionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(getAddWorkflowChain());
			c.addCommand(new AddUserFunctionCommand());
			return c;
		}
		public static FacilioChain getUpdateWorkflowUserFunctionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(getUpdateWorkflowChain());
			c.addCommand(new UpdateUserFunctionCommand());
			return c;
		}

		public static FacilioChain getDefaultWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetDefaultWorkflowContext());
			return c;
		}
		public static FacilioChain getExecuteDefaultWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetDefaultWorkflowContext());
			c.addCommand(getExecuteWorkflowChain());
			return c;
		}

		public static FacilioChain getExecuteWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateWorkflowCommand());
			c.addCommand(new ExecuteWorkflowCommand());
			return c;
		}



		public static FacilioChain getExecuteFloorPlanWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecuteFloorPlanWorkflowCommand());
			return c;
		}

		public static FacilioChain getAddGraphicsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGraphics());
			c.addCommand(new AddGraphicsCommand());
			return c;
		}

		public static FacilioChain getDeleteGraphicsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGraphics());
			c.addCommand(new DeleteGraphicsCommand());
			return c;
		}

		public static FacilioChain getUpdateGraphicsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGraphics());
			c.addCommand(new UpdateGraphicsCommand());
			return c;
		}

		public static FacilioChain getAddGraphicsFolderChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGraphicsFolder());
			c.addCommand(new AddGraphicsFolderCommand());
			return c;
		}

		public static FacilioChain getDeleteGraphicsFolderChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGraphicsFolder());
			c.addCommand(new DeleteGraphicsFolderCommand());
			return c;
		}

		public static FacilioChain getUpdateGraphicsFolderChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGraphicsFolder());
			c.addCommand(new UpdateGraphicsFolderCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateScheduledWorkflowChain() {
			FacilioChain c = getDefaultChain();
//			c.addCommand(getAddWorkflowChain());
			c.addCommand(new AddOrUpdateScheduledWorkflowCommand());
			return c;
		}
//		public static FacilioChain getUpdateScheduledWorkflowChain() {
//			FacilioChain c = getDefaultChain();
//			c.addCommand(getUpdateWorkflowChain());
//			c.addCommand(new updateScheduledWorkflowCommand());
//			return c;
//		}
		public static FacilioChain getDeleteScheduledWorkflowChain() {
			FacilioChain c = getDefaultChain();
//			c.addCommand(new DeleteWorkflowCommand());
			c.addCommand(new DeleteScheduledWorkflowCommand());
			return c;
		}
		public static FacilioChain getAddShiftRotationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShiftRotation());
			c.addCommand(new AddOrUpdateShiftRotationCommand());
			return c;
		}

		public static FacilioChain getUpdateShiftRotationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShiftRotation());
			c.addCommand(new AddOrUpdateShiftRotationCommand());
			return c;
		}

		public static FacilioChain getDeleteShiftRotationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShiftRotation());
			c.addCommand(new DeleteShiftRotationCommand());
			return c;
		}

		public static FacilioChain getExecuteShiftRotationCommand() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecuteShiftRotationCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateTicketStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateFacilioStatusCommand());
			return c;
		}

		public static FacilioChain getV2AddEventPayloadChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new JsonToV2EventCommand());
			c.addCommand(getV2AddEventChain(false));
			return c;
		}

		public static FacilioChain getV2AddEventChain(boolean isHistorical) {
			FacilioChain c = getDefaultChain();
			c.addCommand(new NewExecuteEventRulesCommand());
			c.addCommand(new InsertNewEventsCommand());
			c.addCommand(new NewEventsToAlarmsConversionCommand());
			c.addCommand(new SaveAlarmAndEventsCommand());

			if(!isHistorical) {
				c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.REPORT_DOWNTIME_RULE));
				c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.READING_ALARM_RULE));
				c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.CONTROL_ACTION_READING_ALARM_RULE));
				c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.MODULE_RULE));
				c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.ALARM_WORKFLOW_RULE));
				c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.PM_ALARM_RULE));
				c.addCommand(new FacilioCommand() {
					@Override
					public boolean executeCommand(Context context) throws Exception {
						context.remove(EventConstants.EventContextNames.EVENT_RULE_LIST);
						return false;
					}
				});
				c.addCommand((new ExecuteAllWorkflowsCommand(false, RuleType.ALARM_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION)));
			} else {
				c.addCommand(new ExecuteAutomatedRuleHistoryWorkflowsCommand());
			}

			c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.ALARM_ACTIVITY));
			return c;
		}

	public static FacilioChain getV2UpdateAlarmChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.REPORT_DOWNTIME_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.READING_ALARM_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.MODULE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.ALARM_WORKFLOW_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.PM_ALARM_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.READINGRULE_WO_ACTION_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false, RuleType.ALARM_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION));
		c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.ALARM_ACTIVITY));
		return c;
	}

	public static FacilioChain updateSensorRulesChain() {

		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateSensorRulesCommand());

		return c;
	}

		public static FacilioChain configureStoreNotificationsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AssociateFieldIdToStoreRuleTypeCommand());
			c.addCommand(addWorkflowRuleChain());
			c.addCommand(new AssociateWorkFlowRuleToStoreCommand());
			return c;
		}
		public static FacilioChain addMultiStoreRulesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddMultiWorkflowsCommand());
			return c;
		}

		public static FacilioChain updateMultiStoreRulesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateMultiWorkflowsCommand());
			return c;
		}



		public static FacilioChain getAddOrUdpateWorkorderServiceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderService());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new AddOrUpdateWorkorderServiceCommand());
			c.addCommand(new LoadWorkOrderServiceLookUpCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			return c;
		}

		public static FacilioChain getDeleteWorkorderServiceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderService());
			c.addCommand(new UpdateTransactionEventTypeCommand());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			return c;
		}

		public static FacilioChain getServicePriceForVendor() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetServicePriceForVendorCommand());
			return c;
		}

		public static FacilioChain getUseLineItemsForServiceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UsePOServiceLineItemsCommand());
			c.addCommand(getAddOrUdpateWorkorderServiceChain());
			c.addCommand(new UpdateUsedQuanityForPoServiceItemCommand());

			return c;
		}

		public static FacilioChain getRentalLeaseContractDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForRentalLeaseContract());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteRentalLeaseContractCommand());
			c.addCommand(new DeletePreferenceRulesOnRecordDeletionCommand());
			return c;
		}

		public static FacilioChain getUpdateRentalLeaseContractStatusChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForRentalLeaseContract());
			chain.addCommand(new UpdateBulkRentalLeaseContractStatusCommand());
			chain.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());
			return chain;
		}

		public static FacilioChain getAddRentalLeaseContractChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForRentalLeaseContract());
			chain.addCommand(new AddOrUpdateRentalLeaseContractCommand());
			chain.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());
			chain.addCommand(getRentalLeaseTotalCostChain()); //roll up for calculating total cost
			return chain;
		}

		public static FacilioChain getAddToolTypesVendorsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypeVendor());
			c.addCommand(new GenericAddModuleDataListCommand());
			return c;
		}

		public static FacilioChain getUpdateToolTypesVendorsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypeVendor());
			c.addCommand(new GenericUpdateListModuleDataCommand());
			return c;
		}

		public static FacilioChain getDeleteToolTypesVendorsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypeVendor());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static FacilioChain getRentalLeaseTotalCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new RentalLeaseContractTotalCostRollUpCommand());
			return c;
		}

		public static FacilioChain getWarrantContractTotalCostChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new WarrantyContractTotalCostRollUpCommand());
			return c;
		}


		public static FacilioChain getDuplicatePurchaseContract() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DuplicatePurchaseContractCommand());
			return c;
		}

		public static FacilioChain getDuplicateLabourContract() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DuplicateLabourContractCommand());
			return c;
		}

		public static FacilioChain getDuplicateWarrantyContract() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DuplicateWarrantyContractCommand());
			return c;
		}

		public static FacilioChain getDuplicateRentalLeaseContract() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DuplicateRentalLeaseContractCommand());
			return c;
		}

		public static FacilioChain getAddTermsAndConditionsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTermsAndConditions());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain getDeleteTermsAndConditionsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTermsAndConditions());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}


		public static FacilioChain getUpdateTermsAndConditionsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTermsAndConditions());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static FacilioChain getReturnAssetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ReturnAsssociatedAssetCommand());
			return c;
		}

		public static FacilioChain getPurchaseAssetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new PurchaseAssociatedAssetCommand());
			c.addCommand(getAddPurchaseOrderChain());
			c.addCommand(new UpdateAssociatedAssetContextCommand());
			return c;
		}

		public static FacilioChain getAssociateAssetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AssociateAssetToContractCommand());
			return c;
		}

//		public static FacilioChain executeJobChain() {
//			FacilioChain c = getDefaultChain();
//			c.addCommand(new ExecuteJobChainCommand());
//			return c;
//		}

		public static FacilioChain getActiveContractAssociatedAssetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetActiveContractAssociatedAssetsCommand());
			return c;
		}

		public static FacilioChain getChangeContractPaymentStatusChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ChangeContractPaymentStatusCommand());
			c.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateRecordRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddorUpdateRecordRuleCommand());
			c.addCommand(new AddScheduledJobForRecordCommand());
			return c;
		}

		public static FacilioChain getDeleteRecordRule() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteRecordRuleCommand());
			return c;
		}

		public static FacilioChain getAssociateTermsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AssociateTermsToContractCommand());
			return c;
		}

		public static FacilioChain getDisAssociateAssetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DisAssociateAssetToContractCommand());
			return c;
		}

		public static FacilioChain getDisAssociateTermsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DisAsscoiateTermsToContractCommand());
			return c;
		}

		public static FacilioChain getAddWarrantyContractLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWarrantyContractLineItems());
			c.addCommand(new AddOrUpdateWarrantyContractLineItemCommand());
			c.addCommand(getWarrantContractTotalCostChain()); //roll up for calculating total cost
			return c;
		}

		public static FacilioChain getAddRentalLeaseContractLineItem() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForRentalLeaseContractLineItem());
			c.addCommand(new AddOrUpdateRentalLeaseContractLineItemCommand());
			c.addCommand(getRentalLeaseTotalCostChain()); //roll up for calculating total cost
			return c;
		}
		public static FacilioChain getDeleteWarrantyContractLineItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWarrantyContractLineItems());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		public static FacilioChain getDeleteRentalLeaseContractLineItemChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForRentalLeaseContractLineItem());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteRecordRuleJobOnRecordDeletionCommand());
			return c;
		}

		public static FacilioChain getDeleteSingleRecordJobChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(new DeleteRecordRuleJobOnRecordDeletionCommand());
			return chain;
		}

		public static FacilioChain getAddOrUpdateDigestConfigChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddActionToDigestConfigCommand());
			//can use scheduleruleaction/wrte a special job to apply user scope
			c.addCommand(new AddDigestConfigActionCommand());
			//command can be added to add digest users rel table
			return c;
		}

		public static FacilioChain getActivateDigestConfigChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ActivateDeactivateDigestConfigCommand());
			return c;
		}

		public static FacilioChain getAllDigestConfigChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetAllDigestConfigCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateDigestMailConfigChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddActionToDigestConfigCommand());
			c.addCommand(new AddDigestConfigActionCommand());
			c.addCommand(new AddDigestConfigMetaCommand());
			//command can be added to add digest users rel table
			return c;
		}

		public static FacilioChain getDeleteDigestConfigChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteDigestConfigCommand());
			return c;
		}

		public static FacilioChain getDeleteScheduledActionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteScheduledActionCommand());
			return c;
		}

		public static FacilioChain getEnablePreference() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new EnablePreferenceCommand());
			c.addCommand(new AddEnabledPreferenceMetaCommand());

			return c;
		}

		public static FacilioChain getDisablePreference() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DisablePreferenceCommand());
			return c;
		}

		public static FacilioChain getAllPreferences() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetAllPreferencesCommand());
			return c;
		}

		public static FacilioChain getAllEnabledPreferences() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetAllEnabledPreferencesCommand());
			return c;
		}

		public static FacilioChain getAllEnabledDigestConfigChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetEnabledDigestConfigCommand());
			return c;
		}
		public static FacilioChain getV2AlarmOccurrenceCreateWO() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateWOForAlarmOccurrenceCommand());
			c.addCommand(getAddWorkOrderChain());
			c.addCommand(new UpdateWoIdInNewAlarmCommand());
			c.addCommand(new AddWorkOrderNotesFromAlarmCommand());
			c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.ALARM_ACTIVITY));
			return c;
		}

		public static FacilioChain getAddKPICategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddKPICategoryCommand());
			return c;
		}
		public static FacilioChain getUpdateKPICategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateKPICategoryCommand());
			return c;
		}
		public static FacilioChain getDeleteKPICategoryChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteKPICategoryCommand());
			return c;
		}

		public static FacilioChain getDeleteAlarmOccurrenceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteAlarmOccurrenceCommand());
			return c;
		}

		public static FacilioChain getRcaAlarmDetails() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetAlarmRcaDetailCommand());
			return c;
	    }

		public static FacilioChain getDeleteAlarmChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteAlarmCommand());
			return c;
		}

//		public static FacilioChain getExecuteFormActionRecursivelyRules() {
//			FacilioChain c = getDefaultChain();
//			c.addCommand(getExecuteFormActionRules());
//			c.addCommand(new ExecuteFormActionRulesForNextRoundCommand());
//			return c;
//		}

		public static FacilioChain getExecuteFormActionRules() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateAndFillFromRuleParamsCommand());
			c.addCommand(new ExecuteFormActionRulesCommand());
			return c;
		}

		public static FacilioChain getChangeStatusForFormRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ChangeFormRuleStatusCommand());
			return c;
		}



		public static FacilioChain getAddHistoricalVMCalculationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddHistoricalVMCalculationCommand());
			return c;
		}

		public static FacilioChain getAddFormRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddFormRuleCommand());
			c.addCommand(new AddFormRuleActionCommand());
			return c;
		}
		public static FacilioChain getUpdateFormRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateFormRuleCommand());
			c.addCommand(new DeleteFormRuleActionCommand());
			c.addCommand(new AddFormRuleActionCommand());
			return c;
		}
		public static FacilioChain getDeleteFormRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteFormRuleActionCommand());
			c.addCommand(new DeleteFormRuleCommand());
			return c;
		}

		public static FacilioChain fetchFormRuleDetailsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetFormRuleDetailsCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateFormValidationRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddOrUpdateFormValidationRuleCommand());
			return c;
		}

		public static FacilioChain getDeleteFormValidationRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteFormValidationRuleCommand());
			return c;
		}


		public static FacilioChain getUpdateStateFlowDiagramChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateStateFlowDiagramCommand());
			return c;
		}
		public static FacilioChain uploadImportFileChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UploadImportFileCommand());
			return c;
		}

		public static FacilioChain getExecuteControlActionCommandChain() {
			return getExecuteControlActionCommandChain(true);
		}

		public static FacilioChain getExecuteControlActionCommandChain(boolean addAction) {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FillRDMForControlActionCommand());
			c.addCommand(new FillDefaultValuesForControlActionCommand());
			if (addAction) {
				c.addCommand(new AddControlActionCommand());
			}
			c.addCommand(new PublishIOTMessageControlActionCommand());
			if (addAction) {
				c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			}
			return c;
		}

		public static FacilioChain getPushControlActionCommandChain() {

			FacilioChain c = getDefaultChain();
			c.addCommand(new PublishIOTMessageControlActionCommand());
			c.addCommand(new MarkPublishedCommandStatusCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			return c;
		}

		public static FacilioChain getExecuteControlActionCommandForControlGroupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FetchControlGroupCommand());
			c.addCommand(new GetControlActionCommandFromControlGroupCommand());
			c.addCommand(new FillRDMForControlActionCommand());
			c.addCommand(new FillDefaultValuesForControlActionCommand());
			c.addCommand(new AddControlActionCommand());
			c.addCommand(new PublishIOTMessageControlActionCommand());
			return c;
		}
		public static FacilioChain getAddAssetMovementChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForAssetMovement());
			chain.addCommand(new AssetMovementPropsSetCommand());
			chain.addCommand(new GenericAddModuleDataCommand());
			chain.addCommand(new ExecuteStateFlowCommand());
			chain.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			chain.addCommand(new AddDefaultChangeSetForCompleteMoveCommand());
			chain.addCommand(new CompleteAssetMoveCommand());
			chain.addCommand(new ConstructAddAssetMovementActivitiesCommand());
			chain.addCommand(new AddActivitiesCommand());
			return chain;
		}

		public static FacilioChain getUpdateAssetMovementChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForAssetMovement());
			chain.addCommand(new GenericUpdateModuleDataCommand());
			chain.addCommand(new UpdateStateForModuleDataCommand());
			chain.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			chain.addCommand(new CompleteAssetMoveCommand());
			chain.addCommand(new AddActivitiesCommand());
			return chain;
		}

		public static FacilioChain addReservationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateAndSetReservationPropCommand(true));
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(addReservationInternalAttendeeChain());
			c.addCommand(addReservationExternalAttendeeChain());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static FacilioChain updateReservationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateAndSetReservationPropCommand(false));
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new UpdateRecordRuleJobOnRecordUpdationCommand());
			c.addCommand(addReservationInternalAttendeeChain());
			c.addCommand(addReservationExternalAttendeeChain());
//			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		private static FacilioChain addReservationInternalAttendeeChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateInternalAttendeesCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
			return c;
		}

		private static FacilioChain addReservationExternalAttendeeChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateExternalAttendeesCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
			return c;
		}

		public static FacilioChain getImportDataChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ImportDataCommand());

			return c;
		}

		public static FacilioChain UploadImportPointsFileChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UploadImportPointsDataCommand());
			c.addCommand(new ImportPointsFieldsEvaluationCommand());
			c.addCommand(new UpdateImportPointsDataCommand());
			return c;
		}

		public static FacilioChain getImportReadingJobChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ImportReadingCommand());

			return c;
		}

		public static FacilioChain getExecuteHistoricalVMCalculation() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new HistoricalVMCalculationCommand());
			return c;
		}

		public static FacilioChain getExecuteHistoricalFormulaFieldCalculation() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new HistoricalFormulaFieldCalculationCommand());
			return c;
		}

		public static FacilioChain getExecuteHistoricalRuleCalculation() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new HistoricalRunForReadingRuleCommand());
			return c;
		}

		public static FacilioChain getExecuteHistoricalAlarmOccurrenceDeletion() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new HistoricalAlarmOccurrenceDeletionCommand());
			return c;
		}

		public static FacilioChain getExecuteHistoricalEventRuleCalculation() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new HistoricalRuleEventRunCommand());
			return c;
		}

		public static FacilioChain getExecuteHistoricalRuleAlarmProcessing() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new HistoricalAlarmProcessingCommand());
			return c;
		}

		public static FacilioChain getExecuteLiveEventsToAlarmProcessingJob() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecuteLiveEventsToAlarmProcessingCommand());
			return c;
		}

		public static FacilioChain getExecuteFormulaFieldJobCalculationCommand() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new FormulaFieldJobCalculationCommand());
			return c;
		}

		public static FacilioChain getScheduleFormulaFieldParentJobCommand() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ScheduleFormulaFieldParentJobCommand());
			return c;
		}

	public static FacilioChain getAddModuleWorkflowRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ModuleWorkflowRuleCommand());
		chain.addCommand(addWorkflowRuleChain());
		return chain;
	}

	public static FacilioChain getUpdateModuleWorkflowRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ModuleWorkflowRuleCommand());
		chain.addCommand(updateWorkflowRuleChain());
		return chain;
	}

	public static FacilioChain getAddControllerChain(boolean fromAgent) {
		FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddRtuNetworkCommand());
		chain.addCommand(new SetAssetCategoryCommand());
		chain.addCommand(new GenericAddModuleDataCommand());
		chain.addCommand(FacilioChainFactory.getCategoryReadingsChain());
		chain.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		// If message is from agent, controllers will be already added in agent and only need to add in facilio.
		// No need to send to agent again
		if (!fromAgent) {
			chain.addCommand(new AddAgentServiceControllerCommand());
		}
		//chain.addCommand(new AddControllerCommand());
		return chain;
	}

	public static FacilioChain getAddPointsChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddPointsCommand());
		chain.addCommand(new MLTagPointListCommand());
		return chain;
	}

	public static FacilioChain getUpdateControllerChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GenericUpdateModuleDataCommand());
		return chain;
	}

	public static FacilioChain deleteControllerChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteResourceCommand());
		return chain;
	}

	public static FacilioChain getRtuControllerChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetRtuNetworkCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}

	public static FacilioChain getControllerChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}

	public static FacilioChain generateScheduleChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new BlockPMEditOnWOGeneration(false, false, true));
		c.addCommand(new SchedulePMWorkOrderGenerationCommand());
		return c;
	}

	public static FacilioChain getAssociateTermsToPOChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AssociateTermsToPOCommand());
		return c;
	}
	public static FacilioChain getAssociateTermsToPRChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AssociateTermsToPRCommand());
		return c;
	}

	public static FacilioChain getDisAssociateTermsToPOChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DisAssociateTermsFromPoCommand());
		return c;
	}

	public static FacilioChain getDisAssociateTermsToPRChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DisAssociateTermsFromPrCommand());
		return c;
	}

	public static FacilioChain getAcknowledgeMessageChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AcknowledgeMessageCommand());
		return c;
	}

		public static FacilioChain addDeviceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateCodeAndGetDeviceMetaCommand());
			c.addCommand(getAddAssetChain());
			c.addCommand(new ConnectDeviceCommand());
			c.addCommand(getUpdateAssetChain());
			return c;
		}
	    public static FacilioChain connectDeviceChain()
	    {
	    	FacilioChain c = getDefaultChain();

			c.addCommand(new ValidateCodeAndGetDeviceMetaCommand());
			c.addCommand(new ConnectDeviceCommand());
			c.addCommand(getUpdateAssetChain());
			return c;

	    }

	    public static FacilioChain getDeleteDeviceChain()
	    {
	    	FacilioChain c = getDefaultChain();

			c.addCommand(FacilioChainFactory.getDeleteAssetChain());
			c.addCommand(new DisconnectDeviceCommand());
			return c;

	    }

	    public static FacilioChain getDisconnectDeviceChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new DisconnectDeviceCommand());
	    	c.addCommand(getUpdateAssetChain());
	    	return c;
	    }


	    public static FacilioChain addCustomFilterChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new GenerateCriteriaFromFilterCommand());
	    	c.addCommand(new AddCustomFilterCommand());
	    	return c;
	    }

	    public static FacilioChain updateCustomFilterChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new GenerateCriteriaFromFilterCommand());
	    	c.addCommand(new UpdateCustomFilterCommand());
	    	return c;
	    }

	    public static FacilioChain deleteCustomFilterChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new DeleteCustomFilterCommand());
	    	return c;
	    }

	    public static FacilioChain addViewManagerPropertiesChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new DeleteQuickFiltersCommand());
	    	c.addCommand(new DeleteAllCustomFiltersCommand());
	    	c.addCommand(new AddViewManagerPropertiesCommand());
	    	return c;
	    }

	    public static FacilioChain fetchQuickFilterChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new FetchQuickFilterCommand());
	    	return c;
	    }

	    public static FacilioChain deleteQuickFilterChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new DeleteQuickFiltersCommand());
	    	return c;
	    }

	    public static FacilioChain addViewGroupChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new AddViewGroupCommand());
	    	return c;
	    }

	    public static FacilioChain updateViewGroupChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new EditViewGroupCommand());
	    	return c;
	    }

	    public static FacilioChain deleteViewGroupChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new DeleteViewGroupCommand());
	    	return c;
	    }

		public static FacilioChain addVisitorChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVisitor());
			c.addCommand(new CheckForVisitorDuplicationCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION))
					.addCommand(new VisitorFaceRecognitionCommand()));

			return c;
		}

		public static FacilioChain updateVisitorChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVisitor());
			c.addCommand(new CheckForVisitorDuplicationCommand());
			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));


			return c;
		}

		public static FacilioChain addVisitorInvitesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVisitorInvites());
			c.addCommand(new ComputeScheduleForVisitorInviteCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));

			return c;
		}

		public static FacilioChain updateVisitorInvitesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVisitorInvites());
			c.addCommand(new DeleteVisitorInviteRelCommand());
			c.addCommand(new AddNewVisitorsWhilePreRegisteringCommand());
			c.addCommand(new ComputeScheduleForVisitorInviteCommand());
			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));
			c.addCommand(new LoadInviteIdAfterAdditionCommand());
			c.addCommand(new AddInviteVisitorRelCommand());
			c.addCommand(SetTableNamesCommand.getForVisitorInviteRel());
			c.addCommand(new GenericAddModuleDataListCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));

			return c;
		}

		public static FacilioChain addInviteesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddNewVisitorsForInviteCommand());
			c.addCommand(addVisitorChain());
			c.addCommand(new AddInviteVisitorRelCommand());
			c.addCommand(SetTableNamesCommand.getForVisitorInviteRel());
			c.addCommand(new GenerateQrInviteUrlCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));


			return c;
		}

		public static FacilioChain addVisitorLoggingRecordsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVisitorLogging());
			c.addCommand(new AddNewVisitorWhileLoggingCommand());
			c.addCommand(new AddOrUpdateVisitorFromVisitsCommand());
			c.addCommand(new CheckForWatchListRecordCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
//			c.addCommand(new AddAttachmentCommand());
//			c.addCommand(new AttachmentContextCommand());
//			c.addCommand(new AddAttachmentRelationshipCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new UpdateVisitorInviteRelArrivedStateCommand());
			c.addCommand(new ChangeVisitorInviteStateCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
							.addCommand(new AddNdaForVisitorLogCommand())
							.addCommand(new GenerateQrInviteUrlCommand())
							.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION))
							.addCommand(new VisitorFaceRecognitionCommand()));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());

			return c;
		}

		public static FacilioChain addRecurringVisitorLoggingRecordsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVisitorLogging());
			c.addCommand(new AddNewVisitorWhileLoggingCommand());
			c.addCommand(new AddOrUpdateVisitorFromVisitsCommand());
			c.addCommand(new CheckForWatchListRecordCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
			c.addCommand(new GenerateQrInviteUrlCommand());
			c.addCommand(new AddOrUpdateVisitorLogTriggerCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new SchedulePreOpenVisitorLogsCreateCommand(false));
			c.addCommand(new UpdateVisitorInviteRelArrivedStateCommand());
			c.addCommand(new ChangeVisitorInviteStateCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());

			return c;
		}

		public static FacilioChain updateVisitorLoggingRecordsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVisitorLogging());
			c.addCommand(new AddOrUpdateVisitorFromVisitsCommand());
			c.addCommand(new GenericUpdateListModuleDataCommand());
//			c.addCommand(new AddAttachmentCommand());
//			c.addCommand(new AttachmentContextCommand());
//			c.addCommand(new AddAttachmentRelationshipCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ChangeVisitorInviteStateCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			return c;
		}

		public static FacilioChain updateRecurringVisitorLoggingRecordsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVisitorLogging());
			c.addCommand(new AddOrUpdateVisitorFromVisitsCommand());
			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new AddOrUpdateVisitorLogTriggerCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new SchedulePreOpenVisitorLogsCreateCommand(true));
			c.addCommand(new ChangeVisitorInviteStateCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));

			return c;
		}

		public static FacilioChain preRegisterVisitorsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVisitorInvites());
			c.addCommand(new AddNewVisitorsWhilePreRegisteringCommand());
			c.addCommand(new ComputeScheduleForVisitorInviteCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));
			c.addCommand(new LoadInviteIdAfterAdditionCommand());
			c.addCommand(new AddInviteVisitorRelCommand());
			c.addCommand(SetTableNamesCommand.getForVisitorInviteRel());
			c.addCommand(new GenerateQrInviteUrlCommand());
			c.addCommand(new GenericAddModuleDataListCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());

			return c;
		}


	public static FacilioChain getCreateStateFlowDraftChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new CreateStateFlowDraftCommand());
		chain.addCommand(new CreateCopyOfStateFlowCommand());
		return chain;
	}

	public static FacilioChain getCloneStateFlowChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetStateFlowCommand());
		chain.addCommand(new CreateClonedStateFlowCommand());
		chain.addCommand(new CreateCopyOfStateFlowCommand());
		return chain;
	}

	public static FacilioChain getPublishStateFlowChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new PublishStateFlowCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateCustomButtonChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateCustomButtomCommand());
		return chain;
	}

	public static FacilioChain getDeleteCustomButtonChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetWorkflowRuleCommand());
		chain.addCommand(new WorkflowRuleDeleteCommand());
		return chain;
	}

	public static FacilioChain getUpdateSystemButtonChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateSystemButtonCommand());
		return chain;
	}


	public static FacilioChain addContactsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForContacts());
		c.addCommand(new CheckForContactDuplicationCommand());
		//c.addCommand(new AddContactsAsRequesterCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdateVendorPrimaryContactLookUpCommand());
		c.addCommand(new SendEventToDeviceCommand());

		return c;
	}

	public static FacilioChain updateContactsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForContacts());
		c.addCommand(new CheckForContactDuplicationCommand());
		//c.addCommand(new UpdateContactsRequesterCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdateVendorPrimaryContactLookUpCommand());

		return c;
	}

	public static FacilioChain getAddOrUpdateServiceCatalogChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateServiceCatalogCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateServiceCatalogGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateServiceCatalogGroupCommand());
		return chain;
	}

//	public static FacilioChain getDeleteFieldDeviceChain() {
//		FacilioChain chain = getDefaultChain();
//		chain.addCommand(new DeleteFieldDevice());
//		return chain;
//	}

	/**
	 * makes controller entry from FieldDeviceIds
	 * 	agentId not mandatory
	 * 	[deviceIds] to select devices or select all devices.
	 * @return
	 */
	public static FacilioChain getConfigurePointAndProcessControllerV2Chain() {
		FacilioChain chain = getDefaultChain();
		//chain.addCommand(new EditPointCommand());
//		chain.addCommand(new getFieldDevicesCommand()); // aI not mandatory, ids - not mandatory(gets all devices)
//		chain.addCommand(new FieldDevicesToControllerCommand());
		chain.addCommand(new ConfigurePointCommand());
		//chain.addCommand(new DeleteFieldDevice());
		return chain;
	}

	public static FacilioChain getaddAndPublishIotMessageChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddAndSendIotMessageCommand());
		return chain;
	}
	public static FacilioChain getAddVisitorTypePicklistOption() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVisitorType());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new SendEventToDeviceCommand());
		return c;
	}

	public static FacilioChain getUpdateVisitorTypePicklistOptionChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVisitorType());
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new SendEventToDeviceCommand());
		return c;
	}

	public static FacilioChain getDeleteVisitorTypePicklistOptionChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVisitorType());
		c.addCommand(new GenericDeleteModuleDataCommand());
		return c;
	}


	public static FacilioChain getAddVisitorTypeSettingChain()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new AddVisitorTypeCommand());
		c.addCommand(new SendEventToDeviceCommand());
		return c;
	}
	public static FacilioChain getUpdateVisitorTypeSettingChain()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new UpdateVisitorSettingsCommand());
		c.addCommand(new SyncVisitorSettingsWithFormCommand());
		c.addCommand(new SendEventToDeviceCommand());
		return c;
	}


	public static FacilioChain addInsurancesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInsurance());
		c.addCommand(new AssociateVendorToInsuranceFromRequesterCommand());
		c.addCommand(new ValidateDateCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));

		return c;
	}

	public static FacilioChain updateInsurancesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInsurance());
		c.addCommand(new ValidateDateCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));

		return c;
	}

	public static FacilioChain addWatchListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWatchList());
		c.addCommand(new CheckForExisitingWatchlistRecordsCommand());
		return c;
	}

	public static FacilioChain updateWatchListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWatchList());
		c.addCommand(new GenericUpdateListModuleDataCommand());

		return c;
	}

	public static FacilioChain addWorkPermitRecordsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkPermit());
		c.addCommand(new ComputeScheduleForWorkPermitCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new GenericAddSubModuleDataCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
		c.addCommand(new RollUpWorkOrderFieldOnWorkPermitApprovalCommand());

		return c;
	}

	public static FacilioChain updateWorkPermitRecordsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkPermit());
		c.addCommand(new ComputeScheduleForWorkPermitCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new LoadWorkPermitLookUpsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
		c.addCommand(new VerifyApprovalCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
		c.addCommand(new RollUpWorkOrderFieldOnWorkPermitApprovalCommand());

		return c;
	}

	public static FacilioChain editRDMWritableChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new EditRDMWritableableCommand());
		return c;
	}

    public static FacilioChain getEditPointChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new EditPointCommand());
        return c;
	}

	public static FacilioChain getDeleteServiceCatalogChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteServiceCatalogCommand());
		return chain;
	}

	public static FacilioChain addOrUpdatePrinterChain()
	{
		FacilioChain chain =getDefaultChain();
		chain.addCommand(new AddOrUpdatePrinterCommand());
		chain.addCommand(new SendMessageOnPrinterChangeCommand());
		return chain;
	}



	public static FacilioChain addOrUpdateVisitorKioskChain()
	{
		FacilioChain chain =getDefaultChain();
		chain.addCommand(new AddOrUpdateVisitorKioskCommand());
		return chain;
	}
	public static FacilioChain getDeletePrinterChain() {
		FacilioChain c = getDefaultChain();
		//for delete sending message before actual delete as ,printer ID will not be in db on delete
		c.addCommand(new SendMessageOnPrinterChangeCommand());
		c.addCommand(new DeletePrinterCommand());

		return c;
	}

	public static FacilioChain getDeleteServiceCatalogGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteServiceCatalogGroupCommand());
		return chain;
	}

	public static FacilioChain getAckProcessorChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand( new AckIotMessageCommand());
		return chain;
	}

	public static FacilioChain resetControllerChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new SendResetCommand());
		return chain;
	}

	public static FacilioChain deletepointsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeletePointCommand());
		chain.addCommand(new SendRemovePointsCommand());
		return chain;
	}

	public static FacilioChain unconfigurePointsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UnConfigurePointsCommand());
		return chain;
	}

	public static FacilioChain subscribeUnsbscribechain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new SubscribeUnsbscribeCommand());
		chain.addCommand(new MLTagPointListCommand());
		return chain;
	}
	
	/**
	 * makes point's config status to in-progress and makes controllerId entry for point and childPoint.
	 * @return
	 */
	public static FacilioChain updatePointsConfigured() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdatePointsConfiguredCommand());
		return chain;
	}

	public static FacilioChain pointsConfigurationComplete(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new PointsConfigurationCompleteCommand());
		chain.addCommand(new MLTagPointListCommand());
		return chain;
	}

	public static FacilioChain addOccupantsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForOccupants());
		c.addCommand(new CheckForOccupantDuplicationCommand());
		c.addCommand(new AddOccupantAsRequesterCommand());
		c.addCommand(new GenericAddModuleDataListCommand());

		return c;
	}

	public static FacilioChain updateOccupantsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForOccupants());
		c.addCommand(new CheckForOccupantDuplicationCommand());
		c.addCommand(new UpdateOccupantsRequestercommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());

		return c;
	}

	public static FacilioChain getAddOrUpdateApplication() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateApplicationCommand());
		return chain;
	}
	public static FacilioChain getAddApplicationRelatedAppsChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddApplicationRelatedAppsCommand());
		return chain;
	}
	public static FacilioChain markApplicationAsDefault() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new MarkApplicationAsDefaultCommand());
		return chain;
	}

	public static FacilioChain getDeleteApplicationsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteApplicationCommand());
		return chain;
	}



	public static FacilioChain getAddOrUpdateTabGroup() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateTabGroupCommandValidationCommand());
		chain.addCommand(new AddOrUpdateTabGroupCommand());
		return chain;
	}

	public static FacilioChain getReorderTabGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ReorderTabGroupCommand());
		return chain;
	}

	public static FacilioChain getReorderTabChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ReorderTabCommand());
		return chain;
	}

	public static FacilioChain getDeleteTabGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteTabGroupCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateTabChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateTabCommand());
		chain.addCommand(new AddNewPermissionCommand());
		chain.addCommand(new AddModuleAppPermissionForTabCommand());
		return chain;
	}

	public static FacilioChain getCreateAndAssociateTabGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new CreateWebTabWebGroupCommand());
		chain.addCommand(new AssociateTabGroupCommand());
		return chain;
	}

	public static FacilioChain getUpdateTabsToGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteTabForGroupCommand());
		chain.addCommand(new CreateWebTabWebGroupCommand());
		chain.addCommand(new AssociateTabGroupCommand());
		return chain;
	}

	public static FacilioChain getDisAssociateTabGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DisAssociateTabGroupCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateApplicationLayoutChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateApplicationLayoutCommand());
		return chain;
	}

	public static FacilioChain getDeleteApllicationRelatedAppsChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteApplicationRelatedAppsCommand());
		return chain;
	}
	public static FacilioChain getAddNewPermissionChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddNewPermissionCommand());
		chain.addCommand(new AddModuleAppPermissionForTabCommand());
		return chain;
	}

	public static FacilioChain getDeleteNewPermissionChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteNewPermissionCommand());
		return chain;
	}
	public static FacilioChain getDeleteTabChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteTabCommand());
		return chain;
	}

	public static FacilioChain getAddRequesterChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddRequesterCommand());
		return c;
	}

	public static FacilioChain getagentV2DataSqlite() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AgentSqliteMakerCommand());
		return chain;
	}


	/*public static FacilioChain getAddRtuChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddRtuNetworkCommand());
		return chain;
	}*/

	public static FacilioChain createAgentChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new CreateAgentCommand());
		return chain;
	}

	public static FacilioChain FlushIntentAndModelChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteChatBotIntentAndModel());
		c.addCommand(getpopulateDefaultChatBotIntentChain());
		return c;
	}

	public static FacilioChain HandleChatBotMessageChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new HandleChatBotMessageCommand());
		return c;
	}

	public static FacilioChain HandleChatBotSessionChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetOrAddCurrentActiveModel());
		c.addCommand(new PrepareChatBotForMlAPICommand());
		c.addCommand(new SendToMlApiForSessionCommand());
		c.addCommand(new CheckAccuracyAndFetchIntentCommand());
		c.addCommand(new HandleInvalidQueryForSessionMessage());
		c.addCommand(new HandleIntentParamsForSessionCommand());
		c.addCommand(new HandleContextWorkflowCommand());
		c.addCommand(new ExecuteActionAndSetResponseForSessionCommand());
		c.addCommand(new FetchSugestionForChatBotIntent());
		c.addCommand(new UpdateCBSessionCommand());
		c.addCommand(new UpdateCBConversationCommand());
		return c;
	}

	public static FacilioChain HandleChatBotSessionConversationChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetOrAddCurrentActiveModel());
		c.addCommand(new PrepareChatBotForMlAPICommand());
		c.addCommand(new SendToMlApiForConversationCommand());
		c.addCommand(new HandleTerminateSessionCommand());
		c.addCommand(new HandleInvalidQueryMessageForConversations());
		c.addCommand(new HandleContextWorkflowFillables());
		c.addCommand(new HandleIntentParamsForConversationCommand());
		c.addCommand(new HandleEditParamFromSuggestionForConversations());
		c.addCommand(new HandleAddParamFromSuggestionForConversations());
		c.addCommand(new HandleContextWorkflowCommand());
		c.addCommand(new RemoveContextWorkflowFillables());
		c.addCommand(new ExecuteActionAndSetResponseForConversationCommand());
		c.addCommand(new FetchSugestionForChatBotIntent());
		c.addCommand(new UpdateCBSessionCommand());
		c.addCommand(new UpdateCBConversationCommand());
		return c;
	}

	public static FacilioChain getAddChatBotIntentChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetOrAddCurrentActiveModel());
		c.addCommand(new AddChatBotIntentCommand());
		return c;
	}

	public static FacilioChain getAddOrUpdateChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddChatBotIntentCommand());
		return c;
	}

	public static FacilioChain getUpdateVisitorFormsChain()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(getUpdateFormListFieldsChain());
		c.addCommand(new SendEventToDeviceCommand());
		return c;
	}

	public static FacilioChain getAddBulkWorkOrderChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddBulkWOCommand());
		return chain;
	}

	public static FacilioChain addDocumentsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddDocumentCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));

		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));

		return c;
	}

	public static FacilioChain updateDocumentsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateDocumentsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));


		return c;
	}

	public static FacilioChain getAddOrUpdateSLAChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateSLACommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateSLAEntityChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateSLAEntityCommand());
		return chain;
	}

	public static FacilioChain deleteSLAEntityChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteSLAEntityCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateSLAPolicyChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateSLAPolicyCommand());
		return chain;
	}

	public static FacilioChain getBulkAddOrUpdateSLAChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateBulkSLACommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateUserDelegationChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateDelegateCommand());
		chain.addCommand(new ForkChainToInstantJobCommand(false)
				.addCommand(new SendDelegationMailCommand()));
		return chain;
	}

	public static FacilioChain getDeleteUserDelegationChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteUserDelegationCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateRelationChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateRelationCommand());
		return chain;
	}

	public static FacilioChain getDeleteRelationChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteRelationCommand());
		return chain;
	}

	public static FacilioChain getCloseBannerChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new CloseBannerCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateScoringRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateScoringRuleCommand());
		return chain;
	}

	public static FacilioChain getReorderWorkflowRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetCustomModuleWorkflowRulesCommand());
		chain.addCommand(new ReOrderWorkflowCommand());
		return chain;
	}

	public static FacilioChain getAddSLAPolicyEscalationsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddSLAPolicyEscalationsCommand());
		return chain;
	}

	public static FacilioChain addSafetyPlansChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForSafetyPlan());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new GenericAddSubModuleDataCommand());

		return c;
	}

	public static FacilioChain updateSafetyPlansChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForSafetyPlan());
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new GenericAddSubModuleDataCommand());

		return c;
	}

	public static FacilioChain addHazardChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForHazard());
		c.addCommand(new GenericAddModuleDataCommand());
		c.addCommand(new GenericAddSubModuleDataCommand());

		return c;
	}

	public static FacilioChain updateHazardChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForHazard());
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new GenericAddSubModuleDataCommand());

		return c;
	}

	public static FacilioChain addPrecautionChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPrecaution());
		c.addCommand(new GenericAddModuleDataListCommand());

		return c;
	}

	public static FacilioChain updatePrecautionChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPrecaution());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		return c;
	}

	public static FacilioChain getAddOrUdpateSafetyPlanHazardChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderLabour());
		c.addCommand(new AddOrUpdateWorkorderLabourCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand());
		c.addCommand(new AddOrUpdateWorkorderCostCommand());
		c.addCommand(new UpdateWorkorderTotalCostCommand());
		return c;
	}

	public static FacilioChain addSafetyPlanHazardChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForSafetyPlanHazards());
		c.addCommand(new GenericAddModuleDataListCommand());

		return c;
	}

	public static FacilioChain addHazardPrecautionListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForHazardPrecaution());
		c.addCommand(new GenericAddModuleDataListCommand());

		return c;
	}

	public static FacilioChain addWorkorderHazardListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderHazard());
		c.addCommand(new GenericAddModuleDataListCommand());

		return c;
	}
	public static FacilioChain addAssetHazardListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAssetHazard());
		c.addCommand(new GenericAddModuleDataListCommand());

		return c;
	}

	public static FacilioChain addClientsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForClient());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdateClientIdInSiteCommand());
		c.addCommand(new AddClientUserCommand());
		return c;
	}

	public static FacilioChain updateClientsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForClient());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdateClientIdInSiteCommand());
		return c;
	}
//		c.addCommand(new AddClientUserCommand());
	public static FacilioChain addFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddFloorPlanCommand());

		return c;
	}
	public static FacilioChain getFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new getFloorPlanCommand());
		return c;
	}

	public static FacilioChain addPeopleChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPeople());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new AddPeopleAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		return c;
	}

	public static FacilioChain addTenantContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTenantContact());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new CheckForMandatoryTenantIdCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		c.addCommand(new UpdateTenantAppPortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());



		return c;
	}

	public static FacilioChain addVendorContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendorContact());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new CheckForMandatoryVendorIdCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		c.addCommand(new UpdateVendorContactAppPortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());

		return c;
	}

	public static FacilioChain addEmployeeChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForEmployee());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new AddPeopleTypeForEmployeeCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdateEmployeePeopleAppPortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());


		return c;
	}
	public static FacilioChain getListOfFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetFloorPlansCommand());

		return c;
	}

	public static FacilioChain updateTenantContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTenantContact());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		c.addCommand(new UpdateTenantAppPortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());


		return c;
	}

	public static FacilioChain updateVendorContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendorContact());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		c.addCommand(new UpdateVendorContactAppPortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());


		return c;
	}

	public static FacilioChain updateEmployeeChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForEmployee());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		c.addCommand(new UpdateEmployeePeopleAppPortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));




		return c;
	}

	public static FacilioChain updatePeopleChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPeople());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new AddPeopleAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		c.addCommand(new UpdateContactDirectoryCommand());


		return c;
	}

	public static FacilioChain updateEmployeeAppAccessChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForEmployee());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdateEmployeePeopleAppPortalAccessCommand());
		return c;
	}

	public static FacilioChain updateVendorContactAppAccessChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendorContact());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdateVendorContactAppPortalAccessCommand());
		return c;
	}

	public static FacilioChain updateTenantContactAppAccessChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTenantContact());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdateTenantAppPortalAccessCommand());
		return c;
	}

	public static FacilioChain updatePeopleAppAccessChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPeople());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new AddPeopleAccessCommand());
		return c;
	}


	public static FacilioChain addOrUpdateFeedbackKioskChain()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new AddOrUpdateFeedbackKioskCommand());

		return c;
	}
	public static FacilioChain addOrUpdateSmartControlKioskChain()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new AddOrUpdateSmartControlKioskCommand());

		return c;
	}
	public static FacilioChain addOrUpdateFeedbackTypeChain()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new AddOrUpdateFeedbackTypeCommand());

		return c;
	}

	public static FacilioChain updateFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new updateFloorPlanCommand());

		return c;
	}

	public static FacilioChain deleteFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new deleteFloorPlanCommand());

		return c;
	}

	public static FacilioChain getAllFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new getAllFloorPlanCommand());

		return c;
	}


	public static FacilioChain disassociateClientFromSiteChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DisassociateClientFromSiteCommand());
		return c;
	}

	public static FacilioChain associateClientFromSiteChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AssociateClientWithSiteCommand());
		return c;
	}

	public static FacilioChain getControllerDataChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetControllerCommand());
		return chain;
	}

	public static FacilioChain getAddSLAPolicyWithChildrenChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateSLAPolicyWithChildrenCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateRatingChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateRatingCommand());
		chain.addCommand(new GenericAddModuleDataCommand());
		return chain;
	}

	public static FacilioChain getUpdateStateTransitionStateChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateStateTransitionStateCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateEmailStructureChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateEmailStructureCommand());
		return chain;
	}

	public static FacilioChain getDeleteEmailStructureChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteEmailStructureCommand());
		return chain;
	}

	public static FacilioChain getPublishEmailStructureChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new PublishEmailStructureCommand());
		return chain;
	}

	public static FacilioChain updateControllableTypeForSpace() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateControllableTypeForSpace());
		return chain;
	}

	public static FacilioChain updateControllableTypeForFloor() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetIncludedSpaceListCommand());
		chain.addCommand(new UpdateControllableTypeForSpaceList());
		return chain;
	}

	public static FacilioChain getExecuteControlActionCommandForSpaceChain() {

		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetControlActionCommandsForSpaceCommand());
		chain.addCommand(TransactionChainFactory.getExecuteControlActionCommandChain());
		return chain;
	}

	public static FacilioChain getExecuteControlActionCommandForFloorChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetIncludedSpaceListCommand());
		chain.addCommand(new GetExecuteControlActionCommandForSpaceList());
		return chain;
	}

	public static FacilioChain getAddPointChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddPointCommand());
		return c;
	}

	public static FacilioChain uploadBimFileChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new UploadBimFileCommand());
		c.addCommand(new ValidateBimFileSheetsCommand());
		c.addCommand(new AddBimIntegrationLogCommand());
		return c;
	}

	public static FacilioChain importBimFileSheetsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateModulesNoInBimIntegrationLogCommand());
		c.addCommand(new ImportBimFileSheetsCommand());
		return c;
	}

	public static FacilioChain getbimImportUpdateChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddBimDefaultValuesCommand());
		c.addCommand(new UpdateBimIntegrationLogCommand());
		return c;
	}

	public static FacilioChain getPMImportDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new PMImportDataCommand());
		c.addCommand(TransactionChainFactory.getbimImportUpdateChain());
		return c;
	}


	public static FacilioChain getNextApprovalStateChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new ChangeApprovalStatusForModuleDataCommand());
		return chain;
	}

	public static FacilioChain upgradeAgentChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpgradeAgentCommand());
		chain.addCommand(new AddAndSendIotMessageCommand());
		return chain;
	}

	public static FacilioChain getAddCommissioningChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddCommissioningLogCommand());
		return chain;
	}

	public static FacilioChain getUpdateCommissioningChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateCommissioningCommand());
		return chain;
	}

	public static FacilioChain getPublishCommissioningChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new PublishCommissioningCommand());
		chain.addCommand(new MLUpdateTaggedPointsCommand());
		return chain;
	}

	public static FacilioChain MlCommissionedPointsMigration(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new CommissionedPointsMLMigration());
		chain.addCommand(new ConfiguredPointsMlMigration());
//		chain.addCommand(new MLUpdateTaggedPointsCommand());

//		chain.addCommand(new MLTagPointListCommand());
		return chain;
	}


	public static FacilioChain getDeleteCommissioningChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteCommissioningCommand());
		return chain;
	}

	public  static  FacilioChain migrateFieldDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new MigrateFieldReadingDataCommand());
		return c;
	}

	public static FacilioChain addFieldMigrationJob() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddFieldMigrationJobCommand());
		return c;

	}


	public static FacilioChain deleteAssetReadings () {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteAssetReadingCommand());
		return c;
	}

	public static FacilioChain shiftAssetReadings() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ShiftAssetReadingCommand());
		return c;
	}

	public static FacilioChain getAddOrUpdateApprovalRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateApprovalRuleCommand());
		return chain;

	}
	public static FacilioChain getExecuteOperationAlarm()
	{
		FacilioChain c=getDefaultChain();
		// c.addCommand(new OperationAlarmCommand());
		c.addCommand(new ExecuteOperationalEventCommand());
		return c;
	}
	public static FacilioChain addClientContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForClientContact());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new CheckForMandatoryClientIdCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		c.addCommand(new UpdateClientAppPortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());


		return c;
	}

	public static FacilioChain updateClientContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForClientContact());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		c.addCommand(new UpdateClientAppPortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());


		return c;
	}

	public static FacilioChain updateClientContactAppAccessChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForClientContact());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdateClientAppPortalAccessCommand());
		return c;
	}

	public static FacilioChain getUpdatePeoplePrimaryContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchPeopleModuleRecordsCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());

		return c;
	}
	public static FacilioChain getExecuteHistoricalRunOpAlarm()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new HistoricalRunOperationalAlarmLog());
		c.addCommand(new HistoricalOperationAlarmOccurencesDeletionCommand());
		return c;
	}
	public static FacilioChain getExecuteHistoricalOperationalAlarmProcessing() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new HistoricalOperationalAlarmProcessingCommand());
		return c;
	}
	public static FacilioChain getImportRollupTenantSpacesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new RollupTenantSpacesCommand());

		return c;
	}

	public static FacilioChain getImportRollupTenantSpacesWhileUpdatingChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new RollupTenantSpacesWhileUpdatingCommand());

		return c;
	}

	public static FacilioChain getActivateAssetDepreciationChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ActivateOrDeActivateAssetDepreciationCommand());
		return c;
	}

	public static FacilioChain addApplicationUsersChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddApplicationUsersCommand());
		c.addCommand(new AddEmployeeTypePeopleForUserAdditionCommand());
		c.addCommand(new AddorUpdateUserScopingCommand());
		c.addCommand(new AddOrUpdatePermissionSetsForPeopleCommand());
		return c;
	}

	public static FacilioChain deleteApplicationUsersChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteApplicationUsersCommand());

		return c;
	}
	
	public static FacilioChain addorUpdateUserSignatureChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddOrUpdateUserSignatureCommand());

		return c;
	}
	public static FacilioChain getUserSignatureChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetUserSignatureCommand());

		return c;
	}
	public static FacilioChain deleteUserSignatureChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteUserSignatureCommand());

		return c;
	}

	public static FacilioChain enableEnergyStarChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetEnergyStarCustomerCommand());
		c.addCommand(new EnergyStarEnableCommand());
		return c;
	}
	public static FacilioChain addEnergyStarProperyChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetEnergyStarCustomerCommand());
		c.addCommand(new AddEnergyStarPropertyCommand());
		c.addCommand(new AddEnergyStarPropertyUseCommand());
		c.addCommand(new AddEnergyStarMeterCommand());
		return c;
	}

	public static FacilioChain addEnergyStarSyncJobChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetEnergyStarCustomerCommand());
		c.addCommand(new AddEnergyStarSyncJobCommand());
		return c;
	}

	public static FacilioChain doEnergyStarSyncChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetEnergyStarCustomerCommand());
		c.addCommand(new ConfirmESPropertyShareCommand());
		c.addCommand(new ConfirmESMeterShareCommand());
		c.addCommand(ReadOnlyChainFactory.getESfetchSetupData());
		c.addCommand(new EnergyStarSyncPropertyMetaCommand());
		c.addCommand(new EnergyStarSyncMeterDataCommand());
		c.addCommand(new EnergyStarSyncPropertyDataCommand());
		return c;
	}

	public static FacilioChain addEnergyStarProperyOnlyChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddEnergyStarPropertyCommand());
		c.addCommand(new PrepareEnergyStarPropertyDataRDMEntry());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}

	public static FacilioChain addEnergyStarMeterOnlyChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddEnergyStarMeterCommand());
		c.addCommand(new PrepareEnergyStarMeterDataRDMEntry());
		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
		return c;
	}

	public static FacilioChain updateEnergyStarPropertyChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetEnergyStarCustomerCommand());
		c.addCommand(new UpdateEnergyStarPropertyCommand());
		c.addCommand(new UpdateEnergyStarPropertyUseCommand());
		c.addCommand(new UpdateEnergyStarMeterCommand());
		return c;
	}

	public static FacilioChain addEnergyStarUtilityDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ESFillMeterDetails());
		c.addCommand(new ValidateEnergyStarUtilityData());
		c.addCommand(new EnergyStarPushDataCommand());
		c.addCommand(new AddEnergyStarMeterData());
		return c;
	}

	public static FacilioChain addPushESHistoricalDataJobChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateForESPushHisoricalForConnectedCommand());
		c.addCommand(new EnergyStarHistoricalPushDataAddJobCommand());
		return c;
	}

	public static FacilioChain addBulkPushESHistoricalDataJobChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new EnergyStarHistoricalBulkPushDataAddJobCommand());
		return c;
	}

	public static FacilioChain syncPropertyMetaAndUseDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(ReadOnlyChainFactory.getESfetchSetupData());
		c.addCommand(new EnergyStarSyncPropertyMetaCommand());
		return c;
	}

	public static FacilioChain addFetchESHistoricalDataJobChain() {
		FacilioChain c = getDefaultChain();
//		c.addCommand(new ValidateForESPushHisoricalForConnectedCommand());
		c.addCommand(new EnergyStarHistoricalFetchDataAddJobCommand());
		return c;
	}

	public static FacilioChain getESPushMeterDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new PrepareESMeterDataCommand());
		c.addCommand(new ESFillMeterDetails());
		c.addCommand(new EnergyStarPushDataCommand());
		c.addCommand(new AddEnergyStarMeterData());
		return c;
	}

	public static FacilioChain deleteEnergyStarPropertyChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetEnergyStarCustomerCommand());
		c.addCommand(new DeleteEnergyStarPropertyCommand());
		return c;
	}

	public static FacilioChain getEnergyStarFetchDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ESCalculateFetchTimeListCommand());
		c.addCommand(new EnergyStarFetchDataCommand());
		c.addCommand(new AddEnergyStarPropertyData());
		return c;
	}
	public static FacilioChain confirmESAccountShareChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetEnergyStarCustomerCommand());
		c.addCommand(new ConfirmESAccountShareCommand());
		return c;
	}

	public static FacilioChain confirmPendingSharesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetEnergyStarCustomerCommand());
		c.addCommand(new ConfirmESPropertyShareCommand());
		c.addCommand(new ConfirmESMeterShareCommand());
		return c;
	}

	public static FacilioChain fetchEnergyStarCustomerChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetEnergyStarCustomerCommand());
		return c;
	}

	public static FacilioChain getAddAssetToDepreciationChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddAssetToDepreciationCommand());
		return chain;
	}

	public static FacilioChain getRemoveAssetToDepreciationChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new RemoveAssetFromDepreciationCommand());
		return chain;
	}

	public static FacilioChain getRuleRollupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new RuleRollupCommand());
		return chain;
	}

	public static FacilioChain getAssociateQuotationTermsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AssociateQuotationTermsCommand());
		c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTE_ACTIVITY));
		return c;
	}
	public static FacilioChain getDisAssociateQuotationTermsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DisAssociateQuotationTermsCommand());
		c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTE_ACTIVITY));
		return c;
	}
	public static FacilioChain getSendQuotationMailChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new SendQuotationMailCommand());
		c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTE_ACTIVITY));
		return c;
	}
	public static  FacilioChain addMultipleAttachment () {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddMultipleAttachmentCommand());
		return c;
	}
	public static FacilioChain getAddOrUpdateAggregation() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateAggregationCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateDashboardFilterChain() {
		FacilioChain c=getDefaultChain();
		c.addCommand(new AddOrUpdateDashboardFilterCommand());
		c.addCommand(new AddOrUpdateDashboardUserFilterCommand());
		return c;
	}
	public static FacilioChain getSaveMailMessage() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new SaveMailMessageCommandV3());
		return chain;
	}

	public static FacilioChain addOrUpdateWeatherDataChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateWeatherDataCommand());
		return chain;
	}

	public static FacilioChain addOrUpdateDailyWeatherDataChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateDailyWeatherDataCommand());
		return chain;
	}

	public static FacilioChain updateUserStatusChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateUserCommand());
		c.addCommand(new UpdateUserStatusCommand());
		return c;
	}
	public static FacilioChain deleteUserChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateUserCommand());
		c.addCommand(new DeleteUserCommand());
		return c;
	}

	public static FacilioChain updateUserChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateUserCommand());
		c.addCommand(new UpdateUserCommand());
		c.addCommand(new AddorUpdateUserScopingCommand());
		c.addCommand(new AddOrUpdatePermissionSetsForPeopleCommand());
		return c;
	}

	public static FacilioChain runDependencyHistory() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new CreateDependencyHistoryCommand());
		return chain;
	}

	public static FacilioChain getAddNamedCriteriaChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddNamedCriteriaCommand());
		return chain;
	}

	public static FacilioChain deleteNamedCriteriaChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteNamedCriteriaCommand());
		return chain;
	}
	public static FacilioChain getUpdateWidgetFilterSettingsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateWidgetFilterSettingsCommand());
		return chain;
	}

	public static FacilioChain getAddOrUpdateAlarmImpactChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateAlarmImpactCommand());
		return chain;
	}

	public static FacilioChain deleteAlarmImpactChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new FacilioCommand() {
			@Override
			public boolean executeCommand(Context context) throws Exception {
				long id = (long) context.get(FacilioConstants.ContextNames.ID);
				if (id > 0) {
					AlarmImpactAPI.deleteAlarmImpact(id);
				}
				return false;
			}
		});
		return chain;
	}

	public static FacilioChain getAddInstantJobChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddInstantJobCommand());
		return chain;
	}

	public static FacilioChain addAgentWorkflowRuleChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(getAddModuleWorkflowRuleChain());
		//chain.addCommand(new UpdateAgentWorkflowCommand());
		return chain;
	}

	public static FacilioChain addScoreSubModuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddScoreSubModuleCommand());
		return chain;
	}

	public static FacilioChain addIndoorFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddIndoorFloorPlanCommand());
		c.addCommand(new AddIndoorFloorPlanObjectsCommand());

		return c;
	}

	public static FacilioChain getIndoorFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new getIndoorFloorPlanCommand());
		return c;
	}

	public static FacilioChain getAllIndoorFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new getAllIndoorFloorPlan());
		return c;
	}
	public static FacilioChain updateIndoorFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteIndoorFloorPlanObjectsCommand());
		c.addCommand(new updateIndoorFloorPlanCommand());
		c.addCommand(new AddIndoorFloorPlanObjectsCommand());
		return c;
	}

	public static FacilioChain deleteIndoorFloorPlanChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeleteIndoorFloorPlanObjectsCommand());
		c.addCommand(new deleteIndoorFloorPlanCommand());
		return c;
	}

	public static FacilioChain addOrUpdateTranslationChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateTranslationCommand());
		return chain;
	}


	public static FacilioChain addOrUpdateScatterGraph() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddOrUpdateScatterGraph());
		return chain;
	}

	public static FacilioChain deleteScatterGraph(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteScatterGraph());
		return chain;
	}

	public static FacilioChain getWeekendListChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetWeekendListCommand());
		return chain;
	}

	public static FacilioChain getWeekendChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetWeekendCommand());
		return chain;
	}

	public static FacilioChain addOrUpdateWeekendChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ValidateWeekendCommand());
		chain.addCommand(new AddorUpdateWeekendCommand());
		return chain;
	}

	public static FacilioChain deleteWeekendChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ValidateWeekendDeletion());
		chain.addCommand(new DeleteWeekendCommand());
		return chain;
	}

	public static FacilioChain getDeleteAttachmentsListChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteTemplateAttachmentCommand());
		return chain;
	}

	public static FacilioChain getAttachmentsListTranslationChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAttachmentsListCommand());
		return chain;
	}

	public static FacilioChain addOrUpdateScopingConfigChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddOrUpdateScopingConfigCommand());
		return c;
	}

	public static FacilioChain addOrUpdateScopingChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddOrUpdateScopingMetaCommand());
		return c;
	}

	public static FacilioChain getDeleteScopingChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteScopingCommand());
		return chain;
	}

	public static FacilioChain getCloneScopingChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new CloneScopingCommand());
		return chain;
	}

	public static FacilioChain addSurveyRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddSurveyRuleCommand ());
		return chain;
	}
	public static FacilioChain getAddMessageSourceChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddMessageSourceCommand());
		return chain;
	}

	public static FacilioChain updateSurveyRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateSurveyRuleCommand ());
		chain.addCommand(new DeleteOldSurveyRuleActionCommand());
		chain.addCommand(new AddSurveyRuleActionCommand());
		return chain;
	}
	public static FacilioChain deleteSurveyRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteSurveyRulesCommand());
		return chain;
	}

	public static FacilioChain updateSurveyMarkAsDeleteChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateSurveyMarkAsDeleteCommand());
		return chain;
	}

	public static FacilioChain addOrUpdatePivotReport(){
		FacilioChain chain = FacilioChain.getTransactionChain();
		chain.addCommand(ReadOnlyChainFactory.fetchPivotReportChain());
		chain.addCommand(new AddOrUpdateReportCommand());

		return chain;
	}

	public static FacilioChain agentMigrationChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AgentMigrationCommand());
		return chain;
	}

	public static FacilioChain getRunWorkflowChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new RunWorkflowCommand());
		return chain;
	}

	public static FacilioChain updateJobActiveStatusChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateJobActiveStatusCommand());
		chain.addCommand(new UpdateAgentConnectedStatusCommand());
		return chain;
	}

	public static FacilioChain addEmployeePortalChain(){
		FacilioChain chain = getDefaultChain();
//		chain.addCommand(new AddEmployeePortalScoping());
		chain.addCommand(new AddEmployeePortalDefaultViews());
		chain.addCommand(new AddEmployeePortalDefaultForms());
		return chain;
	}

	public static FacilioChain addPortalEmployeeChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForEmployee());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new AddPeopleTypeForEmployeeCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdateEmployeePortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());


		return c;
	}

	public static FacilioChain updatePortalEmployeeChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForEmployee());
		c.addCommand(new PeopleValidationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		c.addCommand(new UpdateEmployeePortalAccessCommand());
		c.addCommand(new UpdateScopingForPeopleCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));




		return c;
	}

	public static FacilioChain getPageWidgetChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetCustomPageWidgetCommand());

		return c;
	}
	public static FacilioChain getTestEmailChain(){
		FacilioChain chain=getDefaultChain();
		chain.addCommand(new sendTestMailCommand());
		return chain;
	}

	public static FacilioChain executeScheduleKpi(Integer scheduleType){
		FacilioChain c = getDefaultChain();
		c.addCommand(new ExecuteSchKpiOfACategoryCommand(scheduleType));
		return c;
	}
	public static FacilioChain fetchIntervalsAndCalculateKpiChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchIntervalsAndCalculateKpiCommand());
		return c;
	}

	public static FacilioChain addScopingChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddUserScopeForGlobalScopeCommand());
		return c;
	}

	public static FacilioChain getAddOrUpdateCurrencyChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new CurrencyObjectValidator());
		c.addCommand(new AddOrUpdateCurrencyCommand());
		return c;
	}

	public static FacilioChain getUpdateCurrencyStatusChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateCurrencyStatusCommand());
		return c;
	}

	public static FacilioChain getAllCurrenciesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAllCurrenciesCommand());
		c.addCommand(new GetCurrencySupplementsCommand());
		return c;
	}

	public static FacilioChain getCurrenciesCountChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetCurrenciesCountCommand());
		return c;
	}

	public static FacilioChain getUnconfiguredCurrenciesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetUnconfiguredCurrenciesCommand());
		return c;
	}

	public static FacilioChain getBaseCurrencyChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetBaseCurrencyCommand());
		return c;
	}
	public static FacilioChain getSpaceBookingActionChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchSpaceBookingWithIdCommand());
		c.addCommand(new SpaceBookingCancelCommand());
		c.addCommand(new ExtendSpaceBookingCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.STATE_RULE));
		return c;
	}

	public static FacilioChain getAddOrUpdateFormSharingRolesChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddFormSharingCommand());
		return c;
	}

	public static FacilioChain getFormSharingRolesChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetFormSharingRolesCommand());
		return c;
	}

	public static FacilioChain getHistoryUpdateChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddActivitiesCommandV3());
		return c;
	}

	public static FacilioChain getCreateCustomPageChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddCustomPageCommand());
		return c;
	}
	public static FacilioChain getAddPageTabsChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddPageTabCommand());
		return c;
	}
	public static FacilioChain getAddPageColumnsChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddPageColumnsCommand());
		return c;
	}
	public static FacilioChain getAddPageSectionChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddPageSectionCommand());
		return c;
	}
	public static FacilioChain getCreatePageSectionWidgetsChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddPageSectionWidgetCommand());
		return c;
	}
	public static FacilioChain getPatchCustomPageChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new PatchCustomPageCommand());
		return c;
	}
	public static FacilioChain getPatchPageTabsChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new PatchPageTabsCommand());
		return c;
	}
	public static FacilioChain getPatchPageColumnChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new PatchPageColumnCommand());
		return c;
	}
	public static FacilioChain getPatchPageSectionChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new PatchPageSectionCommand());
		return c;
	}
	public static FacilioChain getDeleteCustomPageChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidatePageDeletionCommand());
		c.addCommand(new DeletePageComponentsCommand());
		return c;
	}
	public static FacilioChain getDeletePageComponentChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new DeletePageComponentsCommand());
		return c;
	}
	public static FacilioChain getReorderPageChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ValidatePageReorderCommand());
		chain.addCommand(new ReorderPageComponentsCommand());
		return chain;
	}
	public static FacilioChain getReorderPageTabsChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ValidatePageTabsReorderCommand());
		chain.addCommand(new ReorderPageComponentsCommand());
		return chain;
	}
	public static FacilioChain getReorderPageSectionsChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ValidatePageSectionsReorderCommand());
		chain.addCommand(new ReorderPageComponentsCommand());
		return chain;
	}

	public static FacilioChain getChangeStatusForPageChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ChangePageStatusCommand());
		return chain;
	}
	public static FacilioChain getChangeStatusForTabChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ChangePageTabStatusCommand());
		return chain;
	}
	public static FacilioChain getChangeDefaultPageChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ChangeDefaultPageCommand());
		return chain;
	}
	public static FacilioChain getAddWidgetConfigCommand(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddNewWidgetsCommand());
		chain.addCommand(new AddWidgetToModuleCommand());
		chain.addCommand(new AddWidgetConfigurationCommand());
		return chain;
	}

	public static FacilioChain getCreateWidgetGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddWidgetGroupConfigCommand());
		chain.addCommand(new AddWidgetGroupSectionsCommand());
		chain.addCommand(new AddWidgetGroupWidgetsCommand());
		return chain;
	}
	public static FacilioChain getRearrangeWidgetsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdatePageWidgetPositionsCommand());
		return chain;
	}


	public static FacilioChain getAddSummaryWidgetChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddSummaryWidgetCommand());
		chain.addCommand(new AddOrUpdateSummaryWidgetGroupsCommand());
		chain.addCommand(new AddOrUpdateSummaryWidgetGroupFieldsCommand());
		return chain;
	}
	public static FacilioChain getUpdateSummaryWidgetChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateSummaryWidgetCommand());
		chain.addCommand(new AddOrUpdateSummaryWidgetGroupsCommand());
		chain.addCommand(new AddOrUpdateSummaryWidgetGroupFieldsCommand());
		return chain;
	}

	public static FacilioChain getAddSummaryWidgetChainInPage(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddSummaryWidgetCommand());
		chain.addCommand(new AddOrUpdateSummaryWidgetGroupsCommand());
		chain.addCommand(new AddOrUpdateSummaryWidgetGroupFieldsCommand());
		chain.addCommand(new AddPageSummaryWidgetCommand());
		return chain;
	}

	public static FacilioChain getUpdateSummaryWidgetChainInPage(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateSummaryWidgetCommand());
		chain.addCommand(new AddOrUpdateSummaryWidgetGroupsCommand());
		chain.addCommand(new AddOrUpdateSummaryWidgetGroupFieldsCommand());
		chain.addCommand(new AddPageSummaryWidgetCommand());
		return chain;
	}

	public static FacilioChain getAddBulkRelatedListCommand() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddBulkRelatedListWidgetCommand());
		return chain;
	}
	public static FacilioChain getUpdateBulkRelatedListCommand() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateBulkRelatedListWidgetCommand());
		return chain;
	}
	public static FacilioChain getKioskVendorCheckoutChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new KioskVendorCheckOutCommand());
		return c;
	}

	public static FacilioChain getGeoLocationFieldsChain(){
		FacilioChain c= getDefaultChain();
		c.addCommand(new GetGeoLocationFiledsCommand());
		return c;
	}

	public static FacilioChain addRuleToWOWorkFlowChain(){
		FacilioChain c= getDefaultChain();
		c.addCommand(new AddRuleWoDetailsCommand());
		return c;
	}
	public static FacilioChain updateRuleToWorkFlowChain(){
		FacilioChain c=getDefaultChain();
		c.addCommand(new UpdateRuleWoDetailsCommand());
		return c;
	}
	public static FacilioChain fetchRuleToWorkflowChain(){
		FacilioChain c=getDefaultChain();
		c.addCommand(new FetchRuleWoDetailsCommand());
		c.addCommand(new SetWorkflowDetailsCommand());
		return c;
	}
	public static FacilioChain addFaultToWorkOrder(){
		FacilioChain c=getDefaultChain();
		c.addCommand(new AddFaultToWorkOrderSupplementsCommand());
		c.addCommand(new WorkOrderCreationFromFaultCommand());
		return c;
	}
	public static FacilioChain closeWorkOrderFromFault(){
		FacilioChain c=getDefaultChain();
		c.addCommand(new CloseWorkOrderFromFaultsCommand());
		return c;
	}
	public static FacilioChain wfRuleStatusChangeFromRule(){
		FacilioChain c=getDefaultChain();
		c.addCommand(new FaultToWorkorderStatusChangeCommand());
		return c;
	}

}



