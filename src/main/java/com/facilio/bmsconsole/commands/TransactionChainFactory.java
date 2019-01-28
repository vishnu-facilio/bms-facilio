package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.data.PopulateImportProcessCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;

public class TransactionChainFactory {
	
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
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_CUSTOM_CHANGE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.CHILD_APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE))
			);
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
			return c;
		}
		
		public static Chain updateAlarmRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateAlarmRuleCommand());
			c.addCommand(new DeleteOldAlarmRuleActionsCommand());
			c.addCommand(new AddActionForAlarmRuleCommand());
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
			//c.addCommand(SetTableNamesCommand.getForAsset());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
//			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new LoadAssetFields());
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
//			c.addCommand(SetTableNamesCommand.getForAsset());
			c.addCommand(new LoadAssetFields());
//			c.addCommand(new LoadAllFieldsCommand());
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
			c.addCommand(new ExecuteAllWorkflowsCommand());
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
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
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
		
		private static void commonAddModuleChain(Chain c) {
			c.addCommand(new AddModulesCommand());
			c.addCommand(new SetColumnNameForNewCFsCommand());
			c.addCommand(new AddFieldsCommand());
		}
		
		public static Chain getAddCategoryReadingChain() {
			Chain c = getDefaultChain();
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
}
