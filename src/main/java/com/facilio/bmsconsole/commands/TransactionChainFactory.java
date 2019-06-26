package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AddActivitiesCommand;
import com.facilio.agent.ConfigureAgentCommand;
import com.facilio.agent.ConfigureControllerCommand;
import com.facilio.agent.DeleteAgentCommand;
import com.facilio.agent.commands.*;
import com.facilio.bmsconsole.actions.PurchaseOrderCompleteCommand;
import com.facilio.bmsconsole.commands.data.PopulateImportProcessCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.mv.command.AddMVAjustmentCommand;
import com.facilio.mv.command.AddMVAjustmentVsBaselineCommand;
import com.facilio.mv.command.AddMVBaselineCommand;
import com.facilio.mv.command.AddMVProjectCommand;
import com.facilio.mv.command.ConstructBaselineFormulaWithAjustmentCommand;
import com.facilio.mv.command.DeleteMVAjustmentVsBaselineCommand;
import com.facilio.mv.command.DeleteMVProjectCommand;
import com.facilio.mv.command.UpdateMVAdjustmentCommand;
import com.facilio.mv.command.UpdateMVBaselineCommand;
import com.facilio.mv.command.UpdateMVPojectCommand;
import com.facilio.workflows.command.AddNameSpaceCommand;
import com.facilio.workflows.command.AddScheduledWorkflowCommand;
import com.facilio.workflows.command.AddUserFunctionCommand;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand;
import com.facilio.workflows.command.AddWorkflowCommand;
import com.facilio.workflows.command.DeleteNameSpaceCommand;
import com.facilio.workflows.command.DeleteScheduledWorkflowCommand;
import com.facilio.workflows.command.DeleteWorkflowCommand;
import com.facilio.workflows.command.ExecuteWorkflowCommand;
import com.facilio.workflows.command.GetDefaultWorkflowContext;
import com.facilio.workflows.command.UpdateNameSpaceCommand;
import com.facilio.workflows.command.UpdateUserFunctionCommand;
import com.facilio.workflows.command.UpdateWorkflowCommand;
import com.facilio.workflows.command.updateScheduledWorkflowCommand;

public class TransactionChainFactory {

