package com.facilio.bmsconsole.commands;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.agent.ConfigureAgentCommand;
import com.facilio.agent.ConfigureControllerCommand;
import com.facilio.agent.DeleteAgentCommand;
import com.facilio.bmsconsole.commands.data.PopulateImportProcessCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

public class TransactionChainFactory {

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
			c.setPostTransactionChain(getUpdateTicketNotesChain());
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
			c.addCommand(new AddTicketActivityCommand());
			c.addCommand(getAddNewTasksChain());
			return c;
		}

		public static Chain getWorkOrderWorkflowsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.BUSSINESS_LOGIC_WORKORDER_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_CUSTOM_CHANGE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
					.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE))
			);
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
			c.addCommand(new AddWorkOrderCommand());
			c.addCommand(new AddAttachmentCommand());
			c.addCommand(new AttachmentContextCommand());
			c.addCommand(new AddAttachmentRelationshipCommand());
			c.addCommand(new AddTicketActivityCommand());
			c.addCommand(getAddTasksChain());
			c.addCommand(getWorkOrderWorkflowsChain());
			return c;
		}

		public static Chain getAddNewTasksChain() {
			FacilioChain c = (FacilioChain) getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new ValidateNewTasksCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddTaskSectionsCommand());
			c.addCommand(new AddTasksCommand());
			c.addCommand(new AddTaskOptionsCommand());
			c.addCommand(new UpdateReadingDataMetaCommand());
			// c.addCommand(new AddTaskTicketActivityCommand());
			c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
			return c;
		}

		public static Chain getAddTasksChain() {
			FacilioChain c = (FacilioChain) getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new ValidateTasksCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddTaskSectionsCommand());
			c.addCommand(new AddTasksCommand());
			c.addCommand(new AddTaskOptionsCommand());
			c.addCommand(new UpdateReadingDataMetaCommand());
			// c.addCommand(new AddTaskTicketActivityCommand());
			c.setPostTransactionChain(TransactionChainFactory.getUpdateTaskCountChain());
			return c;
		}
		
		public static Chain getUpdateWorkOrderChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkOrder());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new FetchOldWorkOrdersCommand());
			c.addCommand(new VerifyApprovalCommand());
			c.addCommand(new UpdateWorkOrderCommand());
			c.addCommand(new SendNotificationCommand());
			c.addCommand(new AddTicketActivityCommand());
