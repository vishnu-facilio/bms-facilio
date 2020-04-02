package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AddActivitiesCommand;
import com.facilio.agent.ConfigureAgentCommand;
import com.facilio.agent.ConfigureControllerCommand;
import com.facilio.agent.DeleteAgentCommand;
import com.facilio.agent.commands.*;
import com.facilio.agent.integration.AddIntegrationCommand;
import com.facilio.agent.integration.UpdateIntegrationCommand;
import com.facilio.agent.integration.wattsense.AgentIntegrationDeleteCommand;
import com.facilio.agentv2.UpdateAgentCommand;
import com.facilio.agentv2.commands.*;
import com.facilio.agentv2.controller.commands.AddDevicesCommand;
import com.facilio.agentv2.controller.commands.FieldDevicesToControllerCommand;
import com.facilio.agentv2.device.commands.DeleteFieldDevice;
import com.facilio.agentv2.device.commands.getFieldDevicesCommand;
import com.facilio.agentv2.iotmessage.AddAndSendIotMessageCommand;
import com.facilio.agentv2.point.AddPointCommand;
import com.facilio.agentv2.point.ConfigurePointCommand;
import com.facilio.agentv2.point.EditPointCommand;
import com.facilio.agentv2.sqlitebuilder.AgentSqliteMakerCommand;
import com.facilio.bmsconsole.actions.GetModuleFromReportContextCommand;
import com.facilio.bmsconsole.actions.PurchaseOrderCompleteCommand;
import com.facilio.bmsconsole.commands.data.PopulateImportProcessCommand;
import com.facilio.bmsconsole.commands.reservation.CreateExternalAttendeesCommand;
import com.facilio.bmsconsole.commands.reservation.CreateInternalAttendeesCommand;
import com.facilio.bmsconsole.commands.reservation.ValidateAndSetReservationPropCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.cb.command.*;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.commands.*;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand;
import com.facilio.events.commands.NewExecuteEventRulesCommand;
import com.facilio.events.constants.EventConstants;
import com.facilio.mv.command.*;
import com.facilio.workflows.command.*;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class TransactionChainFactory {

	private static FacilioChain getDefaultChain() {
		return FacilioChain.getTransactionChain();
    }

		public static FacilioChain getOrgSignupChain() {
			FacilioChain c = getDefaultChain();

			c.addCommand(new AddDefaultModulesCommand());
			c.addCommand(new AddDefaultUnitsCommand());
			c.addCommand(new AddDefaultGraphicsCommand());
			c.addCommand(new AddDefaultWoStateflowCommand());
			c.addCommand(new AddEventModuleCommand());
			c.addCommand(new AddOrgInfoCommand());
			c.addCommand(new CreateAppSuperAdminCommand());
			c.addCommand(new AddEmployeeTypePeopleForUserAdditionCommand());
			
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
			c.addCommand(new ExecuteNoteWorkflowCommand());
			c.addCommand(new AddNoteTicketActivityCommand());
//			c.setPostTransactionChain(getUpdateTicketNotesChain());
			c.addCommand(new AddActivitiesCommand());
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
		
		public static FacilioChain runThroughReadingRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new RunThroughReadingRulesCommand());
			return c;
		}
		
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
			c.addCommand(SetTableNamesCommand.getForTenants());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddTenantCommand());
			c.addCommand(new AddTenantUserCommand());
			c.addCommand(new AddTenantSpaceRelationCommand());
			c.addCommand(new ExecuteStateFlowCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			return c;
		}
		
		
		public static FacilioChain v2UpdateTenantChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateTenantCommand());
			c.addCommand(new AddTenantUserCommand());
			c.addCommand(new DeleteTenantSpaceRelationCommand());
			c.addCommand(new AddTenantSpaceRelationCommand());
			c.addCommand(new AddTenantUnitSpaceRelationCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			return c;
		}
		
		public static FacilioChain v2fetchTenantDetails() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTenants());
			c.addCommand(new AssociateTenantToContactCommand());
			c.addCommand(new GetTenantDetailCommand());
			c.addCommand(new SetTenantSpaceAndContactsCommand());
			return c;
		}
		
		
		
		public static FacilioChain v2DeleteTenantChain () {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteTenantCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
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
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_CUSTOM_CHANGE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
//			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ExecuteSLAWorkFlowsCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));

			if (sendNotification) {
				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 218L) {
					c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION));
				} else {
					c.addCommand(new ForkChainToInstantJobCommand()
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
			c.addCommand(new ValidateWorkOrderFieldsCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new UpdateEventListForStateFlowCommand());
			c.addCommand(new AddWorkOrderCommand());
			c.addCommand(new AddWorkorderHazardsFromSafetyPlanCommand());
			c.addCommand(new AddAttachmentCommand());
			c.addCommand(new AttachmentContextCommand());
			c.addCommand(new AddAttachmentRelationshipCommand());
			c.addCommand(new AddTicketActivityCommand());
			c.addCommand(getAddTasksChain());
			c.addCommand(new AddPrerequisiteApproversCommand());
			c.addCommand(new FacilioCommand() {
				@Override
				public boolean executeCommand(Context context) throws Exception {
					context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(context.get(FacilioConstants.ContextNames.WORK_ORDER)));
					return false;
				}
			});
			c.addCommand(getWorkOrderWorkflowsChain(true));
			c.addCommand(new AddOrUpdateSLABreachJobCommand(true));
			c.addCommand(new AddActivitiesCommand());
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
		
		
		public static FacilioChain getUpdateWorkOrderChain() {
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
			c.addCommand(new VerifyQrCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new SendNotificationCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_STATE_FLOW));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ExecuteSLACommitmentWorkflowsCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION))
				.addCommand(new ClearAlarmOnWOCloseCommand())
				.addCommand(new ExecuteTaskFailureActionCommand())
			);
			c.addCommand(new AddOrUpdateSLABreachJobCommand(false));
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
		public static FacilioChain deleteWattsenseChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new AgentIntegrationDeleteCommand());
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
			c.addCommand(new LoadViewCommand());
			c.addCommand(new GenerateCriteriaFromFilterCommand());
			c.addCommand(new AddCVCommand());
			c.addCommand(new CustomizeViewColumnCommand());
			return c;
		}
		public static FacilioChain deleteViewChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteViewCommand());
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
		
		public static FacilioChain addAlarmRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddAlarmRuleCommand());
			c.addCommand(new AddActionForAlarmRuleCommand());
			c.addCommand(new AddOrUpdateJobEntryForScheduledReadingRuleCommand());
			c.addCommand(new AddReadingAlarmRuleCommand());
			return c;
		}
		
		public static FacilioChain addReadingAlarmRuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddReadingAlarmRuleCommand());
			return c;
		}
		
		public static FacilioChain addControlGroupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddControlGroupCommand());
			c.addCommand(new AddControlGroupSpaceCommand());
			c.addCommand(new AddControlGroupInclExclCommand());
			return c;
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
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ConstructAddAssetActivityCommand());
			c.addCommand(new AddRotatingItemToolCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(FacilioChainFactory.getCategoryReadingsChain());
			c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		
		public static FacilioChain getUpdateAssetChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new AddCategoryOnAssetUpdateCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new UpdateEventListForStateFlowCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new ConstructEditAssetActivityCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
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
			c.addCommand(new InsertReadingDataMetaForImport());
			c.addCommand(TransactionChainFactory.getbimImportUpdateChain());
			c.addCommand(new SendEmailCommand());
			return c;
		}
		
		public static FacilioChain getBulkAssertImportChain() {
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
			return c;
		}
		public static FacilioChain updateBusinessHourInResourceChain () {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new UpdateBusinessHourInResourceCommand());
			c.addCommand(new AddActivitiesCommand());
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
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ALARM_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION))
			);
			c.addCommand(new AddAlarmFollowersCommand());
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
		
		public static FacilioChain getAddModuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new CreateCustomModuleCommand());
			c.addCommand(new AddSystemFieldsCommand());
			c.addCommand(commonAddModuleChain());
			c.addCommand(new CreateCustomModuleDefaultSubModuleCommand());
			c.addCommand(new AddDefaultFormForCustomModuleCommand());
			c.addCommand(new AddDefaultStateFlowCommand());
			return c;
		}

		public static FacilioChain getUpdateModuleChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateModuleCommand());
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
			c.addCommand(new SetColumnNameForNewCFsCommand());
			c.addCommand(new AddFieldsCommand());

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
			FacilioChain c = getDefaultChain();
			c.addCommand(getAddReadingsChain());
			c.addCommand(new AddResourceReadingRelCommand());
			c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
			return c;
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

		public static FacilioChain getAddFieldsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new SetColumnNameForNewCFsCommand());
			c.addCommand(new AddFieldsCommand());
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
			return c;
		}
		
		public static FacilioChain updateFormulaChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new UpdateFormulaCommand());
			c.addCommand(new AddValidationRulesCommand());
			//c.addCommand(new AddKPIViolationRuleCommand());
			return c;
		}
		
		public static FacilioChain onlyAddOrUpdateReadingsChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new GetReadingDataMetaCommand());
			c.addCommand(new ReadingUnitAndInputConversionCommand());
			c.addCommand(new EnergyDataDeltaCalculationCommand());
			c.addCommand(new CalculateDeltaCommand());
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
			c.addCommand(new ValidateAndCreateValuesForInputTaskCommand());
			c.addCommand(new ValidateReadingInputForTask());
			c.addCommand(new ReadingUnitConversionToRdmOrSiUnit());
			c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
			c.addCommand(new ReadingUnitConversionToSiUnit());
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new UpdateTaskCommand());
			c.addCommand(new UpdateClosedTasksCounterCommand());
			c.addCommand(new AddTaskTicketActivityCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			c.addCommand(new AddActivitiesCommand());
			c.addCommand(new UpdateRdmWithLatestInputUnit());
			c.addCommand(new SiUnitConversionToEnteredReadingUnit());
//			c.addCommand(getAddOrUpdateReadingValuesChain());
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
	public static FacilioChain getTimeSeriesProcessChainV2() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new ProcessDataCommandV2());
		chain.addCommand(new ModeledDataCommand());
		chain.addCommand(new UnModeledDataCommand());
		chain.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());

		//chain.addCommand(new ProcessTimeSeriesData());
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

		public static FacilioChain getExportModuleReportFileChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
			c.addCommand(new GetModuleFromReportContextCommand());
			c.addCommand(new GetExportModuleReportFileCommand());
			return c;
		}

		public static FacilioChain sendModuleReportMailChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(getExportModuleReportFileChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}

		public static FacilioChain getExportModuleAnalyticsFileChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ConstructReportData());
			c.addCommand(getExportModuleReportFileChain());
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
			return c;
		}
		
		public static FacilioChain getUpdateStoreRoomChain(){
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForStoreRoom());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new DeleteSitesForStoreRoomCommad());
			c.addCommand(new AddSitesForStoreRoomCommand());
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
			return c;
		}
		
		public static FacilioChain getUpdateItemTypesChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypes());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static FacilioChain getAddOrUpdateConnectedAppChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForConnectedApps());
			c.addCommand(new AddOrUpdateConnectedAppCommand());
			return c;
		}

		public static FacilioChain getDeleteConnectedAppChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForConnectedApps());
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
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
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
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));
			c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
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
			c.addCommand(new AddItemCommand());
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
			c.addCommand(new GetAddPurchasedItemCommand());
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
			c.addCommand(new AddOrUpdateItemQuantityCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE));

			c.addCommand(getUpdateItemTypeQuantityRollupChain());
			return c;
		}
		
		public static FacilioChain getUpdateItemTypeQuantityRollupChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ItemTypeQuantityRollupCommand());
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
			c.addCommand(new AddOrUpdateWorkorderItemsCommand());