	private static Chain getDefaultChain() {
		return FacilioChain.getTransactionChain();
    }

//	public static Chain getOrgSignupChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new CreateOrgCommand());
//		c.addCommand(new AddDefaultModulesCommand());
//		c.addCommand(new AddDefaultReportCommand());
//		c.addCommand(new AddDefaultUnitsCommand());
//		c.addCommand(new AddEventModuleCommand());
//		c.addCommand(new AddOrgInfoCommand());
//		c.addCommand(new CreateSuperAdminCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getAddNotesChain() {
//		FacilioChain c = (FacilioChain) getDefaultChain();
//		c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new AddNotesCommand());
//		c.addCommand(new ExecuteNoteWorkflowCommand());
//		c.addCommand(new AddNoteTicketActivityCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		c.setPostTransactionChain(getUpdateTicketNotesChain());
//		return c;
//	}
//
//	public static Chain getUpdateTicketNotesChain() {
//		Chain c1 = getDefaultChain();
//		c1.addCommand(new UpdateNotesCountCommand());
//		CommonCommandUtil.addCleanUpCommand(c1);
//		return c1;
//	}
//
//	public static Chain getUpdateTaskCountChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new UpdateTaskCountCommand());
//		return c;
//	}
//
//	public static Chain getUpdateAttachmentCountChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new UpdateAttachmentCountUpdateCommand());
//		return c;
//	}
//
//	public static Chain historicalFormulaCalculationChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new HistoricalFormulaCalculationCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain runThroughReadingRuleChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new RunThroughReadingRulesCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getAddWorkOrderChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new GetFormMetaCommand());
//		c.addCommand(new ValidateFormCommand());
//		c.addCommand(new AddRequesterCommand());
//		c.addCommand(SetTableNamesCommand.getForWorkOrder());
//		c.addCommand(new ValidateWorkOrderFieldsCommand());
//		c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new AddWorkOrderCommand());
//		c.addCommand(new AddAttachmentCommand());
//		c.addCommand(new AttachmentContextCommand());
//		c.addCommand(new AddAttachmentRelationshipCommand());
//		c.addCommand(new AddTicketActivityCommand());
//		c.addCommand(getAddTasksChain());
//		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
//		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
//		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE,
//				RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
//		c.addCommand(new ForkChainToInstantJobCommand()
//				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE,
//						RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE)));
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getAddTasksChain() {
//		FacilioChain c = (FacilioChain) getDefaultChain();
//		c.addCommand(SetTableNamesCommand.getForTask());
//		c.addCommand(new ValidateTasksCommand());
//		c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new AddTaskSectionsCommand());
//		c.addCommand(new AddTasksCommand());
//		c.addCommand(new AddTaskOptionsCommand());
//		c.addCommand(new UpdateReadingDataMetaCommand());
//		// c.addCommand(new AddTaskTicketActivityCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
//		return c;
//	}
//
//	public static Chain getUpdateWorkOrderChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(SetTableNamesCommand.getForWorkOrder());
//		c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new FetchOldWorkOrdersCommand());
//		c.addCommand(new VerifyApprovalCommand());
//		c.addCommand(new UpdateWorkOrderCommand());
//		c.addCommand(new SendNotificationCommand());
//		c.addCommand(new AddTicketActivityCommand());
//		// c.addCommand(getAddOrUpdateReadingValuesChain());
//		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
//		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
//		c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE,
//				RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
//		c.addCommand(new ForkChainToInstantJobCommand()
//				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE,
//						RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE))
//				.addCommand(new ClearAlarmOnWOCloseCommand()));
//		c.addCommand(new ConstructTicketNotesCommand());
//		c.addCommand(getAddNotesChain());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain addOrUpdateReadingReportChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new CreateReadingAnalyticsReportCommand());
//		c.addCommand(new AddOrUpdateReportCommand());
//		return c;
//	}
//
//	public static Chain addOrUpdateReportChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new AddOrUpdateReportCommand());
//		return c;
//	}
//
//	public static Chain addWorkOrderReportChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new CreateWorkOrderAnalyticsReportCommand());
//		c.addCommand(new AddOrUpdateReportCommand());
//		return c;
//	}
//
//	public static Chain getAddPhotoChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new UploadPhotosCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getEditControllerChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new FetchControllerSettingCommand());
//		c.addCommand(new EditControllerSettingsCommand());
//		c.addCommand(new PublishControllerPropertyToIoTCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getDeleteControllerChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new DeleteControllerCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getExportAnalyticsFileChain() {
//		Chain c = new ChainBase();
//		c.addCommand(ReadOnlyChainFactory.fetchReadingReportChain());
//		c.addCommand(new GetExportReportDataCommand());
//		return c;
//	}
//
//	public static Chain getExportReportFileChain() {
//		Chain c = new ChainBase();
//		c.addCommand(ReadOnlyChainFactory.fetchReportDataChain());
//		c.addCommand(new GetExportReportDataCommand());
//		return c;
//	}
//
//	public static Chain sendAnalyticsMailChain() {
//		Chain c = new ChainBase();
//		c.addCommand(getExportAnalyticsFileChain());
//		c.addCommand(new SendReadingReportMailCommand());
//		return c;
//	}
//
//	public static Chain sendReportMailChain() {
//		Chain c = new ChainBase();
//		c.addCommand(getExportReportFileChain());
//		c.addCommand(new SendReadingReportMailCommand());
//		return c;
//	}
//
//	public static Chain getExportNewAnalyticsFileChain() {
//		Chain c = new ChainBase();
//		c.addCommand(ReadOnlyChainFactory.newFetchReadingReportChain());
//		c.addCommand(new GetExportReportFileCommand());
//		return c;
//	}
//
//	public static Chain getExportNewReportFileChain() {
//		Chain c = new ChainBase();
//		c.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
//		c.addCommand(new GetExportReportFileCommand());
//		return c;
//	}
//
//	public static Chain sendNewAnalyticsMailChain() {
//		Chain c = new ChainBase();
//		c.addCommand(getExportNewAnalyticsFileChain());
//		c.addCommand(new SendReadingReportMailCommand());
//		return c;
//	}
//
//	public static Chain sendNewReportMailChain() {
//		Chain c = new ChainBase();
//		c.addCommand(getExportNewReportFileChain());
//		c.addCommand(new SendReadingReportMailCommand());
//		return c;
//	}
//
//	public static Chain scheduleReportChain() {
//		Chain c = new ChainBase();
//		c.addCommand(new AddTemplateCommand());
//		c.addCommand(new ScheduleV2ReportCommand());
//		return c;
//	}
//
//	public static Chain updateScheduledReportsChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new AddTemplateCommand());
//		c.addCommand(new DeleteScheduledReportsCommand(true));
//		c.addCommand(new ScheduleV2ReportCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain deleteScheduledReportsChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new DeleteScheduledReportsCommand(true));
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain addWorkflowRuleChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new AddWorkflowRuleCommand());
//		c.addCommand(new AddActionsForWorkflowRule());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain addApprovalRuleChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new ConstructApprovalRuleCommand());
//		c.addCommand(addWorkflowRuleChain());
//		c.addCommand(new AddApproverActionRelCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getAddScheduledActionChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new AddActionsForWorkflowRule());
//		c.addCommand(new AddScheduledActionCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain editViewChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new LoadViewCommand());
//		c.addCommand(new GenerateCriteriaFromFilterCommand());
//		c.addCommand(new AddCVCommand());
//		c.addCommand(new CustomizeViewColumnCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain deleteViewChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new DeleteViewCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain updateWorkflowRuleChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new UpdateWorkflowRuleCommand());
//		c.addCommand(new DeleterOldRuleActionsCommand());
//		c.addCommand(new AddActionsForWorkflowRule());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain addAlarmRuleChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new AddAlarmRuleCommand());
//		c.addCommand(new AddActionForAlarmRuleCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain updateAlarmRuleChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new UpdateAlarmRuleCommand());
//		c.addCommand(new DeleterOldRuleActionsCommand());
//		c.addCommand(new AddActionForAlarmRuleCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain updateVersionedWorkflowRuleChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new UpdateWorkflowRuleCommand());
//		c.addCommand(addWorkflowRuleChain());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain updateApprovalRuleChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new ConstructApprovalRuleCommand());
//		c.addCommand(updateVersionedWorkflowRuleChain());
//		c.addCommand(new AddApproverActionRelCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getAddAssetChain() {
//		Chain c = getDefaultChain();
//		// c.addCommand(SetTableNamesCommand.getForAsset());
//		c.addCommand(new SetModuleForSpecialAssetsCommand());
//		// c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new LoadAssetFields());
//		c.addCommand(new GenericAddModuleDataCommand());
//		c.addCommand(new ExecuteAllWorkflowsCommand());
//		c.addCommand(FacilioChainFactory.getCategoryReadingsChain());
//		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getUpdateAssetChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new AddCategoryOnAssetUpdateCommand());
//		c.addCommand(new SetModuleForSpecialAssetsCommand());
//		// c.addCommand(SetTableNamesCommand.getForAsset());
//		c.addCommand(new LoadAssetFields());
//		// c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new GenericUpdateModuleDataCommand());
//		c.addCommand(new ExecuteAllWorkflowsCommand());
//		c.addCommand(FacilioChainFactory.getCategoryReadingsChain());
//		c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getImportChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new ProcessImportCommand());
//		c.addCommand(new PopulateImportProcessCommand());
//		c.addCommand(new UpdateBaseAndResourceCommand());
//		c.addCommand(new InsertReadingDataMetaForImport());
//		c.addCommand(new SendEmailCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getBulkAssertImportChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new ProcessImportCommand());
//		c.addCommand(new SeperateToCategoriesCommand());
//		// c.addCommand(new FilterForInsertSkipCommand());
//		c.addCommand(new SetModuleForSpecialAssetsCommand());
//		c.addCommand(new BulkPushAssetCommands());
//		c.addCommand(new UpdateBaseAndResourceCommand());
//		c.addCommand(new InsertReadingDataMetaForImport());
//		c.addCommand(new SendEmailCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain scheduledRuleExecutionChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new FetchScheduledRuleMatchingRecordsCommand());
//		c.addCommand(new ExecuteSingleWorkflowRuleCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain controllerActivityAndWatcherChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new UpdateCheckPointAndAddControllerActivityCommand());
//		c.addCommand(new CheckAndStartWatcherCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getAddAlarmFromEventChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new ProcessAlarmCommand());
//		c.addCommand(getAddAlarmChain());
//		return c;
//	}
//
//	public static Chain getAddAlarmChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(SetTableNamesCommand.getForAlarm());
//		c.addCommand(new AddAlarmCommand());
//		c.addCommand(new ExecuteAllWorkflowsCommand());
//		c.addCommand(new AddAlarmFollowersCommand());
//		c.addCommand(new ConstructTicketNotesCommand());
//		c.addCommand(getAddNotesChain());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain updateAlarmFromJsonChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new ProcessAlarmCommand());
//		c.addCommand(getUpdateAlarmChain());
//		return c;
//	}
//
//	public static Chain getUpdateAlarmChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(SetTableNamesCommand.getForAlarm());
//		c.addCommand(new UpdateAlarmCommand());
//		c.addCommand(new ExecuteAllWorkflowsCommand());
//		c.addCommand(new ForkChainToInstantJobCommand().addCommand(new AddClearCommentInWoOnAlarmClearCommand()));
//		c.addCommand(new ConstructTicketNotesCommand());
//		c.addCommand(getAddNotesChain());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getAddWoFromAlarmChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new AddWOFromAlarmCommand());
//		c.addCommand(getAddWorkOrderChain());
//		c.addCommand(new UpdateWoIdInAlarmCommand());
//		return c;
//	}
//
//	public static Chain getAddModuleChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new CreateCustomModuleCommand());
//		commonAddModuleChain(c);
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getAddReadingsChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new CreateReadingModulesCommand());
//		commonAddModuleChain(c);
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	private static void commonAddModuleChain(Chain c) {
//		c.addCommand(new AddModulesCommand());
//		c.addCommand(new SetColumnNameForNewCFsCommand());
//		c.addCommand(new AddFieldsCommand());
//	}
//
//	public static Chain getAddCategoryReadingChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(getAddReadingsChain());
//		c.addCommand(new AddCategoryReadingRelCommand());
//		c.addCommand(new GetCategoryResourcesCommand());
//		c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
//		// c.addCommand(new SetValidationRulesContextCommand());
//		c.addCommand(new AddValidationRulesCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain addResourceReadingChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(getAddReadingsChain());
//		c.addCommand(new AddResourceReadingRelCommand());
//		c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getAddFieldsChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new SetColumnNameForNewCFsCommand());
//		c.addCommand(new AddFieldsCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain addFormulaFieldChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new CreateFormulaFieldDependenciesCommand());
//		c.addCommand(getAddReadingsChain());
//		c.addCommand(new AddResourceReadingRelCommand());
//		c.addCommand(new AddCategoryReadingRelCommand());
//		c.addCommand(new GetCategoryResourcesCommand());
//		c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
//		c.addCommand(new AddFormulaFieldCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain addDerivationFormulaChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(addFormulaFieldChain());
//		c.addCommand(new UpdateDerivationCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain onlyAddOrUpdateReadingsChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new GetReadingDataMetaCommand());
//		c.addCommand(new ReadingUnitAndInputConversionCommand());
//		c.addCommand(new DeltaCalculationCommand());
//		c.addCommand(new CalculateDeltaCommand());
//		c.addCommand(new CalculatePreFormulaCommand());
//		c.addCommand(new ExecuteValidationRule());
//		c.addCommand(new AddOrUpdateReadingValuesCommand());
//		c.addCommand(new AddMarkedReadingValuesCommand());
//		c.addCommand(new SpaceBudIntegrationCommand()); // For RMZ-SpaceBud
//														// Integration
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getUpdateTaskChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new ValidateAndCreateValuesForInputTaskCommand());
//		c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
//		c.addCommand(SetTableNamesCommand.getForTask());
//		c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new UpdateTaskCommand());
//		c.addCommand(new UpdateClosedTasksCounterCommand());
//		c.addCommand(new AddTaskTicketActivityCommand());
//		c.addCommand(new ExecuteAllWorkflowsCommand());
//		// c.addCommand(getAddOrUpdateReadingValuesChain());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getProcessDataChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new ProcessDataCommand());
//		c.addCommand(new ModeledDataCommand());
//		c.addCommand(new UnModeledDataCommand());
//		c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getProcessHistoricalDataChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new HistoricalReadingsCommand());
//		c.addCommand(new BulkModeledReadingCommand());
//		c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getInstanceAssetMappingChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new InstanceAssetMappingCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getMarkUnmodeledInstanceChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new MarkUnmodeledInstanceCommand());
//		c.addCommand(new PublishConfigMsgToIoTCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getSubscribeInstanceChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new SubscribeInstanceIoTCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getChangePreventiveMaintenanceStatusChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new ChangePreventiveMaintenanceStatusCommand());
//		c.addCommand(new DeletePMAndDependenciesCommand(false, true));
//		c.addCommand(new AddPMTriggerCommand(true));
//		c.addCommand(new AddPMReminderCommand(true));
//		c.addCommand(new SetMissingRelInResourcePlannersCommand());
//		c.addCommand(new AddPMRelFieldsCommand(true));
//		c.addCommand(new SchedulePMCommand(true));
//		c.addCommand(new scheduleBeforePMRemindersCommand(true));
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getExecutePreventiveMaintenanceChain() {
//		Chain c = getDefaultChain();
//
//		c.addCommand(new ForkChainToInstantJobCommand().addCommand(new ResetTriggersCommand())
//				.addCommand(new SchedulePrePMRemindersCommand()));
//
//		c.addCommand(new PreparePMForMultipleAsset());
//		c.addCommand(new ExecutePMCommand());
//		c.addCommand(new SchedulePostPMRemindersCommand());
//
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getExecutePMsChain() { // from Bulk Execute
//		Chain c = getDefaultChain();
//		c.addCommand(new ExecutePMsCommand());
//		c.addCommand(new SchedulePostPMRemindersCommandForBulkExecutePm());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getMigrateReadingDataChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new MigrateReadingDataCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	public static Chain getSetReadingInputValuesChain() {
//		Chain c = getDefaultChain();
//		c.addCommand(new SetReadingInputValuesCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
//		return c;
//	}
//
//	private static Chain getDefaultChain() {
//		return new FacilioChain(true);
//	}

		
		public static Chain getOrgSignupChain() {
			Chain c = getDefaultChain();
			c.addCommand(new CreateOrgCommand());
			c.addCommand(new AddDefaultModulesCommand());
			c.addCommand(new AddDefaultReportCommand());
			c.addCommand(new AddDefaultUnitsCommand());
			c.addCommand(new AddEventModuleCommand());
			c.addCommand(new AddOrgInfoCommand());
			c.addCommand(new CreateSuperAdminCommand());
			return c;
		}

		public static Chain getAddNotesChain() {
			FacilioChain c = (FacilioChain) getDefaultChain();
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddNotesCommand());
			c.addCommand(new ExecuteNoteWorkflowCommand());
			c.addCommand(new AddNoteTicketActivityCommand());
//			c.setPostTransactionChain(getUpdateTicketNotesChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		
		public static Chain getUpdateTicketNotesChain() {
			Chain c1 = getDefaultChain();
			c1.addCommand(new UpdateNotesCountCommand());
			return c1;
		}
		
		public static Chain getUpdateTaskCountChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateTaskCountCommand());
			return c;
		}
		
		public static Chain getUpdateAttachmentCountChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateAttachmentCountUpdateCommand());
			return c;
		}
	
		public static Chain historicalFormulaCalculationChain() {
			Chain c = getDefaultChain();
			c.addCommand(new HistoricalFormulaCalculationCommand());
			return c;
		}
		
		public static Chain runThroughReadingRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new RunThroughReadingRulesCommand());
			return c;
		}
		
		public static Chain historicalScheduledRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddHistoricalScheduledRuleJobCommand());
			return c;
		}

		public static Chain getAddPreOpenedWorkOrderChain() {
			Chain c = getDefaultChain();
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

		public static Chain getWorkOrderWorkflowsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.STATE_FLOW));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.BUSSINESS_LOGIC_WORKORDER_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_CUSTOM_CHANGE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 218L) {
				c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE));
			} else {
				c.addCommand(new ForkChainToInstantJobCommand()
						.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE)));
			}
			return c;
		}
		public static Chain getAddWorkOrderChain() {
			Chain c = getDefaultChain();
			c.addCommand(new PMSettingsCommand());
			c.addCommand(new GetFormMetaCommand());
			c.addCommand(new ValidateFormCommand());
			c.addCommand(new AddRequesterCommand());
			c.addCommand(SetTableNamesCommand.getForWorkOrder());
			c.addCommand(new ValidateWorkOrderFieldsCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new UpdateEventListForStateFlowCommand());
			c.addCommand(new AddWorkOrderCommand());
			c.addCommand(new AddAttachmentCommand());
			c.addCommand(new AttachmentContextCommand());
			c.addCommand(new AddAttachmentRelationshipCommand());
			c.addCommand(new AddTicketActivityCommand());
			c.addCommand(getAddTasksChain());
			c.addCommand(getWorkOrderWorkflowsChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static Chain getAddNewTasksChain() {
			FacilioChain c = (FacilioChain) getDefaultChain();
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

		public static Chain getAddTasksChain() {
			FacilioChain c = (FacilioChain) getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new ValidateTasksCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddTaskSectionsCommand());
			c.addCommand(new AddActionForTaskCommand());
			c.addCommand(new AddTasksCommand());
			c.addCommand(new AddTaskOptionsCommand());
			c.addCommand(new UpdateReadingDataMetaCommand());
			c.addCommand(new AddTaskDefaultValueReadingsCommand());
			c.addCommand(new UpdateTaskReadingInfoCommand());
			// c.addCommand(new AddTaskTicketActivityCommand());
//			c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		
		public static Chain getUpdateWorkOrderChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrder());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new FetchOldWorkOrdersCommand());
			c.addCommand(new VerifyApprovalCommand());
			c.addCommand(new UpdateEventListForStateFlowCommand());
			c.addCommand(new UpdateWorkOrderCommand());
			c.addCommand(new BackwardCompatibleStateFlowUpdateCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new SendNotificationCommand());
			c.addCommand(new AddTicketActivityCommand());
//			c.addCommand(getAddOrUpdateReadingValuesChain());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.BUSSINESS_LOGIC_WORKORDER_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ExecuteStateTransitionsCommand(RuleType.STATE_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE))
				.addCommand(new ClearAlarmOnWOCloseCommand())
				.addCommand(new ExecuteTaskFailureActionCommand())
			);
			c.addCommand(new ConstructTicketNotesCommand());
			c.addCommand(getAddNotesChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		
		public static Chain addOrUpdateReadingReportChain() {
			Chain c = getDefaultChain();
			c.addCommand(new CreateReadingAnalyticsReportCommand());
			c.addCommand(new AddOrUpdateReportCommand());
			return c;
		}
		
		public static Chain addOrUpdateReportChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddOrUpdateReportCommand());
			return c;
		}
		
		public static Chain addWorkOrderReportChain() {
			Chain c = getDefaultChain();
			c.addCommand(new CreateWorkOrderAnalyticsReportCommand());
			c.addCommand(new AddOrUpdateReportCommand());
			return c;
		}
		
		public static Chain getAddPhotoChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UploadPhotosCommand());
			return c;
		}
		public static Chain getEditControllerChain() {
			Chain c = getDefaultChain();
			c.addCommand(new FetchControllerSettingCommand());
			c.addCommand(new EditControllerSettingsCommand());
			c.addCommand(new PublishControllerPropertyToIoTCommand());
			return c;
		}
		
		public static Chain getDeleteControllerChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteControllerCommand());
			return c;
		}

		public static Chain demoRollUpChain () {
			Chain c = getDefaultChain();
			c.addCommand(new DemoRollUpCommand());
			return c;
		}


		public static Chain editAgent(){
			Chain c = getDefaultChain();
			c.addCommand(new ConfigureAgentCommand());
			return c;
		}

		public static Chain updateAgentTable(){
		Chain c = getDefaultChain();
		c.addCommand(new UpdateAgentTableCommand());
		return c;
		}

		public static Chain updateAgent(){
		Chain c = getDefaultChain();
		c.addCommand(new UpdateAgentDetailsCommand());
		return c;
		}

        public static Chain getUpdateQueryChain(){
	    Chain c = getDefaultChain();

	    return c;
        }

        public static Chain getUpdateAgentMessageChain(){
	    Chain c = getDefaultChain();
	    c.addCommand(new UpdateAgentMessageCommand());
	    return c;
        }

        public static Chain getAddAgentMessageChain(){
	    Chain c = getDefaultChain();
	    c.addCommand(new AddAgentMessageCommand());
        return c;
        }

        public static Chain getAddAgentMetricsChain(){
	    Chain c = getDefaultChain();
	    c.addCommand(new AddAgentMetricsCommand());
	    return c;
        }

        public static Chain getUpdateAgentMetricsChain(){
	    Chain c = getDefaultChain();
	    c.addCommand(new UpdateAgentMetricsCommand());
	    return c;
        }

        public static Chain addLogChain(){
	    Chain c = getDefaultChain();
	    c.addCommand(new AddLogChainCommand() );
	    return c;
        }

        public static Chain getAgentEditChain(){
		Chain c = getDefaultChain();
		c.addCommand(new AgentEditCommand());
		return c;
		}
		public static Chain getAddAgentChain(){
		Chain c = getDefaultChain();
		c.addCommand(new AgentCreate());
		return c;
		}

		public static Chain deleteAgent(){
			Chain c = getDefaultChain();
			c.addCommand(new DeleteAgentCommand());
			return c;
		}

		public static Chain updateAckChain(){
		Chain c = getDefaultChain();
		c.addCommand(new AckUpdateCommand());
		return c;
		}

		public static Chain controllerEdit(){
			Chain c = getDefaultChain();
			c.addCommand(new ConfigureControllerCommand());
			return c;
		}
		public static Chain updateScheduledReportsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddTemplateCommand());
			c.addCommand(new DeleteScheduledReportsCommand(true));
			c.addCommand(new ScheduleV2ReportCommand());
			return c;
		}
		
		public static Chain deleteScheduledReportsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteScheduledReportsCommand(true));
			return c;
		}
		
		public static Chain getExportModuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ExportCommand());
			return c;
		}
		
		
		public static Chain sendModuleMailChain () {
			Chain c = getDefaultChain();
			c.addCommand(FacilioChainFactory.fetchExportModuleChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}
		
		public static Chain addWorkflowRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddWorkflowRuleCommand());
			c.addCommand(new AddActionsForWorkflowRule());
			return c;
		}
		
		public static Chain addApprovalRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ConstructApprovalRuleCommand());
			c.addCommand(addWorkflowRuleChain());
			c.addCommand(new AddApproverActionRelCommand());
			return c;
		}
		
		public static Chain getAddScheduledActionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddActionsForWorkflowRule());
			c.addCommand(new AddScheduledActionCommand());
			return c;
		}
		
		public static Chain editViewChain() {
			Chain c = getDefaultChain();
			c.addCommand(new LoadViewCommand());
			c.addCommand(new GenerateCriteriaFromFilterCommand());
			c.addCommand(new AddCVCommand());
			c.addCommand(new CustomizeViewColumnCommand());
			return c;
		}
		public static Chain deleteViewChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteViewCommand());
			return c;
		}
		public static Chain updateWorkflowRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateWorkflowRuleCommand());
			c.addCommand(new DeleterOldRuleActionsCommand());
			c.addCommand(new AddActionsForWorkflowRule());
			return c;
		}
		
		public static Chain addAlarmRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddAlarmRuleCommand());
			c.addCommand(new AddActionForAlarmRuleCommand());
			c.addCommand(new AddJobEntryForScheduledReadingRuleCommand());
			c.addCommand(new AddReadingAlarmRuleCommand());
			return c;
		}
		
		public static Chain updateAlarmRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateAlarmRuleCommand());
			c.addCommand(new AddActionForAlarmRuleCommand());
			c.addCommand(new AddJobEntryForScheduledReadingRuleCommand());
			c.addCommand(new AddReadingAlarmRuleCommand());
			return c;
		}
		
		public static Chain updateVersionedWorkflowRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateWorkflowRuleCommand());
			c.addCommand(addWorkflowRuleChain());
			return c;
		}
		
		public static Chain updateApprovalRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ConstructApprovalRuleCommand());
			c.addCommand(updateVersionedWorkflowRuleChain());
			c.addCommand(new AddApproverActionRelCommand());
			return c;
		}
		
		public static Chain getAddAssetChain() {
			Chain c = getDefaultChain();
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
		
		public static Chain getUpdateAssetChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddCategoryOnAssetUpdateCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new GenericGetModuleDataListCommand());
			c.addCommand(new UpdateStateForModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(FacilioChainFactory.getCategoryReadingsChain());
			c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
			return c;
		}
		
		public static Chain getUpdateQrChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateQrCommand());
			return c;
		}
		
		public static Chain getImportChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ProcessImportCommand());
			c.addCommand(new SwitchToAddResourceChain());
			return c;
		}

		public static Chain parseReadingDataForImport() {
			Chain c = getDefaultChain();
			c.addCommand(new ParseDataForReadingLogsCommand());
			c.addCommand(new InsertImportDataIntoLogCommand());
			return c;
		}

		public static Chain parseImportData() {
			Chain c = getDefaultChain();
			c.addCommand(new GenericParseDataForImportCommand());
			c.addCommand(new InsertImportDataIntoLogCommand());
			return c;
		}

		public static Chain getImportReadingChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ConstructVirtualSheetForReadingsImport());
			c.addCommand(new InsertReadingCommand());
			c.addCommand(new WriteSkippedToFileCommand());
			c.addCommand(new SendEmailCommand());
			return c;
		}


		public static Chain getGenericImportChain() {
			Chain c= getDefaultChain();
			c.addCommand(new PopulateImportProcessCommand());
			c.addCommand(new UpdateBaseAndResourceCommand());
			c.addCommand(new InsertReadingDataMetaForImport());
			c.addCommand(new SendEmailCommand());
			return c;
		}
		
		public static Chain getBulkAssertImportChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ProcessImportCommand());
			c.addCommand(new SeperateToCategoriesCommand());
			// c.addCommand(new FilterForInsertSkipCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new BulkPushAssetCommands());
			c.addCommand(new UpdateBaseAndResourceCommand());
			c.addCommand(new InsertReadingDataMetaForImport());
			c.addCommand(new SendEmailCommand());
			return c;
		}
		
		public static Chain scheduledRuleExecutionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new FetchScheduledRuleMatchingRecordsCommand());
			c.addCommand(new ExecuteSingleWorkflowRuleCommand());
			return c;
		}
		
		public static Chain controllerActivityAndWatcherChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateCheckPointAndAddControllerActivityCommand());
			c.addCommand(new CheckAndStartWatcherCommand());
			return c;
		}
		
		public static Chain getAddAlarmFromEventChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ProcessAlarmCommand());
			c.addCommand(getAddAlarmChain());
			return c;
		}
		public static Chain addBusinessHourChain () {
			Chain c = FacilioChain.getTransactionChain();
			c.addCommand(new AddBusinessHourCommand());
			c.addCommand(new AddSingleDayBusinessHourCommand());
			c.addCommand(new UpdateBusinessHourInResourceCommand());
			return c;
		}
		public static Chain updateBusinessHoursChain() {
			Chain c = FacilioChain.getTransactionChain();
			c.addCommand(new UpdateBusinessHoursCommand());
			c.addCommand(new DeleteSingleDayBusinessHoursCommand());
			c.addCommand(new AddSingleDayBusinessHourCommand());
			return c;
		}
		public static Chain updateBusinessHourInResourceChain () {
			Chain c = FacilioChain.getTransactionChain();
			c.addCommand(new UpdateBusinessHourInResourceCommand());
			return c;
		}
		public static Chain deleteBusinessHoursChain () {
			Chain c = FacilioChain.getTransactionChain();
			c.addCommand(new DeleteBusinessHourCommand());
			c.addCommand(new DeleteSingleDayBusinessHoursCommand());
			return c;
		}
		
		
		
		public static Chain getAddAlarmChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForAlarm());
			c.addCommand(new AddAlarmCommand());
			c.addCommand(new AddMLOccurrenceCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.READING_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.BUSSINESS_LOGIC_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.PM_ALARM_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ALARM_NOTIFICATION_RULE, RuleType.CUSTOM_ALARM_NOTIFICATION_RULE))
			);
			c.addCommand(new AddAlarmFollowersCommand());
			c.addCommand(new ConstructTicketNotesCommand());
			c.addCommand(getAddNotesChain());
			return c;
		}
		
		public static Chain updateAlarmFromJsonChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ProcessAlarmCommand());
			c.addCommand(getUpdateAlarmChain());
			return c;
		}
		
		public static Chain getUpdateAlarmChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForAlarm());
			c.addCommand(new UpdateAlarmCommand());
			c.addCommand(new AddMLOccurrenceCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.BUSSINESS_LOGIC_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.PM_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.READING_ALARM_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ALARM_NOTIFICATION_RULE, RuleType.CUSTOM_ALARM_NOTIFICATION_RULE))
				.addCommand(new AddClearCommentInWoOnAlarmClearCommand())
			);
			c.addCommand(new ConstructTicketNotesCommand());
			c.addCommand(getAddNotesChain());
			return c;
		}
		
		public static Chain getAddWoFromAlarmChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddWOFromAlarmCommand());
			c.addCommand(getAddWorkOrderChain());
			c.addCommand(new UpdateWoIdInAlarmCommand());
			return c;
		}
		
		public static Chain getAddModuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new CreateCustomModuleCommand());
			commonAddModuleChain(c);
			return c;
		}
		
		public static Chain getAddReadingsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new CreateReadingModulesCommand());
			commonAddModuleChain(c);
			return c;
		}
		
		public static void commonAddModuleChain(Chain c) {
			c.addCommand(new AddModulesCommand());
			c.addCommand(new SetColumnNameForNewCFsCommand());
			c.addCommand(new AddFieldsCommand());
		}
		
		public static Chain getAddCategoryReadingChain() {
			Chain c = getDefaultChain();
			c.addCommand(new GetCategoryModuleCommand());
			c.addCommand(getAddReadingsChain());
			c.addCommand(new AddCategoryReadingRelCommand());
			c.addCommand(new GetCategoryResourcesCommand());
			c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
			c.addCommand(new SetValidationRulesContextCommand());
			c.addCommand(new AddValidationRulesCommand());
			return c;
		}
		
		public static Chain addResourceReadingChain() {
			Chain c = getDefaultChain();
			c.addCommand(getAddReadingsChain());
			c.addCommand(new AddResourceReadingRelCommand());
			c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
			return c;
		}
		
		public static Chain getAddFieldsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new SetColumnNameForNewCFsCommand());
			c.addCommand(new AddFieldsCommand());
			return c;
		}
		
		public static Chain addFormulaFieldChain() {
			Chain c = getDefaultChain();
			c.addCommand(new CreateFormulaFieldDependenciesCommand());
			c.addCommand(new GetCategoryModuleCommand());
			c.addCommand(getAddReadingsChain());
			c.addCommand(new AddResourceReadingRelCommand());
			c.addCommand(new AddCategoryReadingRelCommand());
			c.addCommand(new GetCategoryResourcesCommand());
			c.addCommand(new InsertReadingDataMetaForNewReadingCommand());
			c.addCommand(new AddFormulaFieldCommand());
			c.addCommand(new SetValidationRulesContextCommand());
			c.addCommand(new AddValidationRulesCommand());
			return c;
		}
		
		public static Chain addDerivationFormulaChain() {
			Chain c = getDefaultChain();
			c.addCommand(addFormulaFieldChain());
			c.addCommand(new UpdateDerivationCommand());
			return c;
		}
		
		public static Chain onlyAddOrUpdateReadingsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new GetReadingDataMetaCommand());
			c.addCommand(new ReadingUnitAndInputConversionCommand());
			c.addCommand(new EnergyDataDeltaCalculationCommand());
			c.addCommand(new CalculateDeltaCommand());
			c.addCommand(new CalculatePreFormulaCommand());
			c.addCommand(new ExecuteValidationRule());
			c.addCommand(new AddOrUpdateReadingValuesCommand());
			c.addCommand(new AddMarkedReadingValuesCommand());
			c.addCommand(new SpaceBudIntegrationCommand());	//For RMZ-SpaceBud Integration
			c.addCommand(new ReadingUnitConversionToDisplayUnit());
			return c;
		}
		
		public static Chain getUpdateTaskChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ValidateAndCreateValuesForInputTaskCommand());
			c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new UpdateTaskCommand());
			c.addCommand(new UpdateClosedTasksCounterCommand());
			c.addCommand(new AddTaskTicketActivityCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddActivitiesCommand());
