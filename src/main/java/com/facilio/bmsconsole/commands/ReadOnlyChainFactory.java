package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.actions.GetToolTransactionsListCommand;
import com.facilio.bmsconsole.commands.anomaly.FormatAnomalyMetricsCommand;
import com.facilio.bmsconsole.commands.anomaly.GetAnomalyDeviationAndWastageCommand;
import com.facilio.bmsconsole.commands.anomaly.GetAnomalyMetricsCommand;
import com.facilio.bmsconsole.commands.anomaly.GetEnergyByCDDCommand;
import com.facilio.bmsconsole.commands.reservation.FetchAttendeesCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.commands.GetControllableCategoryFromSpaceCommand;
import com.facilio.controlaction.commands.GetControllableCategoryFromSpaceListCommand;
import com.facilio.controlaction.commands.GetIncludedSpaceListCommand;
import com.facilio.mv.command.FetchMVWidgetResultCommand;
import com.facilio.workflows.command.GetAllNameSpaceWithFunctionCommand;
import com.facilio.workflows.command.GetAllScheduledWorkflowCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;

public class ReadOnlyChainFactory {
	private static Logger LOGGER = LogManager.getLogger(ReadOnlyChainFactory.class.getName());
	public static FacilioChain fetchReportDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FilterFieldCommand());
		c.addCommand(new FetchReportDataCommand());
		c.addCommand(new TransformReportDataCommand());
		c.addCommand(new CustomTransformReportDataCommand());
		c.addCommand(new CalculateDerivationCommand());
		c.addCommand(new CalculateVarianceCommand());
		c.addCommand(new FetchReportExtraMeta());
		return c;
	}

	public static FacilioChain getBasespaceWithHierarchy(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetBasespaceWithHierarchyCommand());
		return c;
	}
	
	public static FacilioChain getSubModuleRelChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetSubModulesRelModuleAndFields());
		return c;
	}

	public static FacilioChain getRelationshipChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetRelationshipCommand());
		return c;
	}
	
	public static FacilioChain getResetCounterMetaChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetResetCounterMetaCommand());
		return c;
	}
	public static FacilioChain getLastAssetBreakDownChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAssetDownTimeDetailsCommand());
		c.addCommand(new GetAssetBreakdownCommand());
		return c;
	}

	public static FacilioChain getBusinessHoursChain () {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetBusinessHourCommand());
		return c;
	}
	public static FacilioChain fetchCardDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchCardDataCommand());
		return c;
	}
	
	
	public static FacilioChain fetchRegressionReportChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new CreateReadingAnalyticsReportCommand());
		c.addCommand(newFetchReportDataChain());
		return c;
	}
	
	
	public static FacilioChain newFetchReportDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FilterFieldCommand());
		c.addCommand(new FetchReportDataCommand());
		c.addCommand(new ConstructReportDataCommand());
		c.addCommand(new CalculateAggregationCommand());
//		c.addCommand(new CustomTransformReportDataCommand()); //Uncomment after changing interface to new format
		c.addCommand(new NewTransformReportDataCommand());
		c.addCommand(new CalculateAggregationCommand()); //For new ones created in Derivations
		c.addCommand(new FetchReportAdditionalInfoCommand());
		c.addCommand(new FetchResourcesCommand());
		c.addCommand(new HandleGroupByDataCommand());
		c.addCommand(new AddRegressionPointsCommand());
		c.addCommand(new FormatHeatMapDataCommand());
		c.addCommand(new GetTrendLineCommand());
		c.addCommand(new FormatForTimeDuration());
		c.addCommand(new FetchCriteriaReportCommand());
		return c;
	}
	
	public static FacilioChain fetchReadingReportChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new CreateReadingAnalyticsReportCommand());
		c.addCommand(fetchReportDataChain());
		return c;
	}
	
	public static FacilioChain newFetchReadingReportChain() {
		FacilioChain c = getDefaultChain();
//		c.addCommand(new SetLatestDateRange());
		c.addCommand(new CreateReadingAnalyticsReportCommand());
		c.addCommand(newFetchReportDataChain());
		return c;
	}
	
	public static FacilioChain fetchWorkorderReportChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new CreateWorkOrderAnalyticsReportCommand());
		c.addCommand(fetchReportDataChain());
		return c;
	}
	
	public static FacilioChain fetchModuleDataListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateCriteriaFromClientCriteriaCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new AddLookupFieldMetaList());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new SpecialHandlingToGetModuleDataListCommand());
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
		return c;
	}
	
	public static FacilioChain fetchModuleDataDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddLookupFieldMetaList());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
		return c;
	}
	
	public static FacilioChain fetchLatestReadingDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetLatestReadingDataCommand());
		c.addCommand(new ConvertUnitForLatestReadingDataCommand());
		return c;
	}
	
	public static FacilioChain getAllAssetReadingsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAllAssetReadingsCommand());
		return c;
	}
	
	public static FacilioChain fetchScheduledReportsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ScheduledV2ReportListCommand());
		return c;
	}
	
	public static FacilioChain getWorkOrderDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderCommand());
		c.addCommand(new GetWorkorderHazardsCommand());
		c.addCommand(new GetAvailableStateCommand());
		c.addCommand(new GetTaskInputDataCommand());
		c.addCommand(new FetchApprovalRulesCommand());
		c.addCommand(new FetchSourceTypeDetailsForWorkorderCommand());
		c.addCommand(new FetchStateflowAndSlaCommand());
		c.addCommand(new FacilioCommand() {
			@Override
			public boolean executeCommand(Context context) throws Exception {
				Object o = context.get(FacilioConstants.ContextNames.WORK_ORDER);
				if (o != null) {
					context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(o));
				}
				return false;
			}
		});
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
		return c;
	}
	
	public static FacilioChain getWorkOrderListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new GenerateCondtionsFromFiltersCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new AddVendorScopeForPortalCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetWorkOrderListCommand());
		c.addCommand(new FetchApprovalRulesCommand());
		c.addCommand(new FacilioCommand() {
			@Override
			public boolean executeCommand(Context context) throws Exception {
				context.put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
				return false;
			}
		});
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
		return c;
	}

	public static FacilioChain getNextWorkOrder() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetNextWorkOrder());
		return c;
	}

	
	public static FacilioChain getCalendarWorkOrdersChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new ValidateCalendarWOCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GetCalendarWOsCommands());