//			c.addCommand(getAddOrUpdateReadingValuesChain());
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.BUSSINESS_LOGIC_WORKORDER_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE))
				.addCommand(new ClearAlarmOnWOCloseCommand())
			);
			c.addCommand(new ConstructTicketNotesCommand());
			c.addCommand(getAddNotesChain());
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

		public static Chain editAgent(){
			Chain c = getDefaultChain();
			c.addCommand(new ConfigureAgentCommand());
			return c;
		}

		public static Chain deleteAgent(){
			Chain c = getDefaultChain();
			c.addCommand(new DeleteAgentCommand());
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
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(FacilioChainFactory.getCategoryReadingsChain());
			c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
			return c;
		}
		
		public static Chain getUpdateAssetChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddCategoryOnAssetUpdateCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(FacilioChainFactory.getCategoryReadingsChain());
			c.addCommand(new InsertReadingDataMetaForNewResourceCommand());
			return c;
		}
		
		public static Chain getImportChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ProcessImportCommand());
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
		
		public static Chain getAddAlarmChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForAlarm());
			c.addCommand(new AddAlarmCommand());
			c.addCommand(new AddMLOccurrenceCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.BUSSINESS_LOGIC_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.PM_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.READING_ALARM_RULE));
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
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.BUSSINESS_LOGIC_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.PM_ALARM_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.READING_ALARM_RULE));
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
			//c.addCommand(new SetValidationRulesContextCommand());
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
			c.addCommand(new DeltaCalculationCommand());
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
//			c.addCommand(getAddOrUpdateReadingValuesChain());
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
		
		public static Chain getChangePreventiveMaintenanceStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ChangePreventiveMaintenanceStatusCommand());
			c.addCommand(new DeletePMAndDependenciesCommand(false, true));
			c.addCommand(new AddPMTriggerCommand(true));
			c.addCommand(new AddPMReminderCommand(true));
			c.addCommand(new SetMissingRelInResourcePlannersCommand());
			c.addCommand(new AddPMRelFieldsCommand(true));
			c.addCommand(new SchedulePMCommand(true));
			c.addCommand(new scheduleBeforePMRemindersCommand(true));
			return c;
		}

		public static Chain getChangeNewPreventiveMaintenanceStatusChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ChangePreventiveMaintenanceStatusCommand());
			c.addCommand(new DeletePMAndDependenciesCommand(false, true));
			c.addCommand(new AddPMTriggerCommand(true));
			c.addCommand(new AddPMReminderCommand(true));
			c.addCommand(new SetMissingRelInResourcePlannersCommand());
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



	public static Chain getExecutePreventiveMaintenanceChain() {
			Chain c = getDefaultChain();
			
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ResetTriggersCommand())
				.addCommand(new SchedulePrePMRemindersCommand())
			);
			
			c.addCommand(new PreparePMForMultipleAsset());
			c.addCommand(new ExecutePMCommand());
			c.addCommand(new SchedulePostPMRemindersCommand());
			
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
		
	    private static Chain getDefaultChain() {
	    	return FacilioChain.getTransactionChain();
	    }
	    public static Chain getAddWidgetChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddWidgetCommand());
			return c;
		}
	    public static Chain getUpdateDashboardChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateDashboardCommand());
			c.addCommand(new EnableMobileDashboardCommand());
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
			c.addCommand(getUpdateWorkOrderChain());
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
			return c;
		}
		
		public static Chain getUpdateStoreRoomChain(){
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForStoreRoom());
			c.addCommand(new GenericUpdateModuleDataCommand());
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
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}
		
		public static Chain getDeleteWorkorderItemChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderItems());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteWorkorderItemCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			c.addCommand(getUpdateWorkOrderChain());
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
			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static Chain getAddOrUdpateWorkorderLabourChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderLabour());
			c.addCommand(new AddOrUpdateWorkorderLabourCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(getUpdateAvailabilityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			c.addCommand(getUpdateWorkOrderChain());
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
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteWorkorderToolCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static Chain getDeleteWorkorderLabourChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForWorkorderLabour());
			c.addCommand(new GenericDeleteModuleDataCommand());
			c.addCommand(new DeleteWorkorderLabourCommand());
			c.addCommand(getUpdateAvailabilityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			c.addCommand(getUpdateWorkOrderChain());
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
			c.addCommand(new ParseQRValueCommand());
			c.addCommand(new FetchAssetFromQRValCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new LoadAssetFields());
			c.addCommand(new GetAssetDetailCommand());
			c.addCommand(new UpdateGeoLocationCommand());
			c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.ASSET_ACTIVITY));
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

		public static Chain getApproveRejectItemsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForItemTransactions());
//			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new ApproveOrRejectItemCommand());
			c.addCommand(new PurchasedItemsQuantityRollUpCommand());
			c.addCommand(getUpdateItemQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			c.addCommand(getUpdateWorkOrderChain());
			return c;
		}

		public static Chain getApproveRejectToolsChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForToolTranaction());
//			c.addCommand(new GenericUpdateListModuleDataCommand());
			c.addCommand(new ApproveOrRejectToolCommand());
			c.addCommand(getUpdatetoolQuantityRollupChain());
			c.addCommand(new AddOrUpdateWorkorderCostCommand());
			c.addCommand(new UpdateWorkorderTotalCostCommand());
			c.addCommand(getUpdateWorkOrderChain());
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
}