//			c.addCommand(getAddOrUpdateReadingValuesChain());
			return c;
		}

	public static Chain getUpdatePreRequestChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTask());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new UpdateTaskCommand());
		return c;
	}
		public static Chain getProcessDataChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ProcessDataCommand());
			c.addCommand(new ModeledDataCommand());
			c.addCommand(new UnModeledDataCommand());
			c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
			return c;
		}
		
		public static Chain getProcessHistoricalDataChain() {
			Chain c = getDefaultChain();
			c.addCommand(new HistoricalReadingsCommand());
			c.addCommand(new BulkModeledReadingCommand());
			c.addCommand(ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain());
			return c;
		}
		
		public static Chain getInstanceAssetMappingChain() {
			Chain c = getDefaultChain();
			c.addCommand(new InstanceAssetMappingCommand());
			return c;
		}
		
		public static Chain getMarkUnmodeledInstanceChain() {
			Chain c = getDefaultChain();
			c.addCommand(new MarkUnmodeledInstanceCommand());
			c.addCommand(new PublishConfigMsgToIoTCommand());
			return c;
		}
		
		public static Chain getSubscribeInstanceChain() {
			Chain c = getDefaultChain();
			c.addCommand(new SubscribeInstanceIoTCommand());
			return c;
		}
		
		public static Chain getUnSubscribeInstanceChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UnsubscribeInstanceIoTCommand());
			return c;
		}

		public static Chain getChangeNewPreventiveMaintenanceStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ChangePreventiveMaintenanceStatusCommand());
			c.addCommand(new DeletePMAndDependenciesCommand(false, true));
			c.addCommand(new AddPMTriggerCommand(true));
			c.addCommand(new AddPMReminderCommand(true));
			c.addCommand(new SetMissingRelInResourcePlannersCommand());
			c.addCommand(new CreateTaskSectionTriggerRelCommand());
			c.addCommand(new AddPMRelFieldsCommand(true));
			c.addCommand(new BlockPMEditOnWOGeneration(true, true));
			c.addCommand(new ScheduleCreateWOJob(true, true));
			return c;
		}

		public static Chain getChangeNewPreventiveMaintenanceStatusChainForMig() {
			Chain c = getDefaultChain();
			c.addCommand(new ChangePreventiveMaintenanceStatusCommand());
			c.addCommand(new DeletePMAndDependenciesCommand(false, true));
			c.addCommand(new AddPMTriggerCommand(true));
			c.addCommand(new AddPMReminderCommand(true));
			c.addCommand(new SetMissingRelInResourcePlannersCommand());
			c.addCommand(new AddPMRelFieldsCommand(true));
			c.addCommand(new ScheduleNewPMCommand(true));
			return c;
		}

		public static Chain getPMMigration(long pmId) {
			Chain c = getDefaultChain();
			c.addCommand(getChangeNewPreventiveMaintenanceStatusChainForMig());
			c.addCommand(new ResetContext(pmId));
			c.addCommand(getChangeNewPreventiveMaintenanceStatusChainForMig());
			return c;
		}

		public static Chain getNewExecutePreventiveMaintenanceChain() {
			Chain c = getDefaultChain();
			c.addCommand(new PreparePMForMultipleAsset());
			c.addCommand(new ExecutePMCommand());
			c.addCommand(new ResetNewTriggersCommand());
			return c;
		}

		public static Chain getExecutePMsChain() {		// from Bulk Execute
			Chain c = getDefaultChain();
			c.addCommand(new ExecutePMsCommand());
			c.addCommand(new SchedulePostPMRemindersCommandForBulkExecutePm());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		
		public static Chain getMigrateReadingDataChain() {
			Chain c = getDefaultChain();
			c.addCommand(new MigrateReadingDataCommand());
			return c;
		}
		
		public static Chain getSetReadingInputValuesChain() {
			Chain c = getDefaultChain();
			c.addCommand(new SetReadingInputValuesCommand());
			return c;
		}
		
		public static Chain scheduleReportChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddTemplateCommand());
			c.addCommand(new ScheduleV2ReportCommand());
			return c;
		}

		public static Chain getExportReportFileChain() {
			Chain c = getDefaultChain();
			c.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
			c.addCommand(new GetExportReportFileCommand());
			return c;
		}

		public static Chain sendReportMailChain() {
			Chain c = getDefaultChain();
			c.addCommand(getExportReportFileChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}

		public static Chain getExportAnalyticsFileChain() {
			Chain c = getDefaultChain();
			c.addCommand(ReadOnlyChainFactory.newFetchReadingReportChain());
			c.addCommand(new GetExportReportFileCommand());
			return c;
		}

		public static Chain sendAnalyticsMailChain() {
			Chain c = getDefaultChain();
			c.addCommand(getExportAnalyticsFileChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}

		public static Chain getExportModuleReportFileChain() {
			Chain c = getDefaultChain();
			c.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
			c.addCommand(new GetExportModuleReportFileCommand());
			return c;
		}

		public static Chain sendModuleReportMailChain() {
			Chain c = getDefaultChain();
			c.addCommand(getExportModuleReportFileChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}

		public static Chain getExportModuleAnalyticsFileChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ConstructReportData());
			c.addCommand(getExportModuleReportFileChain());
			return c;
		}

		public static Chain sendModuleAnalyticsMailChain() {
			Chain c = getDefaultChain();
			c.addCommand(getExportModuleAnalyticsFileChain());
			c.addCommand(new SendReadingReportMailCommand());
			return c;
		}


	    public static Chain getAddWidgetChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddWidgetCommand());
			return c;
		}
	    public static Chain getAddDashboardChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddDashboardCommand());
			return c;
		}
	    public static Chain getUpdateDashboardChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DuplicateDashboardForBuildingCommand());
			c.addCommand(new UpdateDashboardWithWidgetCommand());
			c.addCommand(new EnableMobileDashboardCommand());
			return c;
		}
	    public static Chain getUpdateDashboardsChain() {
			Chain c = FacilioChain.getTransactionChain();
			c.addCommand(new DuplicateBaseSpaceForDashboard());
			c.addCommand(new UpdateDashboardsCommand());
			return c;
		}

	    public static Chain getDeleteDashboardChain() {
			Chain c = FacilioChain.getTransactionChain();
			c.addCommand(new DeleteDashboardCommand());
			return c;
		}

	    public static Chain getAddDashboardFolderChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddDashboardFolderCommand());
			return c;
		}
	    public static Chain getUpdateDashboardFolderChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateDashboardFolderCommand());
			return c;
		}
	    public static Chain getDeleteDashboardFolderChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteDashboardFolderCommand());
			return c;
		}

		public static Chain executeScheduledReadingRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ExecuteScheduledReadingRuleCommand());
			return c;
		}
		
		public static Chain executeScheduledAlarmTriggerChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ExecuteScheduledAlarmTriggerCommand());
			return c;
		}
		
		public static Chain getAddInventoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventory());
			// c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddInventoryCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static Chain getUpdateInventoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventory());
			c.addCommand(new UpdateInventoryCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static Chain getDeleteInventoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventory());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static Chain getAddInventoryVendorChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryVendor());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static Chain getUpdateInventoryVendorChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryVendor());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static Chain getDeleteVendorChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryVendor());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static Chain getAddInventoryCategoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryCategory());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static Chain getUpdateInventoryCategoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryCategory());
			c.addCommand(new GenericUpdateModuleDataCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}

		public static Chain getDeleteInventoryCategoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryCategory());
			c.addCommand(new GenericDeleteModuleDataCommand());