//		c.addCommand(new GetFutureWOsCommands());
		return c;
	}
	
	public static FacilioChain getCalendarResourceJobChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new LoadPMPlannerSettingCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetPMCalendarResouceJobsCommand());
		return c;

	}

	public static FacilioChain getWorkOrderStatusPercentageChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetDigestTeamTechnicianCountCommand());
		c.addCommand(new GetWorkOrderStatusPecentageCommand());
		
		return c;
	}
	
	public static FacilioChain getAvgResponseResolutionTimeBySiteChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetAvgResponseResolutionBySiteCommand());
		
		return c;
	}
	
	public static FacilioChain getWorkOrderCountBySiteChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetRoleWoCountBySiteCommand());
		
		return c;
	}
	
	public static FacilioChain getTopNTechBySiteChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetTopNTechCountCommand());
		
		
		return c;
	}
	
	public static FacilioChain getAvgCompletionTimeByCategoryChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetAvgCompletionTimeByCategoryCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static FacilioChain fetchWorkflowRulesOfTypeChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetWorkFlowOfRuleTypeCommand());
		return c;
	}
	
	public static FacilioChain fetchWorkflowRules () {
		FacilioChain c = getDefaultChain();
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetWorkFlowOfRuleTypeCommand());
		return c;
	}
	
	
	public static FacilioChain fetchWorkflowRuleWithActionsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchWorkflowRuleCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		return c;
	}
	
	public static FacilioChain fetchAlarmRuleWithActionsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchAlarmRuleCommand());
		c.addCommand(new GetActionListForAlarmRuleCommand());
		c.addCommand(new FetchExtraMetaForAlarmRuleCommand());
		return c;
	}

	public static FacilioChain fetchRuleSummaryChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchRuleSummaryCommand());
		return c;
	}

	public static FacilioChain getFetchRuleActionChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchRuleActionCommand());
		return c;
	}
	
	public static FacilioChain fetchApprovalRuleWithActionsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchWorkflowRuleCommand());
		c.addCommand(new FetchChildRulesOfApprovalRuleCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		c.addCommand(new DeConstructApprovalRuleCommand());
		return c;
	}
	
	public static FacilioChain getAlarmListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetAlarmListCommand());
		c.addCommand(new GetReadingRuleNameCommand());
		return c;
	}
	
	public static FacilioChain getRDMChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetRDMCommand());
		return c;
	}
	
	public static FacilioChain getChatBotConversationChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetChatBotConversationCommandCommand());
		return c;
	}
	
	public static FacilioChain getAlarmDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAlarmCommand());
		c.addCommand(new GetReadingRuleNameCommand());
		c.addCommand(new GetReadingRuleDetailsCommand());
		return c;
	}
	
	public static FacilioChain getV2AlarmListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(fetchModuleDataListChain());
		c.addCommand(new HandleV2AlarmListLookupCommand());
		return c;
	}
	
	public static FacilioChain getV2AlarmDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetV2AlarmsCommand());
		return c;
	}
	public static FacilioChain getV2OccurrenceListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(fetchModuleDataListChain());
		c.addCommand(new HandleOccurrenceListLookupCommand());
		return c;
	}
	
	public static FacilioChain getV2EventListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());

		return c;
	}
	
	public static FacilioChain getWorkflowRuleLoggersCommand() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetWorkflowRuleLoggersCommand());
		return c;
	}
	
	public static FacilioChain getWorkflowRuleResourceLoggersCommand() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetWorkflowRuleResourceLoggersCommand());
		return c;
	}
	
	public static FacilioChain getWorkflowRuleHistoricalLogsCommand() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetWorkflowRuleHistoricalLogsCommand());
		return c;
	}
	
	public static FacilioChain getAddOrUpdateReadingValuesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new AddOrUpdateReadingsCommand());
		return c;
	}
	
	public static FacilioChain executeWorkflowsForReadingChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ExecuteAllWorkflowsCommand(false, RuleType.READING_RULE, RuleType.PM_READING_RULE, RuleType.VALIDATION_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE,RuleType.ALARM_RCA_RULES, RuleType.PM_READING_TRIGGER,RuleType.REPORT_DOWNTIME_RULE, RuleType.READING_VIOLATION_RULE));
		return c;
	}
	
	public static FacilioChain calculateFormulaChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new CalculatePostFormulaCommand());
		return c;
	}
	
	public static FacilioChain getControllerListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetControllerListCommand());
		return c;
	}
	
	public static FacilioChain getUnmodelledInstancesForController() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetUnmodelledInstancesForControllerCommand());
		c.addCommand(new GetLatestReadingDataCommand());
		return c;
	}
	
	public static FacilioChain getResourcesListForMultiplePM() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetResourceListForMultiplePM());
		return c;
	}
	
	public static FacilioChain getPubSubPublishMessageChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new PubSubPublishMessageCommand());
		return c;
	}


	public static FacilioChain fetchTenantDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTenants());
		c.addCommand(new AssociateTenantToContactCommand());
		c.addCommand(new GetTenantDetailCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	

	
	public static FacilioChain constructAndFetchReportDataChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ReportSpecialHandlingCommand(true));
		c.addCommand(newFetchReportDataChain());
		c.addCommand(new ReportSpecialHandlingCommand(false));
		return c;
	}

	public static FacilioChain getPageChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new PageRecordHandlingCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetPageCommand());
		return c;
	}

	public static FacilioChain getSpecialModulePageChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetSpecialModuleDataDetailCommand());
		c.addCommand(new GetPageCommand());
		return c;
	}
	
	private static FacilioChain getDefaultChain() {
		return FacilioChain.getNonTransactionChain();
    }
	
	public static FacilioChain fetchInventoryDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventory());
		c.addCommand(new GetInventoryDetailsCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static FacilioChain getInventoryListChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventory());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetInventoryListCommand());
		return c;
	}
	
	public static FacilioChain fetchInventoryVendorDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventoryVendor());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static FacilioChain getInventoryVendorsList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventoryVendor());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static FacilioChain getWorkorderPartsList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderParts());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkorderPartsListCommand());
		return c;
	}
	
	public static FacilioChain getStoreRoomList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForStoreRoom());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetStoreRoomListCommand());
		return c;
	}
	
	public static FacilioChain fetchStoreRoomDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForStoreRoom());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetStoreRoomDetailsCommand());
		return c;
	}
	
	public static FacilioChain fetchItemTypesDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTypes());
		c.addCommand(new GetItemTypesDetailsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static FacilioChain getItemTypessList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTypes());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new GetItemTypesListCommand());
		return c;
	}
	
	public static FacilioChain fetchToolDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForToolTypes());
		c.addCommand(new GetItemTypesDetailsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static FacilioChain gettoolsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForToolTypes());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new GetToolTypesListCommand());
		return c;
	}
	
	public static FacilioChain fetchVendorDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendors());
		c.addCommand(new LoadVendorLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetVendorDetailsCommand());
		return c;
	}
	
	public static FacilioChain getVendorsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendors());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new LoadVendorLookUpCommand());
		c.addCommand(new GetVendorsListCommand());
		c.addCommand(new GetStateflowsForModuleDataListCommand());
		return c;
	}
	
	public static FacilioChain fetchItemDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItem());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetItemDetailsCommand());
		return c;
	}
	
	public static FacilioChain getItemList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItem());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetItemListCommand());
		return c;
	}
	
	public static FacilioChain fetchPurchasesItemDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedItem());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchPurchasedItemDetailsCommand());
		return c;
	}
	
	public static FacilioChain getWorkorderItemsList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderItems());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkorderItemsListCommand());
		return c;
	}
	
	public static FacilioChain fetchStockedToolsDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTool());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetStockedToolsDetailsCommand());
		return c;
	}
	
	public static FacilioChain getStockedToolsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTool());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetToolsListCommand());
		return c;
	}
	
	public static FacilioChain getWorkorderToolsList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderTools());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkorderToolsListCommand());
		return c;
	}
	
	public static FacilioChain getWorkorderLabourList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderLabour());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderLabourListCommand());
		return c;
	}
	
	public static FacilioChain getWorkorderCostList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkorderCostListCommand());
		return c;
	}
	
	public static FacilioChain getActivitiesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ConstructCriteriaAndSetModuleNameForActivity());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}

	public static FacilioChain getItemVendorsList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetItemVendorsList());
		return c;
	}
	
	public static FacilioChain getItemTransactionsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTransactions());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetItemTransactionsListCommand());
		return c;
	}
	
	public static FacilioChain getPurchasdItemsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedItem());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetPurchasedItemsListCommand());
		return c;
	}
	
	public static FacilioChain getPurchasedItemsViewsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedItem());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetPurchasedItemsViewsListCommand());
		return c;
	}
	
	public static FacilioChain getGatePassList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForGatePass());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetGatePassListCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new SetGatePassLineItemsCommand());
		return c;
	}
	
	public static FacilioChain GetItemTypesForVendorCommand(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetItemTypesForVendorCommand());
		return c;
	}
	
	public static FacilioChain getPurchasdToolsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedTool());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetPurchasedToolsListCommand());
		return c;
	}
	
	public static FacilioChain getToolTransactionsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForToolTranaction());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetToolTransactionsListCommand());
		return c;
	}
	
	public static FacilioChain getUnusedPurchasdToolsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedTool());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetUnusedPurchasedToolsList());
		return c;
	}
	
	public static FacilioChain getUnusedPurchasdItemsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedItem());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetUnusedPurchasedItemsList());
		return c;
	}
	public static FacilioChain getLabourList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForLabour());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetLabourListCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static FacilioChain fetchLabourDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForLabour());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetLabourDetailsCommand());
		return c;
	}
	
	public static FacilioChain getFormList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetFormListCommand());
		return c;
	}

	public static FacilioChain getPurchaseRequestListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForPurchaseRequest());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetPurchaseRequestListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}

	public static FacilioChain getPurchaseRequestDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchaseRequest());
		c.addCommand(new PurchaseRequestPurchaseOrderLookUpsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchPurchaseRequestDetailsCommand());
		return c;
	}

	public static FacilioChain getPurchaseOrderListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForPurchaseOrder());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetPurchaseOrderListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new SetPurchaseOrderLineItemsCommand());
		return chain;
	}

	public static FacilioChain getPurchaseOrderDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchaseOrder());
		c.addCommand(new PurchaseRequestPurchaseOrderLookUpsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchPurchaseOrderDetailsCommand());
		return c;
	}

	public static FacilioChain getAllReceiptsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForReceipt());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetReceiptsListCommand());
		return c;
	}

	public static FacilioChain getAllReceivablesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForReceivables());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetReceivablesListCommand());
		return c;
	}
	public static FacilioChain getContractListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForContract());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetContractListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}

	
	public static FacilioChain getPurchaseContractListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForPurchaseContract());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetContractListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new LookupPrimaryFieldHandlingCommand());
	
		
		return chain;
	}

	public static FacilioChain getPurchaseContractDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchaseContract());
		c.addCommand(new LoadContractLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchPurchaseContractDetailsCommand());
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
	
		return c;
	}
	public static FacilioChain getLabourContractListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForLabourContract());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetContractListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new LookupPrimaryFieldHandlingCommand());
	
		return chain;
	}
	
	public static FacilioChain getLabourContractDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForLabourContract());
		c.addCommand(new LoadContractLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchLabourContractDetailsCommand());
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
	
		return c;
	}

	public static FacilioChain getPoLineItemsSerialNumberList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPoLineItemSerialNumber());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetPoLineItemSerialNumbersCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}

	public static FacilioChain getAssetModuleReportCardChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ConstructAssetReportCardsCommand());
		c.addCommand(new GetModuleReportCardsCommand());
		return c;
	}

	public static FacilioChain getAssetDowntimeMetricsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchAssetDowntimeMetricsCommand());
		return c;
	}


	public static FacilioChain getAlarmInsightChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchAlarmInsightCommand());
		return c;
	}

	public static FacilioChain fetchGatePassDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForGatePass());
		c.addCommand(new LoadGatePassDetailLookupCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetGatePassDetailsCommand());
		return c;
	}
	
	public static FacilioChain fetchConnectedAppDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForConnectedApps());
		c.addCommand(new GetConnectedAppDetailsCommand());
		return c;
	}
	
	public static FacilioChain getConnectedAppsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForConnectedApps());
		c.addCommand(new GetConnectedAppsListCommand());
		return c;
	}

	public static FacilioChain getStateFlowList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetStateFlowListCommand());
		return c;
	}

	public static FacilioChain getStateTransitionList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetStateTransitionListCommand());
		return c;
	}

	public static FacilioChain viewStateFlow() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetStateFlowCommand());
		return c;
	}

	public static FacilioChain viewStateTransition() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetStateTransitionCommand());
		return c;
	}

	public static FacilioChain getModuleList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetModuleListCommand());
		return c;
	}

	public static FacilioChain getModulesList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetModulesListCommand());
		return c;
	}

	public static FacilioChain getInventoryRequestListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetInventoryRequestListCommand());
		return chain;
	}

	public static FacilioChain getInventoryRequestDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventoryRequest());
		c.addCommand(new LoadInventoryRequestLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchInventoryRequestDetailsCommand());
		return c;
	}

	public static FacilioChain getInventoryRequestLineItemListByRequesterIdChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
		chain.addCommand(new GetInventoryRequestLineItemsByRequesterIdCommand());
		return chain;
	}

	public static FacilioChain getInventoryRequestLineItemListByParentIdChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
		chain.addCommand(new GetInventoryRequestLineItemsForParentIdCommand());
		return chain;
	}

	public static FacilioChain getInventoryRequestLineItemListByStoreRoomIdChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
		chain.addCommand(new GetInventoryRequestLineItemsForStoreRoomIdCommand());
		return chain;
	}

	public static FacilioChain getAssetForTypeAndStoreChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForAsset());
		chain.addCommand(new GetAssetForTypeAndStoreCommand());
		return chain;
	}
	
	public static FacilioChain getAssetForTypeChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForAsset());
		chain.addCommand(new GetAssetForTypeCommand());
		return chain;
	}
	public static FacilioChain getShiftUserMappingChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetShiftUserMappingCommand());
		return c;
	}

	public static FacilioChain getAttendanceList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAttendance());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new LoadAttendanceLookUpCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}

	public static FacilioChain getAttendanceTransactionsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAttendanceTransaction());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	public static FacilioChain getAllBreakChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAllBreaksCommand());
		return c;
	}

	public static FacilioChain getBreakList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForBreak());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetBreakListCommand());
		return c;
	}

	public static FacilioChain getShiftList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForShift());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetShiftListCommand());
		return c;
	}

	public static FacilioChain getBreakChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetBreakCommand());
		return c;
	}

	public static FacilioChain getServiceListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForServices());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetServicesListCommand());
		return chain;
	}


	public static FacilioChain getPMPlannerSettingschain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new LoadPMPlannerSettingCommand());
		return c;
	}

	public static FacilioChain getShipmentListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForShipment());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetShipmentListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}

	public static FacilioChain getShipmentDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForShipment());
		c.addCommand(new LoadShipmentLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchShipmentDetailsCommand());
		return c;
	}

	public static FacilioChain getAttendanceDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAttendance());
		c.addCommand(new LoadAttendanceLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}

	public static FacilioChain getServiceDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForServices());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetServiceVendorListCommand());

		return c;
	}

	public static FacilioChain getWarrantyContractListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForWarrantyContract());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetContractListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new LookupPrimaryFieldHandlingCommand());
	
		return chain;
	}

	public static FacilioChain getWarrantyContractDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWarrantyContract());
		c.addCommand(new LoadContractLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchWarrantyContractDetailsCommand());
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
	
		return c;
	}

	public static FacilioChain getWorkorderServiceList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderService());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadWorkOrderServiceLookUpCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}

	public static FacilioChain getReceivedPoLineItemList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchaseOrderLineItem());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAvailableReceivedPoLineItemsCommand());
		return c;
	}

	public static FacilioChain getRentalLeaseContractListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForRentalLeaseContract());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetContractListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new LookupPrimaryFieldHandlingCommand());
		
		return chain;
	}

	public static FacilioChain getRentalLeaseContractDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForRentalLeaseContract());
		c.addCommand(new LoadContractLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchRentalLeaseContractDetailsCommand());
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
		
		return c;
	}


	public static FacilioChain GetToolTypesForVendorCommand(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForToolTypeVendor());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetToolTypesForVendorCommand());
		return c;
	}

	public static FacilioChain getToolVendorsList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForToolTypeVendor());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetToolVendorsListCommand());
		return c;
	}

	public static FacilioChain fetchTermsAndConditionsDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTermsAndConditions());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}

	public static FacilioChain getTermsAndConditionsList(){
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTermsAndConditions());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}


	public static FacilioChain getAllWorkflowNameSpaceChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAllNameSpaceWithFunctionCommand());
		return c;
	}
	
	public static FacilioChain getAllScheduledWorkflowChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAllScheduledWorkflowCommand());
		return c;
	}
	
	public static FacilioChain getGraphicsListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForGraphics());
		c.addCommand(new GetGraphicsListCommand());
		return c;
	}
	
	public static FacilioChain getGraphicsDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForGraphics());
		c.addCommand(new GetGraphicsDetailCommand());
		return c;
	}
	
	public static FacilioChain getGraphicsFolderListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForGraphicsFolder());
		c.addCommand(new GetGraphicsFolderListCommand());
		return c;
	}

	public static FacilioChain getGraphicsForAssetCategoryChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForGraphics());
		c.addCommand(new GetGraphicsForAssetCategoryCommand());
		return c;
	}
	public static FacilioChain getShiftRotationList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForShiftRotation());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new GetShiftRotationListCommand());
		return c;
	}
	
	public static FacilioChain getShiftRotationDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForShiftRotation());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