//			c.addCommand(new ExecuteAllWorkflowsCommand());
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
			c.addCommand(new RequestedLineItemQuantityRollUpCommand());
			c.addCommand(new GenericDeleteModuleDataCommand());
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
			c.addCommand(new AddBulkToolStockTransactionsCommand());
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
			c.addCommand(new AddOrUpdateWorkorderToolsCommand());
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
			c.addCommand(new AddOrUpdateWorkorderLabourCommand());
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
			c.addCommand(new RequestedLineItemQuantityRollUpCommand());
			c.addCommand(new GenericDeleteModuleDataCommand());
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
			c.addCommand(new SetItemAndToolTypeForStoreRoomCommand());
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
			chain.addCommand(new AddOrUpdatePurchaseOrderCommand());
			chain.addCommand(new RollUpReceivableDeliveryTimeCommand());
			chain.addCommand(new AssociateDefaultTermsToPoCommand());
			chain.addCommand(getPurchaseOrderTotalCostChain()); //update purchase order total cost
			chain.addCommand(new AddPurchaseRequestOrderRelation());
			chain.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE));
			chain.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION)));


			return chain;
		}

		public static FacilioChain getPurchaseOrderDeleteChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseOrder());
			c.addCommand(new DeletePurchaseOrderCommand());
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
			c.addCommand(new GenericGetModuleDataDetailCommand());
			c.addCommand(new GetAvailableStateCommand());
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
			chain.addCommand(new AddOrUpdateInventoryRequestCommand());
			return chain;
		}

		public static FacilioChain getIssueInventoryRequestChain() {
			FacilioChain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
			chain.addCommand(new AddOrUpdateInventoryRequestCommand());
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
			c.addCommand(new UseInventoryRequestLineItemsCommand());
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
		public static FacilioChain getDeleteWorkflowUserFunctionChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteWorkflowCommand());
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

		public static FacilioChain getExecuteCardWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ExecuteCardWorkflowCommand());
			c.addCommand(new ApplyConditionalFormattingForCard());
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

		public static FacilioChain getAddScheduledWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(getAddWorkflowChain());
			c.addCommand(new AddScheduledWorkflowCommand());
			return c;
		}
		public static FacilioChain getUpdateScheduledWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(getUpdateWorkflowChain());
			c.addCommand(new updateScheduledWorkflowCommand());
			return c;
		}
		public static FacilioChain getDeleteScheduledWorkflowChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new DeleteWorkflowCommand());
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
				c.addCommand(new ForkChainToInstantJobCommand()
						.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ALARM_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION))
				);		
			}	
			c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.ALARM_ACTIVITY));
			return c;
		}

	public static FacilioChain getV2UpdateAlarmChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateAlarmOccurrenceCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.REPORT_DOWNTIME_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.READING_ALARM_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.MODULE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.ALARM_WORKFLOW_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.PM_ALARM_RULE));
		c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ALARM_NOTIFICATION_RULE, RuleType.MODULE_RULE_NOTIFICATION))
		);
		c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.ALARM_ACTIVITY));
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
			c.addCommand(new AddOrUpdateWorkorderServiceCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			return c;
		}

		public static FacilioChain getDeleteWorkorderServiceChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderService());
			c.addCommand(new GenericDeleteModuleDataCommand());
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

		public static FacilioChain getExecuteFormActionRules() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateAndFillFromRuleParamsCommand());
			c.addCommand(new ExecuteFormActionRulesCommand());
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
			FacilioChain c = getDefaultChain();
			c.addCommand(new FillRDMForControlActionCommand());
			c.addCommand(new FillDefaultValuesForControlActionCommand());
			c.addCommand(new AddControlActionCommand());
			c.addCommand(new PublishIOTMessageControlActionCommand());
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
			c.addCommand(new HistoricalEventRunForReadingRuleCommand());
			return c;
		}

		public static FacilioChain getExecuteHistoricalRuleAlarmProcessing() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new HistoricalAlarmProcessingCommand());
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

	public static FacilioChain getAddControllerChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GenericAddModuleDataCommand());
		//chain.addCommand(new AddControllerCommand());
		return chain;
	}

	public static FacilioChain getUpdateControllerChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GenericUpdateModuleDataCommand());
		return chain;
	}

	public static FacilioChain deleteControllerChain(){
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteResourceCommand());
		return chain;
	}

	public static FacilioChain getControllerChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}

	public static FacilioChain getAddDevicesChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddDevicesCommand());
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
	public static FacilioChain getDisAssociateTermsToPOChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new DisAssociateTermsFromPoCommand());
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
			c.addCommand(getUpdateAssetChain());
			return c;
	    	
	    }
	    
	    public static FacilioChain getDisconnectDeviceChain()
	    {
	    	FacilioChain c=getDefaultChain();
	    	c.addCommand(new DisconnectDeviceCommand());
	    	c.addCommand(getUpdateAssetChain());
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
			c.addCommand(new AddAttachmentCommand());
			c.addCommand(new AttachmentContextCommand());
			c.addCommand(new AddAttachmentRelationshipCommand());
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

	public static FacilioChain getDeleteFieldDeviceChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new DeleteFieldDevice());
		return chain;
	}

	/**
	 * makes controller entry from FieldDeviceIds
	 * 	agentId not mandatory
	 * 	[deviceIds] to select devices or select all devices.
	 * @return
	 */
	public static FacilioChain getConfigurePointAndProcessControllerV2Chain() {
		FacilioChain chain = getDefaultChain();
		//chain.addCommand(new EditPointCommand());
		chain.addCommand(new getFieldDevicesCommand()); // aI not mandatory, ids - not mandatory(gets all devices)
		chain.addCommand(new FieldDevicesToControllerCommand());
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
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		
		return c;
	}
	
	public static FacilioChain updateInsurancesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInsurance());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));
		
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
		c.addCommand(new ExecuteStateFlowCommand());
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
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE_NOTIFICATION));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
		c.addCommand(new RollUpWorkOrderFieldOnWorkPermitApprovalCommand());

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



	public static FacilioChain addOrUpdateVisitorKioskConfigChain()
	{
		FacilioChain chain =getDefaultChain();
		chain.addCommand(new AddOrUpdateVisitorKioskConfigCommand());
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


	public static FacilioChain getAgentUpdateChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new UpdateAgentCommand());
		return  chain;
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
		chain.addCommand(new unconfigurePointsCommand());
		return chain;
	}

	public static FacilioChain subscribeUnsbscribechain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new SubscribeUnsbscribeCommand());
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
		chain.addCommand(new AddOrUpdateTabGroupCommand());
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
		return chain;
	}

	public static FacilioChain getAddNewPermissionChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddNewPermissionCommand());
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
	public static FacilioChain addServiceRequestChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForServiceRequests());
		c.addCommand(new AddRequesterForServiceRequestCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new AddAttachmentCommand());
		c.addCommand(new AttachmentContextCommand());
		c.addCommand(new AddAttachmentRelationshipCommand());
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		return c;
	}

	public static FacilioChain getAddRequesterChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddRequesterCommand());
		return c;
	}

	public static FacilioChain updateServiceRequestChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForServiceRequests());
		c.addCommand(new AddRequesterCommand());
		c.addCommand(new GenericUpdateModuleDataCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		return c;
	}

	public static FacilioChain getagentV2DataSqlite() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AgentSqliteMakerCommand());
		return chain;
	}


	public static FacilioChain getAddRtuChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new AddRtuNetworkCommand());
		return chain;
	}

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
		c.addCommand(new HandleInvalidQueryForSessionMessage());
		c.addCommand(new ExecuteActionAndSetResponseForSessionCommand());
		c.addCommand(new FetchSugestionForChatBotIntent());
		return c;
	}
	
	public static FacilioChain HandleChatBotSessionConversationChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetOrAddCurrentActiveModel());
		c.addCommand(new PrepareChatBotForMlAPICommand());
		c.addCommand(new SendToMlApiForConversationCommand());
		c.addCommand(new HandleTerminateSessionCommand());
		c.addCommand(new HandleInvalidQueryMessageForConversations());
		c.addCommand(new HandleEditParamFromSuggestionForConversations());
		c.addCommand(new HandleAddParamFromSuggestionForConversations());
		c.addCommand(new ExecuteActionAndSetResponseForConversationCommand());
		c.addCommand(new FetchSugestionForChatBotIntent());
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

		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
		
		return c;
	}

	public static FacilioChain updateDocumentsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateDocumentsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new UpdateStateForModuleDataCommand());
		c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
		c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
		

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

	public static FacilioChain getReorderWorkflowRuleChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(ReadOnlyChainFactory.fetchWorkflowRules());
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
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		
		return c;
	}
	
	public static FacilioChain addTenantContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTenantContact());
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new CheckForMandatoryTenantIdCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		
		return c;
	}
	
	public static FacilioChain addVendorContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendorContact());
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new CheckForMandatoryVendorIdCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		
		return c;
	}
	
	public static FacilioChain addEmployeeChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForEmployee());
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new AddPeopleTypeForEmployeeCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
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
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		return c;
	}
	
	public static FacilioChain updateVendorContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendorContact());
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		return c;
	}
	
	public static FacilioChain updateEmployeeChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForEmployee());
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		
		return c;
	}
	
	public static FacilioChain updatePeopleChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPeople());
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		
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
		c.addCommand(new OperationAlarmCommand());
		// c.addCommand(new ExecuteOpertionalAlarmCommand);
		return c;
	}	
	public static FacilioChain addClientContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForClientContact());
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new CheckForMandatoryClientIdCommand());
		c.addCommand(new GenericAddModuleDataListCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
		
		return c;
	}
	
	public static FacilioChain updateClientContactChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForClientContact());
		c.addCommand(new CheckForPeopleDuplicationCommand());
		c.addCommand(new GenericUpdateListModuleDataCommand());
		c.addCommand(new UpdatePeoplePrimaryContactCommand());
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
	public static FacilioChain getHistoricalOperationAlarm()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new HistoricalOperationAlarmCommand());
		c.addCommand(new HistoricalOperationAlarmOccurencesDeletionCommand());

		return c;
	}
	public static FacilioChain getExecuteOperatonalAlarm()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new OperationAlarm());
		return c;
	}
	public static FacilioChain getHistoricalOperationAlarmOccurencesDeletion()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new HistoricalOperationAlarmOccurencesDeletionCommand());
		return c;
	}
}