//			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getAddOrUdpateWorkorderPartChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderParts());
			c.addCommand(new AddOrUpdateWorkorderPartCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}
		
		public static Chain getDeleteWorkorderPartChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderParts());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		
		public static Chain getAddWorkorderCostChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			return c;
		}
		
		public static Chain getAddStoreRoomChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForStoreRoom());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new DeleteSitesForStoreRoomCommad());
			c.addCommand(new AddSitesForStoreRoomCommand());
			return c;
		}
		
		public static Chain getUpdateStoreRoomChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForStoreRoom());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new DeleteSitesForStoreRoomCommad());
			c.addCommand(new AddSitesForStoreRoomCommand());
			return c;
		}
		
		public static Chain getAddItemTypeCategoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesCategory());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static Chain getUpdateItemTypeCategoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesCategory());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static Chain getDeleteItemTypesCategoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesCategory());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		
		public static Chain getAddItemTypesStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypeStatus());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static Chain getUpdateItemTypesStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypeStatus());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static Chain getDeleteItemTypesStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypeStatus());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		
		public static Chain getAddItemTypesChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypes());
			c.addCommand(new GenericAddModuleDataCommand());
			return c;
		}
		
		public static Chain getUpdateItemTypesChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypes());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static Chain getAddOrUpdateConnectedAppChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForConnectedApps());
			c.addCommand(new AddOrUpdateConnectedAppCommand());
			return c;
		}

		public static Chain getDeleteConnectedAppChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForConnectedApps());
			c.addCommand(new DeleteConnectedAppCommand());
			return c;
		}
		
		public static Chain getAddToolTypesCategoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesCategory());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static Chain getUpdateToolTypesCategoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesCategory());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static Chain getDeleteToolTypesCategoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesCategory());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static Chain getAddToolsStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesStatus());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static Chain getUpdateToolsStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesStatus());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static Chain getDeleteToolsStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypesStatus());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		
		public static Chain getAddToolStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolStatus());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static Chain getUpdateToolStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolStatus());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static Chain getDeleteToolStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolStatus());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		
		public static Chain getAddToolTypesChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypes());
			c.addCommand(new GenericAddModuleDataCommand());
			return c;
		}
		
		public static Chain getBulkAddToolTypesChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypes());
			c.addCommand(new GenericAddModuleDataListCommand());
			return c;
		}
		
		public static Chain getUpdateToolTypesChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTypes());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}
		
		public static Chain getAddVendorChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVendors());
			c.addCommand(new GenericAddModuleDataCommand());
			return c;
		}
		
		public static Chain getUpdateVendorChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForVendors());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}
		
		public static Chain getAddItemStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemStatus());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static Chain getUpdateItemStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemStatus());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static Chain getItemStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemStatus());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		
		public static Chain getAddItemChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItem());
			c.addCommand(new AddItemCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getAddPurchasedItemChain());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		
		public static Chain getAddBulkItemChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItem());
			c.addCommand(new BulkItemAdditionCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getAddBulkPurchasedItemChain());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			return c;
		}
		
		public static Chain getUpdateItemChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItem());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(getAddPurchasedItemChain());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		
		public static Chain getAddPurchasedItemChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedItem());
			c.addCommand(new GetAddPurchasedItemCommand());
			c.addCommand(getAddOrUpdateItemStockTransactionChain());
			return c;
		}
		
		
		public static Chain getAddBulkPurchasedItemChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedItem());
			c.addCommand(new AddPurchasedItemsForBulkItemAddCommand());
			c.addCommand(getAddOrUpdateItemStockTransactionChain());
			return c;
		}
		
		public static Chain getAddOrUpdateItemStockTransactionChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new AddOrUpdateItemStockTransactionsCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			return c;
		}
		
		public static Chain getUpdateItemQuantityRollupChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddOrUpdateItemQuantityCommand());
			c.addCommand(getUpdateItemTypeQuantityRollupChain());
			return c;
		}
		
		public static Chain getUpdateItemTypeQuantityRollupChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ItemTypeQuantityRollupCommand());
			return c;
		}
		
		public static Chain getUpdateInventoryCostChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedItem());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			return c;
		}
		
		public static Chain getDeleteInventoryCostChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedItem());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}
		
		public static Chain getAddOrUdpateWorkorderItemsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderItems());
			c.addCommand(new AddOrUpdateWorkorderItemsCommand());