//		c.addCommand(new GenerateCriteriaFromFilterCommand());
//		c.addCommand(new GenerateSearchConditionCommand());
//		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetShiftRotationDetailCommand());
		return c;
	}
	
	public static FacilioChain getBreakTransactionsList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForBreakTransaction());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new LoadBreakTransactionLookupCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
		
	}
	
	public static FacilioChain viewRecordRule() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetRecordRuleCommand());
		return c;
	}
	
	public static FacilioChain getRecordSpecificRuleList() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetRecordSpecificRuleListCommand());
		return c;
	}
	
	public static FacilioChain fetchWorkflowRulesForStoreChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetRulesForStoreCommand());
		return c;
	}

	public static FacilioChain getAllAlarmOccurrenceListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAlarmOccurrence());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		c.addCommand(new FetchAlarmFromOccurrenceCommand());
		return c;
	}

	public static FacilioChain getAlarmOccurrenceDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAlarmOccurrenceDetailsCommand());
		return c;
	}

	public static FacilioChain getAlarmOccurrenceListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAlarmOccurrenceListCommand());
		return c;
	}

	public static FacilioChain getEventListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetV2EventListCommand());
		return c;
	}

	public static FacilioChain getV2EventDetailChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetV2EventDetailCommand());
		return c;
	}

	public static FacilioChain fetchMVWidgetResultChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchMVWidgetResultCommand());
		return c;
	}
	
	public static FacilioChain getUpdateNewPreventiveMaintenanceJobChain() {
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new UpdateNewPreventiveMaintenanceJobCommand());
		return c;
	}

	public static FacilioChain getModuleDetails() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetModuleDetailCommand());
		return c;
	}

	public static FacilioChain getRecommendedUsers() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetRecommendedUsersCommand());
		return c;
	}

	public static FacilioChain getControlActionCommandsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetControlActionCommandsCommand());
		return c;
	}
	
	public static FacilioChain getControlActionRulesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetControlActionRulesCommand());
		return c;
	}

	public static FacilioChain getControllableAssetsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetControllableAssetsCommand());
		return c;
	}

	public static FacilioChain getControllableFieldsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetControllableFieldsCommand());
		return c;
	}

	public static FacilioChain fetchReservationDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(fetchModuleDataDetailsChain());
		c.addCommand(new FetchAttendeesCommand());
		return c;
	}
	
	public static FacilioChain getAssetMovementListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForAssetMovement());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new AddLookupFetchForModuleStateCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new GetStateflowsForModuleDataListCommand());
		return chain;
	}

	public static FacilioChain fetchControlGroupsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new FetchControlGroupsCommand());
		return c;
	}

	public static FacilioChain fetchMLSummaryDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetMLSummmaryDetail());
		return c;
	}
	public static FacilioChain fetchRcaAnomaly() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetMLSummmaryDetail());
		return c;
	}
	public static FacilioChain fetchRelatedAssetAlarms () {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetRelatedAssetCommand());
		c.addCommand(new FetchAlarmInsightCommand());
		return c;
	}
	
	public static FacilioChain fetchRelatedAssetChain () {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetRelatedAssetCommand());
		return c;
	}

	
	public static FacilioChain getCustomModuleWorkflowRulesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetCustomModuleWorkflowRulesCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		return c;
	}
	
	public static FacilioChain fetchAnomalyMetricsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAnomalyMetricsCommand());
		c.addCommand(new GetAnomalyDeviationAndWastageCommand());
		c.addCommand(new GetEnergyByCDDCommand());
		c.addCommand(new FormatAnomalyMetricsCommand());
		return c; 
	}
	public static FacilioChain getAssetAssociatedActiveContractsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetAssetAssociatedActiveContractsCommand());
		return c;
	}
	
	public static FacilioChain performConditionalFormattings() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new PerformConditionalFormatting());
		return c;
		}

	public static FacilioChain getImportHistoryListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForImportProcess());
		c.addCommand(new GetImportHistoryListCommand());
		return c;
	}

	public static FacilioChain getSiteAlarmList() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetSiteLevelAlarmCountCommand());
		return chain;
	}

	public static FacilioChain getAssetAlarmList() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAssetAlarmDetailsCommand());
		return chain;
	}
	public static FacilioChain getVisitorListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForVisitor());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadVisitorLookUpCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getVisitorDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVisitor());
		c.addCommand(new LoadVisitorLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}

	public static FacilioChain getSearchVisitorByPhotoChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVisitor());
		c.addCommand(new SearchVisitorByPhotoCommand());
		return c;
	}

	public static FacilioChain getVisitorInvitesListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForVisitorInvites());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new AddVendorScopeForPortalCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getVisitorInvitesDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVisitorInvites());
		c.addCommand(new LoadVisitorInviteLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetInviteesListCommand());
		
		return c;
	}
	
	public static FacilioChain getVisitorLoggingListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForVisitorLogging());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
