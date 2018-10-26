package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;

import com.facilio.bmsconsole.commands.FacilioChainFactory.FacilioChain;
import com.facilio.bmsconsole.commands.data.PopulateImportProcessCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;

public class TransactionChainFactory {

		public static Chain getAddNotesChain() {
			Chain c = getDefaultChain();
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddNotesCommand());
			c.addCommand(new ExecuteNoteWorkflowCommand());
			c.addCommand(new AddNoteTicketActivityCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
	
		public static Chain historicalFormulaCalculationChain() {
			Chain c = getDefaultChain();
			c.addCommand(new HistoricalFormulaCalculationCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain runThroughReadingRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new RunThroughReadingRulesCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getAddWorkOrderChain() {
			Chain c = getDefaultChain();
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
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.ASSIGNMENT_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.SLA_RULE));
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE))
			);
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getAddTasksChain() {
			Chain c = getDefaultChain();
			c.addCommand(SetTableNamesCommand.getForTask());
			c.addCommand(new ValidateTasksCommand());
			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new AddTaskSectionsCommand());
			c.addCommand(new AddTasksCommand());
			c.addCommand(new AddTaskOptionsCommand());
			c.addCommand(new UpdateReadingDataMetaCommand());
			// c.addCommand(new AddTaskTicketActivityCommand());
			CommonCommandUtil.addCleanUpCommand(c);
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
			c.addCommand(new ExecuteAllWorkflowsCommand(RuleType.APPROVAL_RULE, RuleType.REQUEST_APPROVAL_RULE, RuleType.REQUEST_REJECT_RULE));
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new ExecuteAllWorkflowsCommand(RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, RuleType.CUSTOM_WORKORDER_NOTIFICATION_RULE))
				.addCommand(new ClearAlarmOnWOCloseCommand())
			);
			c.addCommand(new ConstructTicketNotesCommand());
			c.addCommand(getAddNotesChain());
			CommonCommandUtil.addCleanUpCommand(c);
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
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		public static Chain getEditControllerChain() {
			Chain c = getDefaultChain();
			c.addCommand(new EditControllerSettingsCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain scheduleReportChain() {
			Chain c = new ChainBase();
			c.addCommand(new AddTemplateCommand());
			c.addCommand(new ScheduleV2ReportCommand());
			return c;
		}
		
		public static Chain updateScheduledReportsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddTemplateCommand());
			c.addCommand(new DeleteScheduledReportsCommand(true));
			c.addCommand(new ScheduleV2ReportCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain deleteScheduledReportsChain() {
			Chain c = getDefaultChain();
			c.addCommand(new DeleteScheduledReportsCommand(true));
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getAddWorkflowRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new AddWorkflowRuleCommand());
			c.addCommand(new AddActionsForWorkflowRule());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain updateWorkflowRuleChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateWorkflowRuleCommand());
			c.addCommand(new DeleterOldRuleActionsCommand());
			c.addCommand(new AddActionsForWorkflowRule());
			CommonCommandUtil.addCleanUpCommand(c);
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
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getUpdateAssetChain() {
			Chain c = getDefaultChain();
			c.addCommand(new SetModuleForSpecialAssetsCommand());
//			c.addCommand(SetTableNamesCommand.getForAsset());
			c.addCommand(new LoadAssetFields());
//			c.addCommand(new LoadAllFieldsCommand());
			c.addCommand(new GenericUpdateModuleDataCommand());
			c.addCommand(new ExecuteAllWorkflowsCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getImportChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ProcessImportCommand());
			c.addCommand(new PopulateImportProcessCommand());
			c.addCommand(new UpdateBaseAndResourceCommand());
			c.addCommand(new InsertReadingDataMetaForImport());
			c.addCommand(new SendEmailCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getBulkAssertImportChain() {
			Chain c = getDefaultChain();
			c.addCommand(new ProcessImportCommand());
			c.addCommand(new SeperateToCategoriesCommand());
			c.addCommand(new SetModuleForSpecialAssetsCommand());
			c.addCommand(new BulkPushAssetCommands());
			c.addCommand(new UpdateBaseAndResourceCommand());
			c.addCommand(new InsertReadingDataMetaForImport());
			c.addCommand(new SendEmailCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain scheduledRuleExecutionChain() {
			Chain c = getDefaultChain();
			c.addCommand(new FetchScheduledRuleMatchingRecordsCommand());
			c.addCommand(new ExecuteSingleWorkflowRuleCommand());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
		public static Chain controllerActivityAndWatcherChain() {
			Chain c = getDefaultChain();
			c.addCommand(new UpdateCheckPointAndAddControllerActivityCommand());
			c.addCommand(new CheckAndStartWatcherCommand());
			CommonCommandUtil.addCleanUpCommand(c);
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
			CommonCommandUtil.addCleanUpCommand(c);
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
			c.addCommand(new AddWOFromAlarmCommand());
			c.addCommand(new ForkChainToInstantJobCommand()
				.addCommand(new AddClearCommentInWoOnAlarmClearCommand())
			);
			c.addCommand(new ExecuteAllWorkflowsCommand());
			c.addCommand(new ConstructTicketNotesCommand());
			c.addCommand(getAddNotesChain());
			CommonCommandUtil.addCleanUpCommand(c);
			return c;
		}
		
	    private static Chain getDefaultChain() {
	    	return new FacilioChain(true);
	    }
}