//			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new ItemTransactionRemainingQuantityRollupCommand());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}
		
		public static Chain getDeleteWorkorderItemChain() {
			Chain c = getDefaultChain();
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
		
		public static Chain getAddToolChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTool());
			c.addCommand(new AddToolCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getAddPurchasedToolChain());
//			c.addCommand(getAddOrUpdateToolStockTransactionChain());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			return c;
		}
		
		public static Chain getBulkAddToolChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTool());
			c.addCommand(new BulkToolAdditionCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddBulkToolStockTransactionsCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			return c;
		}
		
		public static Chain getUpdateToolChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTool());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}
		
		public static Chain getAddOrUdpateWorkorderToolsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderTools());
			c.addCommand(new AddOrUpdateWorkorderToolsCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static Chain getAddOrUdpateWorkorderLabourChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderLabour());
			c.addCommand(new AddOrUpdateWorkorderLabourCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			//c.addCommand(getUpdateAvailabilityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}


		public static Chain getUpdatetoolQuantityRollupChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ToolQuantityRollUpCommand());
			c.addCommand(getUpdateToolTypeQuantityRollupChain());
			return c;
		}

		public static Chain getUpdateAvailabilityRollupChain() {
			Chain c = getDefaultChain();
			c.addCommand(new LabourAvailabilityRollUpCommand());
			return c;
		}

		public static Chain getUpdateToolTypeQuantityRollupChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ToolTypeQuantityRollupCommand());
			return c;
		}
		
		public static Chain getDeleteWorkorderToolsChain() {
			Chain c = getDefaultChain();
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

		public static Chain getDeleteWorkorderLabourChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderLabour());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteWorkorderLabourCommand());
			//c.addCommand(getUpdateAvailabilityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}
		public static Chain getAddOrUpdateWorkorderCostChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			return c;
		}
		
		public static Chain getUpdateWorkorderCostChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			return c;
		}
		
		public static Chain getDeleteWorkorderCostChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			return c;
		}
		
		public static Chain getAssetFromQRChain() {
			Chain c = getDefaultChain();
			//c.addCommand(new ParseQRValueCommand());
			c.addCommand(new FetchAssetFromQRValCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new LoadAssetFields());
			c.addCommand(new GetAssetDetailCommand());
			c.addCommand(new UpdateGeoLocationCommand());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static Chain getAddItemTypesVendorsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
			c.addCommand(new GenericAddModuleDataListCommand());
			return c;
		}
		
		public static Chain getUpdateItemTypesVendorsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
			c.addCommand(new GenericUpdateListModuleDataCommand());
			return c;
		}
		
		public static Chain getDeleteItemTypesVendorsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}
		
		public static Chain getAddOrUpdateItemTransactionsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new AddOrUpdateManualItemTransactionCommand());
			c.addCommand(getItemTransactionRemainingQuantityRollupChain());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new UpdateRequestedItemIssuedQuantityCommand());
			c.addCommand(new AddActivitiesCommand());

			return c;
		}
		
		public static Chain getItemTransactionRemainingQuantityRollupChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new ItemTransactionRemainingQuantityRollupCommand());
			return c;
		}
		public static Chain getDeleteItemTransactionsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteItemTransactionCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getItemTransactionRemainingQuantityRollupChain());
			c.addCommand(getUpdateItemQuantityRollupChain());
			return c;
		}
		
		public static Chain getUpdateInventoryTransactionsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			return c;
		}
		
		public static Chain getAddPurchasedToolChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedTool());
			c.addCommand(new GetAddPurchasedToolCommand());
			c.addCommand(getAddOrUpdateToolStockTransactionChain());
			return c;
		}
		
		public static Chain getDeletePurchasedToolsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchasedTool());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}
		
		public static Chain getAddOrUpdateToolStockTransactionChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
			c.addCommand(new AddOrUpdateToolStockTransactionsCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			return c;
		}
		
		public static Chain getAddOrUdpateToolTransactionsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
			c.addCommand(new AddOrUpdateManualToolTransactionsCommand());
			c.addCommand(getToolTransactionRemainingQuantityRollupChain());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new UpdateRequestedToolIssuedQuantityCommand());

			return c;
		}
		
		public static Chain getToolTransactionRemainingQuantityRollupChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
			c.addCommand(new ToolTransactionRemainingQuantityRollupCommand());
			return c;
		}
		
		public static Chain getDeleteToolTransactChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteToolTransactionCommand());
			c.addCommand(getToolTransactionRemainingQuantityRollupChain());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			return c;
		}
		
		public static Chain getSetItemAndToolTypeForStoreRoomChain() {
			Chain c = getDefaultChain();
			c.addCommand(new SetItemAndToolTypeForStoreRoomCommand());
			return c;
		}

		public static Chain getApproveRejectWorkorderItemsChain() {
			Chain c = getDefaultChain();
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
		
		public static Chain getApproveRejectManualItemsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
			c.addCommand(new ApproveOrRejectItemCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new AddActivitiesCommand());
			return c;
		}

		public static Chain getApproveRejectWorkorderToolsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