//		chain.addCommand(new AddVisitorLogStatesCommand());
		chain.addCommand(new AddVendorScopeForPortalCommand());
		//chain.addCommand(new AddTenantScopeForVisitorLoggingListCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadVisitorLoggingLookUpCommand());
		chain.addCommand(new AddTenantFilterForVisitsListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new GetStateflowsForModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getVisitorLoggingDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVisitorLogging());
		c.addCommand(new GetTriggerForRecurringLogCommand());
		
		return c;
	}
	
	public static FacilioChain qrScanVisitorChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetVisitorAndInviteForQrScanCommand());
		return c;
	}
	
	public static FacilioChain qrScanVisitorLogChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new QrScanVisitorLogCommand());
		return c;
	}

	public static FacilioChain passCodeScanVisitorLogChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetVisitorDetailAndLogCommand());
		return c;
	}

	public static FacilioChain getVisitorInviteRelContextChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetVisitorInviteRelContextCommand());
		return c;
	}

	public static FacilioChain getKPIViewListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GetKPIViewListCommand());
		chain.addCommand(new SetCurrentValueForKPICommand());
		chain.addCommand(new KPIListGroupingCommand());
		return chain;
	}

	public static FacilioChain getAllFormulasOfTypeChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GetAllFormulasOfTypeCommand());
		chain.addCommand(new GetReadingFieldsCommand());
		return chain;
	}

	public static FacilioChain getAllCustomButtonChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetCustomButtonListCommand());
		chain.addCommand(new GetActionListForWorkflowRulesCommand());
		return chain;
	}

	public static FacilioChain getAllServiceCatalogGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAllServiceCatalogGroupCommand());
		return chain;
	}

	public static FacilioChain getServiceCatalogListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAllServiceCatalogCommand());
		return chain;
	}

	public static FacilioChain getServiceCatalogDetailsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetServiceCatalogDetailsCommand());
		return chain;
	}

	public static FacilioChain getKPIOccurrencesChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetKPIOccurrencesCommand());
		return chain;
	}
	public static FacilioChain getFetchVisitorTypeSettingChain()
	{
		FacilioChain c=getDefaultChain();
		c.addCommand(new FetchVisitorTypeSettingsCommand());
		return c;
	}
	
	public static FacilioChain getContactsListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForContacts());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadContactsLookUpCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getContactDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForContacts());
		c.addCommand(new LoadContactsLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		
		return c;
	}
	
	public static FacilioChain getInsuranceListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForInsurance());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new AddVendorScopeForPortalCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadInsuranceLookUpCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getInsuranceDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInsurance());
		c.addCommand(new LoadInsuranceLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		
		return c;
	}
	
	public static FacilioChain getWatchListRecordsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForWatchList());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getWatchListDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWatchList());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetLogsForWatchListCommand());
		
		return c;
	}
	
	public static FacilioChain getWorkPermitListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForWorkPermit());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new AddVendorScopeForPortalCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadWorkPermitLookUpsCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new GetStateflowsForModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getWorkPermitDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkPermit());
		c.addCommand(new LoadWorkPermitLookUpsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new LoadWorkPermitRecurringInfoCommand());
		
		return c;
	}

	public static FacilioChain getServiceCatalogGroupDetailChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetServiceCatalogGroupDetailsCommand());
		return chain;
	}

	public static FacilioChain getAutomationModules() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAutomationModulesCommand());
		return chain;
	}
	public static FacilioChain fetchVisitorTypePicklistData() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVisitorType());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}

	public static FacilioChain getSubFormModulesChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetSubFormModulesCommand());
		return chain;
	}

	public static FacilioChain getStateTransitionDetailsFromPermalink() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetPermalinkDetailsCommand());
		chain.addCommand(new GetStateTransitionIdFromSessionCommand());
		chain.addCommand(new GetStateTransitionCommand());
		chain.addCommand(new VerifyTransitionAccessFromPermalinkCommand());
		return chain;
	}

	public static FacilioChain getVisitorDetailAndLogForPhoneNumberChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetVisitorDetailAndLogCommand());
		return c;
	}

	public static FacilioChain getServiceCatalogModuleList() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetServiceCatalogModulesCommand());
		return chain;
	}

	public static FacilioChain getV3FilePreview() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new V3FilePreviewCommad());
		return chain;
	}
	public static FacilioChain getVisitorKioskConfigDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchVisitorKioskConfigDetailsCommand());
		return c;
	}
	public static FacilioChain getVisitorKioskListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new VisitorKioskListCommand());
		return c;
	}

	
	public static FacilioChain getPrintersListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetPrintersListCommand());
		return c;
	}

	public static FacilioChain getOccupantsListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForOccupants());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadOccupantLookUpCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}

	public static FacilioChain getDocumentListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetDocumentsListCommand());
		return chain;
	}


	public static FacilioChain getOccupantDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForOccupants());
		c.addCommand(new LoadOccupantLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());

		return c;
	}

	public static FacilioChain getAllApplicationChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAllApplicationCommand());
		return chain;
	}

	public static FacilioChain getApplicationDetails() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetApplicationDetails());
		return chain;
	}

	public static FacilioChain getAllWebTabGroupChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAllWebTabGroupCommand());
		return chain;
	}

	public static FacilioChain getAllTabChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAllWebTabCommand());
		return chain;
	}

	public static FacilioChain getTabPermissionChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetTabPermissionsCommand());
		return chain;
	}
	
	public static FacilioChain getTabTypePermissionChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetTabTypePermissionCommand());
		return chain;
	}

	public static FacilioChain getServiceRequestsListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForServiceRequests());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadServiceRequestLookupCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new GetStateflowsForModuleDataListCommand());
		return chain;
	}

	public static FacilioChain getServcieRequestDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForServiceRequests());
		c.addCommand(new LoadServiceRequestLookupCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());

		return c;
	}
	
	public static FacilioChain getServiceRequestSubModulesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForServiceRequests());
		c.addCommand(new GetServiceRequestSubModules());
		
		return c;
	}

	public static FacilioChain copySpecificAssetReadingToAnotherOrgChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new copyAssetReadingCommand());
		return chain;
	}

	public static FacilioChain getKPIMetricsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetKPIMetricsCommand());
		return c;
	}

	public static FacilioChain getKPIListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GetKPIListCommand());
		return c;
	}

	public static FacilioChain getAllSLAChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAllSLACommand());
		return chain;
	}

	public static FacilioChain getSLAChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetSLACommand());
		return chain;
	}

	public static FacilioChain getAllSLAEntityChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetAllSLAEntityCommand());
		return chain;
	}

	public static FacilioChain getSLAPolicyEscalationsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetSLAPolicyEscalationsCommand());
		return chain;
	}
	
	public static FacilioChain getSafetyPlanListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForSafetyPlan());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getHazardListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForHazard());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getPrecautionListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForPrecaution());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getSafetyPlanDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForSafetyPlan());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetSafetyPlanHazardsCommand());
		return c;
	}
	
	public static FacilioChain getHazardDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForHazard());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		
		return c;
	}
	
	public static FacilioChain getPrecautionDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPrecaution());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		
		return c;
	}

	public static FacilioChain getSafetyPlanHazardListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForSafetyPlanHazards());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadSafetyPlanHazardLookUpsCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getHazardPrecautionListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForHazardPrecaution());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadHazardPrecautionLookUpsCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getWorkorderHazardListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForWorkorderHazard());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadWorkorderHazardLookUpCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getAssetHazardListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForAssetHazard());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadAssetHazardLookUpsCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getClientDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForClient());
		c.addCommand(new LoadClientLookupCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static FacilioChain getClientsListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForClient());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new LoadClientLookupCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	
	}

	public static FacilioChain getPeopleListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForPeople());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getVendorContactListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForVendorContact());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new AddVendorContactLookUpCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}

	public static FacilioChain getFeedbackTypeListChain() {
		FacilioChain chain = getDefaultChain();		
		chain.addCommand(new FeedbackTypeListCommand());
		return chain;
	}
	
	public static FacilioChain getFeedbackKioskListChain() {
		FacilioChain chain = getDefaultChain();		
		chain.addCommand(new FeedbackKioskListCommand());
		return chain;
	}
	public static FacilioChain getSmartControlKioskListChain() {
		FacilioChain chain = getDefaultChain();		
		chain.addCommand(new SmartControlKioskListCommand());
		return chain;
	}
	
	public static FacilioChain getFeedbackKioskDetailsChain() {
		FacilioChain chain = getDefaultChain();		
		chain.addCommand(new FeedbackKioskDetailsCommand());
		return chain;
	}
	public static FacilioChain getSmartControlKioskDetailsChain() {
		FacilioChain chain = getDefaultChain();		
		chain.addCommand(new SmartControlKioskDetailsCommand());
		return chain;
	}

	public static FacilioChain getSLAPolicyWithChildrenChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetSLAPolicyWithChildrenCommand());
		return chain;
	}
	
	
	public static FacilioChain getControllableCategoryFromSpaceIdChain() {
		FacilioChain chain = getDefaultChain();		
		chain.addCommand(new GetControllableCategoryFromSpaceCommand());
		return chain;
	}
	
	public static FacilioChain getControllableCategoryChain() {
		FacilioChain chain = getDefaultChain();		
		chain.addCommand(new GetIncludedSpaceListCommand());
		chain.addCommand(new GetControllableCategoryFromSpaceListCommand());
		return chain;
	}

	public static FacilioChain getSiteTotalAreaChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetSiteTotalAreaCommand());
		return chain;
	}

	public static FacilioChain getSpaceModuleListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new LoadSpaceManagementLookupFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}

	public  static  FacilioChain getFieldUsageLog () {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FieldUsageCommand());
		return c;
	}
	
	public static FacilioChain getCommissioningLogsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetCommissioningLogsCommand());
		return chain;
	}
	
	public static FacilioChain getCommissioningDetailsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetCommissioningDetailsCommand());
		return chain;
	}
	
	public static FacilioChain getFloorPlanSpacesChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetFloorPlanSpacesCommand());
		return c;
	}

	public static FacilioChain getApprovalRuleDetailsChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetApprovalRuleCommand());
		return chain;
	}

	public static FacilioChain getApprovalDetails() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetApprovalDetailsCommand());
		chain.addCommand(new GetApprovalRuleCommand());
		chain.addCommand(new GetApproversListCommand());
		return chain;
	}
	public static FacilioChain getTenantContactListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForTenantContact());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new AddTenantContactLookUpCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		
		return chain;
	}
	
	public static FacilioChain getEmployeeListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForEmployee());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
//		chain.addCommand(new AddClientContactLookUpCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	
	public static FacilioChain getClientContactListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForClientContact());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new AddClientContactLookUpCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		return chain;
	}
	
	public static FacilioChain getPeopleDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPeople());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static FacilioChain getVendorContactDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendorContact());
		c.addCommand(new AddVendorContactLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static FacilioChain getTenantContactDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTenantContact());
		c.addCommand(new AddTenantContactLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new LookupPrimaryFieldHandlingCommand());
		return c;
	}
	
	public static FacilioChain getEmployeeDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForEmployee());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static FacilioChain getClientContactDetailsChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForClientContact());
		c.addCommand(new AddClientContactLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}


	public static FacilioChain getApprovalModuleDataListChain() {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetApprovalModuleDataListCommand());
		chain.addCommand(new GenericGetModuleDataListCommand());
		chain.addCommand(new GetStateflowsForModuleDataListCommand());
		return chain;
	}

	public static FacilioChain getSpaceDirectChildrenChain () {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetSpaceDirectChildrenCommand());
		return chain;
	}
	
	public static FacilioChain getApplicationUsersChain () {
		FacilioChain chain = getDefaultChain();
		chain.addCommand(new GetApplicationUsersCommand());
		return chain;
	}
}