//			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new ApproveOrRejectToolCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
//			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}
		
		public static Chain getApproveRejectManualToolsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
//			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new ApproveOrRejectToolCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			return c;
		}

		public static Chain getAddLabourChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabour());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}

		public static Chain getDeleteLabourChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabour());
			c.addCommand(new DeleteLabourCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			return c;
		}


		public static Chain getUpdateLabourChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabour());
			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static Chain getAddPurchaseRequestChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseRequest());
			chain.addCommand(new AddOrUpdatePurchaseRequestCommand());
			chain.addCommand(getPurchaseRequestTotalCostChain()); //update purchase request total cost
			return chain;
		}

		public static Chain getUpdatePurchaseRequestStatusChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseRequest());
			chain.addCommand(new UpdateBulkPurchaseRequestStatusCommand());
			return chain;
		}

		public static Chain getPurchaseRequestDeleteChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseRequest());
			c.addCommand(new DeletePurchaseRequestCommand());
			return c;
		}

		public static Chain getAddPurchaseRequestLineItem() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseRequestLineItem());
			c.addCommand(new AddOrUpdatePurchaseRequestLineItemCommand());
			c.addCommand(getPurchaseRequestTotalCostChain()); //update purchase request total cost
			return c;
		}

		public static Chain getDeletePurchaseRequestLineItem() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseRequestLineItem());
			c.addCommand(new DeletePurchaseRequestLineItemCommand());
			return c;
		}
		
		public static Chain getAddPurchaseOrderChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseOrder());
			chain.addCommand(new AddOrUpdatePurchaseOrderCommand());
			chain.addCommand(getPurchaseOrderTotalCostChain()); //update purchase order total cost
			chain.addCommand(new AddPurchaseRequestOrderRelation());
			return chain;
		}

		public static Chain getPurchaseOrderDeleteChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseOrder());
			c.addCommand(new DeletePurchaseOrderCommand());
			c.addCommand(new DeleteReceivableByPoIdCommand());
			return c;
		}

		public static Chain getAddPurchaseOrderLineItem() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseOrderLineItem());
			c.addCommand(new AddOrUpdatePurchaseOrderLineItemCommand());
			c.addCommand(getPurchaseOrderTotalCostChain()); //update purchase order total cost
			return c;
		}

		public static Chain getUpdatePurchaseOrderStatusChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseOrder());
			chain.addCommand(new UpdateBulkPurchaseOrderStatusCommand());
			return chain;
		}

		public static Chain getDeletePurchaseOrderLineItem() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseOrderLineItem());
			c.addCommand(new DeletePurchaseOrderLineItemCommand());
			return c;
		}

		public static Chain getAddOrUpdateReceiptsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForReceipt());
			c.addCommand(new AddOrUpdateReceiptCommand());
			c.addCommand(getPurchaseOrderLineItemQuantityRecievedRollUpChain());
			c.addCommand(getPurchaseOrderQuantityRecievedRollUpChain());
			c.addCommand(getPurchaseOrderAutoCompleteChain());
			return c;
		}
		
		public static Chain getAddOrUpdateReceivablesChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForReceivables());
			c.addCommand(new AddOrUpdateReceivablesCommand());
			return c;
		}

		public static Chain getConvertPRToPOChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ConvertPRToPOCommand());
			return c;
		}
		public static Chain getPurchaseRequestTotalCostChain() {
			Chain c = getDefaultChain();
			c.addCommand(new PurchaseRequestTotalCostRollUpCommand());
			return c;
		}
		
		public static Chain getPurchaseOrderTotalCostChain() {
			Chain c = getDefaultChain();
			c.addCommand(new PurchaseOrderTotalCostRollUpCommand());
			return c;
		}
		
		public static Chain getPurchaseOrderQuantityRecievedRollUpChain() {
			Chain c = getDefaultChain();
			c.addCommand(new PurchaseOrderQuantityRecievedRollUpCommand());
			return c;
		}
		
		public static Chain getPurchaseOrderLineItemQuantityRecievedRollUpChain() {
			Chain c = getDefaultChain();
			c.addCommand(new PurchaseOrderLineItemQuantityRollUpCommand());
			return c;
		}
		
		public static Chain getPurchaseOrderAutoCompleteChain() {
			Chain c = getDefaultChain();
			c.addCommand(new PurchaseOrderAutoCompleteCommand());
			c.addCommand(getAddOrUpdateItemTypeVendorChain());
			c.addCommand(getBulkAddToolChain());
			c.addCommand(getAddBulkItemChain());
			return c;
		}
		
		public static Chain getPurchaseOrderCompleteChain() {
			Chain c = getDefaultChain();
			c.addCommand(new PurchaseOrderCompleteCommand());
			c.addCommand(getAddOrUpdateItemTypeVendorChain());
			c.addCommand(getBulkAddToolChain());
			c.addCommand(getAddBulkItemChain());
			return c;
		}
		
		public static Chain getPendingPOLineItemsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new GetPendingPoLineItemsCommand());
			return c;
		}
		
		public static Chain getReceivedPOLineItemsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new GetReceivedPoLineItemsCommand());
			return c;
		}
		
		public static Chain getPOOnInventoryTypeIdChain() {
			Chain c = getDefaultChain();
			c.addCommand(new GetPurchaseOrdersListOnInventoryTypeIdCommand());
			return c;
		}


		public static Chain getAddOrUpdateItemTypeVendorChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddOrUpdateItemTypeVendorCommand());
			return c;
		}
		
		public static  Chain getImportItemChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ImportItemCommand());
			c.addCommand(getAddBulkItemChain());
			return c;
		}
		
		public static  Chain getImportToolChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ImportToolCommand());
			c.addCommand(getBulkAddToolChain());
			return c;
		}

		public static  Chain getAddFormCommand() {
			Chain c = getDefaultChain();
			c.addCommand(new AddFormCommand());
			return c;
		}

		public static Chain getUpdateFormChain() {
			Chain c = getDefaultChain();
			c.addCommand(new EditFormCommand());
			return c;
		}

		public static Chain getUpdateFormFieldChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateFormFieldCommand());
			return c;
		}

		public static Chain getUpdateFormFieldsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateFormFieldsCommand());
			return c;
		}

		public static Chain getUpdateFormSectionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateFormSectionCommand());
			return c;
		}

		public static Chain getDeleteFormChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteFormCommand());
			return c;
		}

		public static Chain getAddPurchaseContractChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseContract());
			chain.addCommand(new AddOrUpdatePurchaseContractCommand());
		    chain.addCommand(getPurchaseContractTotalCostChain()); //roll up for calculating total cost
			return chain;
		}

		public static Chain getPurchaseContractDeleteChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseContract());
			c.addCommand(new DeletePurchaseContractCommand());
			return c;
		}

		public static Chain getAddPurchaseContractLineItem() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseContractLineItem());
			c.addCommand(new AddOrUpdatePurchaseContractLineItemCommand());
			c.addCommand(getPurchaseContractTotalCostChain()); //roll up for calculating total cost
			return c;
		}

		public static Chain getActivePurchaseContractPrice() {
			Chain c = getDefaultChain();
			c.addCommand(new GetActiveContractPriceCommand());
			return c;
		}

		public static Chain getDeletePurchaseContractLineItemChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPurchaseContractLineItem());
			c.addCommand(new DeletePurchaseContractLineItemCommand());
			return c;
		}
		public static Chain getAddLabourContractChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForLabourContract());
			chain.addCommand(new AddOrUpdateLabourContractCommand());
			//rollup to update the cost per hour in the actual labour module
			chain.addCommand(new UpdateLabourCostRollUpCommand());
		    //rollup might be needed to update purchase contract total cost -- need to be discussed
			return chain;
		}

		public static Chain getLabourContractDeleteChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabourContract());
			c.addCommand(new DeleteLabourContractCommand());
			return c;
		}

		public static Chain getAddLabourContractLineItem() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabourContractLineItem());
			c.addCommand(new AddOrUpdateLabourContractLineItemCommand());
			//rollup might be needed to update contract total cost -- need to be discussed
			return c;
		}

		public static Chain getDeleteLabourContractLineItemChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForLabourContractLineItem());
			c.addCommand(new DeleteLabourContractLineItemCommand());
			return c;
		}
		public static Chain getUpdatePurchaseContractStatusChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPurchaseContract());
			chain.addCommand(new UpdateBulkPurchaseContractStatusCommand());
			return chain;
		}
		public static Chain getUpdateLabourContractStatusChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForLabourContract());
			chain.addCommand(new UpdateBulkLabourContractStatusCommand());
			return chain;
		}

		public static Chain getPurchaseContractTotalCostChain() {
			Chain c = getDefaultChain();
			c.addCommand(new PurchaseContractTotalCostRollupCommand());
			return c;
		}

		public static Chain getAddPoLineItemSerialNumbersChain () {
			Chain chain = getDefaultChain();
			chain.addCommand(new AddSerialNumberForPoLineItemsCommand());
			chain.addCommand(SetTableNamesCommand.getForPoLineItemSerialNumber());
			chain.addCommand(new GenericAddModuleDataListCommand());
			return chain;
		}

		public static Chain getUpdatePoLineItemSerialNumbersChain () {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForPoLineItemSerialNumber());
			chain.addCommand(new GenericUpdateModuleDataCommand());
			return chain;
		}
		public static Chain getDeletePoLineItemSerialNumbersChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForPoLineItemSerialNumber());
			c.addCommand(new GenericDeleteModuleDataCommand());
			return c;
		}

		public static Chain getAddGatePassChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGatePass());
			c.addCommand(new GenericAddModuleDataCommand());
			c.addCommand(getApproveRejectWorkorderToolsChain());
			c.addCommand(new AddGatePassLineItemsCommand());
			return c;
		}

		public static Chain getUpdateStateTransitionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new GenericGetModuleDataDetailCommand());
			c.addCommand(new UpdateStateCommand());
			c.addCommand(new UpdateFieldDataCommand());
			c.addCommand(new AddActivitiesCommand());
//			c.addCommand(new GenericUpdateModuleDataCommand());
			return c;
		}

		public static Chain getAvailableState() {
			Chain c = getDefaultChain();
			c.addCommand(new GenericGetModuleDataDetailCommand());
			c.addCommand(new GetAvailableStateCommand());
			return c;
		}

		public static Chain getAddOrUpdateStateFlowTransition() {
			Chain c = getDefaultChain();
			c.addCommand(new ConstructStateFlowTransitionCommand());
			c.addCommand(new AddOrUpdateStateTransitionCommand());
			return c;
		}

		public static Chain getAddOrUpdateStateFlow() {
			Chain c = getDefaultChain();
			c.addCommand(new AddOrUpdateStateFlowCommand());
			return c;
		}

		public static Chain getAddOrUpdateStateChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddOrUpdateStateCommand());
			return c;
		}

		public static Chain getAddConnectionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddConnectionCommand());
			return c;
		}

		public static Chain getUpdateConnectionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateConnectionCommand());
			return c;
		}

		public static Chain getDeleteConnectionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteConnectionCommand());
			return c;
		}

		public static Chain getAddTemplateToRules () {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrder());
			c.addCommand(new ConvertToRulesCommand());
			return c;
		}

		public static Chain getAddOrUpdateInventoryRequestChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
			chain.addCommand(new AddOrUpdateInventoryRequestCommand());
			return chain;
		}

		public static Chain getIssueInventoryRequestChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
			chain.addCommand(new AddOrUpdateInventoryRequestCommand());
			chain.addCommand(new LoadItemTransactionEntryInputCommand());
			chain.addCommand(getAddOrUpdateItemTransactionsChain());
			//rollups work with record_list object in the context
			chain.addCommand(new CopyToToolTransactionCommand());
			chain.addCommand(getAddOrUdpateToolTransactionsChain());
			return chain;

		}
		public static Chain getInventoryRequestDeleteChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryRequest());
			c.addCommand(new DeleteInventoryRequestCommand());
			return c;
		}

		public static Chain getAddInventoryRequestLineItem() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryRequest());
			c.addCommand(new AddOrUpdateInventoryRequestLineItemCommand());
			return c;
		}

		public static Chain getDeleteInventoryRequestLineItem() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForInventoryRequest());
			c.addCommand(new DeleteInventoryRequestLineItemCommand());
			return c;
		}

		public static Chain getUseLineItemsForItemsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UseInventoryRequestLineItemsCommand());
			c.addCommand(getAddOrUdpateWorkorderItemsChain());
			return c;
		}

		public static Chain getUseLineItemsForToolsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UseInventoryRequestLineItemsCommand());
			c.addCommand(getAddOrUdpateWorkorderToolsChain());
			return c;
		}



		public static Chain getAddOrUpdateGatePassChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGatePass());
			c.addCommand(new GetAddOrUpdateGatePassCommand());
			return c;
		}

		public static Chain getGatePassDeleteChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGatePass());
			c.addCommand(new DeleteGatePassCommand());
			return c;
		}

		public static Chain getAddJobPlanChain () {
			Chain c = getDefaultChain();
			c.addCommand(new AddJobPlanCommand());
			return c;
		}

		public static Chain getUpdateJobPlanChain () {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateJobPlanCommand());
			return c;
		}
		
		public static Chain getAddOrUpdateShipmentChain() {
			Chain chain = getDefaultChain();
			chain.addCommand(SetTableNamesCommand.getForShipment());
			chain.addCommand(new AddOrUpdateShipmentCommand());
			return chain;
		}
		public static Chain getDeleteShipmentChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipment());
			c.addCommand(new GetDeleteShipmentCommand());
			return c;
		}


		public static Chain getDeleteStateFlowTransition() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteStateFlowTransition());
			return c;
		}

		public static Chain getRearrangeStateFlows() {
			Chain c = getDefaultChain();
			c.addCommand(new ChangeTransitionExecutionOrderCommand());
			return c;
		}
		public static Chain getPMPlannerSettingsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdatePMPlannerSettingsCommand());
			return c;
		}
		public static Chain getAddShipmentLineItem() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipmentLineItems());
			c.addCommand(new AddOrUpdateShipmentLineItemCommand());
			return c;
		}

		public static Chain getDeleteShipmentLineItemChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipmentLineItems());
			c.addCommand(new DeleteShipmentLineItemCommand());
			return c;
		}
		
		public static Chain getReceiveShipmentChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipment());
			c.addCommand(new AddOrUpdateShipmentCommand());
			c.addCommand(getReceiveShipmentInventoryChain());
		    return c;
		}
		
		public static Chain getReceiveShipmentInventoryChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ReceiveShipmentCommand());
			c.addCommand(getBulkAddShipmentToolChain());
			c.addCommand(getAddBulkItemChain());
			c.addCommand(new AddShipmentRotatingAssetsCommand());
			return c;
		}
		public static Chain getStageShipmentChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShipment());
			c.addCommand(new AddOrUpdateShipmentCommand());
			c.addCommand(getStageShipmentInventoryChain());
			return c;
		}
		
		public static Chain getBulkAddShipmentToolChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTool());
			c.addCommand(new BulkToolAdditionCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new AddBulkToolStockTransactionsCommand());
			c.addCommand(new AddBulkRotatingToolShipmentTransactionCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(getSetItemAndToolTypeForStoreRoomChain());
			return c;
		}
		public static Chain getStageShipmentInventoryChain () {
			Chain c = getDefaultChain();
			c.addCommand(new StageShipmentCommand());
			c.addCommand(new LoadTransactionInputForShipmentCommand()); 
	        c.addCommand(getAddOrUpdateItemTransactionsChain()); 
	        //rollups work with record_list object in the context 
	        c.addCommand(new LoadToolTransactionsCommand()); 
	        c.addCommand(getAddOrUdpateToolTransactionsChain());
	        c.addCommand(new DeleteShipmentRotatingAssetCommand());
	        return c;
		    
		}
		
		public static Chain getTransferShipmentChain() {
			
			Chain c = getDefaultChain();
			c.addCommand(new UpdateShipmentRecordForDirecttransferCommand());
			c.addCommand(getStageShipmentInventoryChain());
		    c.addCommand(getReceiveShipmentInventoryChain());
		    return c;
		}


		public static Chain addShiftUserMappingChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddShiftUserMappingCommand());
			return c;
		}

		public static Chain getAddAttendanceTransactionChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForAttendanceTransaction());
			c.addCommand(new AddAttendanceCommand());
			c.addCommand(new GenericAddModuleDataCommand());
			return c;
		}
		public static Chain markAbsentChain() {
			Chain c = getDefaultChain();
			c.addCommand(new GetUsersForShiftCommand());
			c.addCommand(new MarkAsAbsentOrLeaveCommand());
			return c;
		}

		public static Chain addOrUpdateBreakChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddOrUpdateBreakCommand());
			return c;
		}

		public static Chain deleteBreakChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteBreakCommand());
			return c;
		}
		
		public static Chain getAddBreakTransactionChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForBreakTransaction());
			c.addCommand(new AddOrUpdateBreakTransactionCommand());
			return c;
		}
		
		public static Chain getAddNewAssetBreakdownChain() {
			Chain c = getDefaultChain();
			c.addCommand(new GetAssetDownTimeDetailsCommand());
			c.addCommand(new GetLastBreakDownFromCurrentSourceCommand());
			c.addCommand(new ValidateAssetBreakdownCommand());
			c.addCommand(new AddAssetBreakDownCommand());
			c.addCommand(new updateAssetDownTimeDetailsCommand());
			return c;
		}
		
		public static Chain getAttendanceTransitionState() {
			Chain c = getDefaultChain();
			c.addCommand(new ShowStateForAttendanceCommand());
			return c;
		}
		
		public static Chain getAddMVProjectChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddMVProjectCommand());
			c.addCommand(new AddMVBaselineCommand());
			c.addCommand(new AddMVAjustmentCommand());
			c.addCommand(new AddMVAjustmentVsBaselineCommand());
			c.addCommand(new ConstructBaselineFormulaWithAjustmentCommand());
			return c;
		}

		public static Chain getUpdateMVProjectChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateMVPojectCommand());
			c.addCommand(new UpdateMVBaselineCommand());
			c.addCommand(new UpdateMVAdjustmentCommand());
			c.addCommand(new DeleteMVAjustmentVsBaselineCommand());
			c.addCommand(new AddMVAjustmentVsBaselineCommand());
			c.addCommand(new ConstructBaselineFormulaWithAjustmentCommand());
			return c;
		}

		public static Chain getAddMVBaselineChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddMVBaselineCommand());
			return c;
		}

		public static Chain getAddMVAdjustmentChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddMVAjustmentCommand());
			return c;
		}

		public static Chain getAddMVAjustmentVsBaselineChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddMVAjustmentVsBaselineCommand());
			return c;
		}

		public static Chain getDeleteMVProjectChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteMVProjectCommand());
			return c;
		}

		public static Chain getAddWorkflowChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddWorkflowCommand());
			return c;
		}
		public static Chain getUpdateWorkflowChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateWorkflowCommand());
			return c;
		}
		public static Chain getDeleteWorkflowChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteWorkflowCommand());
			return c;
		}

		public static Chain getAddWorkflowNameSpaceChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddNameSpaceCommand());
			return c;
		}
		public static Chain getUpdateWorkflowNameSpaceChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateNameSpaceCommand());
			return c;
		}
		public static Chain getDeleteWorkflowNameSpaceChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteNameSpaceCommand());
			return c;
		}
		
		public static Chain getAddWorkflowUserFunctionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddWorkflowCommand());
			c.addCommand(new AddUserFunctionCommand());
			return c;
		}
		public static Chain getUpdateWorkflowUserFunctionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateWorkflowCommand());
			c.addCommand(new UpdateUserFunctionCommand());
			return c;
		}
		public static Chain getDeleteWorkflowUserFunctionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteWorkflowCommand());
			return c;
		}
		
		public static Chain getExecuteDefaultWorkflowChain() {
			Chain c = getDefaultChain();
			c.addCommand(new GetDefaultWorkflowContext());
			c.addCommand(new ExecuteWorkflowCommand());
			return c;
		}
		
		public static Chain getExecuteWorkflowChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ExecuteWorkflowCommand());
			return c;
		}
		
		public static Chain getAddGraphicsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGraphics());
			c.addCommand(new AddGraphicsCommand());
			return c;
		}
		
		public static Chain getDeleteGraphicsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGraphics());
			c.addCommand(new DeleteGraphicsCommand());
			return c;
		}
		
		public static Chain getUpdateGraphicsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForGraphics());
			c.addCommand(new UpdateGraphicsCommand());
			return c;
		}

		public static Chain getAddScheduledWorkflowChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddWorkflowCommand());
			c.addCommand(new AddScheduledWorkflowCommand());
			return c;
		}
		public static Chain getUpdateScheduledWorkflowChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateWorkflowCommand());
			c.addCommand(new updateScheduledWorkflowCommand());
			return c;
		}
		public static Chain getDeleteScheduledWorkflowChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteWorkflowCommand());
			c.addCommand(new DeleteScheduledWorkflowCommand());
			return c;
		}
		public static Chain getAddShiftRotationChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShiftRotation());
			c.addCommand(new AddOrUpdateShiftRotationCommand());
			return c;
		}

		public static Chain getUpdateShiftRotationChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShiftRotation());
			c.addCommand(new AddOrUpdateShiftRotationCommand());
			return c;
		}

		public static Chain getDeleteShiftRotationChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForShiftRotation());
			c.addCommand(new DeleteShiftRotationCommand());
			return c;
		}

		public static Chain getExecuteShiftRotationCommand() {
			Chain c = getDefaultChain();
			c.addCommand(new ExecuteShiftRotationCommand());
			return c;
		}

		public static Chain getAddOrUpdateTicketStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddOrUpdateFacilioStatusCommand());
			return c;
		}
		public static Chain getV2AddEventChain() {
			Chain c = getDefaultChain();
			c.addCommand(new InsertNewEventsCommand());
			c.addCommand(new NewEventsToAlarmsConversionCommand());
			c.addCommand(new SaveAlarmAndEventsCommand());
			return c;
		}

}


