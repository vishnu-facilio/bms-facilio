package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.actions.GetToolTransactionsListCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;

public class ReadOnlyChainFactory {
	private static Logger LOGGER = LogManager.getLogger(ReadOnlyChainFactory.class.getName());
	public static Chain fetchReportDataChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FilterFieldCommand());
		c.addCommand(new FetchReportDataCommand());
		c.addCommand(new TransformReportDataCommand());
		c.addCommand(new CustomTransformReportDataCommand());
		c.addCommand(new CalculateDerivationCommand());
		c.addCommand(new CalculateVarianceCommand());
		c.addCommand(new FetchReportExtraMeta());
		return c;
	}
	
	public static Chain fetchCardDataChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FetchCardDataCommand());
		return c;
	}
	
	public static Chain newFetchReportDataChain() {
		Chain c = getDefaultChain();
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
		return c;
	}
	
	public static Chain fetchReadingReportChain() {
		Chain c = getDefaultChain();
		c.addCommand(new CreateReadingAnalyticsReportCommand());
		c.addCommand(fetchReportDataChain());
		return c;
	}
	
	public static Chain newFetchReadingReportChain() {
		Chain c = getDefaultChain();
		c.addCommand(new CreateReadingAnalyticsReportCommand());
		c.addCommand(newFetchReportDataChain());
		return c;
	}
	
	public static Chain fetchWorkorderReportChain() {
		Chain c = getDefaultChain();
		c.addCommand(new CreateWorkOrderAnalyticsReportCommand());
		c.addCommand(fetchReportDataChain());
		return c;
	}
	
	public static Chain fetchModuleDataListChain() {
		Chain c = getDefaultChain();
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain fetchModuleDataDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static Chain fetchLatestReadingDataChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GetLatestReadingDataCommand());
		c.addCommand(new ConvertUnitForLatestReadingDataCommand());
		return c;
	}
	
	public static Chain fetchScheduledReportsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new ScheduledV2ReportListCommand());
		return c;
	}
	
	public static Chain getWorkOrderDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderCommand());
		c.addCommand(new GetTaskInputDataCommand());
		c.addCommand(new FetchApprovalRulesCommand());
		c.addCommand(new FetchSourceTypeDetailsForWorkorderCommand());
		return c;
	}
	
	public static Chain getWorkOrderListChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		//c.addCommand(new GenerateCondtionsFromFiltersCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetWorkOrderListCommand());
		c.addCommand(new FetchApprovalRulesCommand());
		return c;
	}
	
	public static Chain getCalendarWorkOrdersChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new ValidateCalendarWOCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GetCalendarWOsCommands());
//		c.addCommand(new GetFutureWOsCommands());
		return c;
	}
	
	public static Chain getWorkOrderStatusPercentageChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetDigestTeamTechnicianCountCommand());
		c.addCommand(new GetWorkOrderStatusPecentageCommand());
		
		return c;
	}
	
	public static Chain getAvgResponseResolutionTimeBySiteChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetAvgResponseResolutionBySiteCommand());
		
		return c;
	}
	
	public static Chain getWorkOrderCountBySiteChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetRoleWoCountBySiteCommand());
		
		return c;
	}
	
	public static Chain getTopNTechBySiteChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetTopNTechCountCommand());
		
		
		return c;
	}
	
	public static Chain getAvgCompletionTimeByCategoryChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new GetAvgCompletionTimeByCategoryCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchWorkflowRulesOfTypeChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GetWorkFlowOfRuleTypeCommand());
		return c;
	}
	
	public static Chain fetchWorkflowRules () {
		Chain c = getDefaultChain();
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetWorkFlowOfRuleTypeCommand());
		return c;
	}
	
	
	public static Chain fetchWorkflowRuleWithActionsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FetchWorkflowRuleCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		return c;
	}
	
	public static Chain fetchAlarmRuleWithActionsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FetchAlarmRuleCommand());
		c.addCommand(new GetActionListForAlarmRuleCommand());
//		c.addCommand(new FetchExtraMetaForAlarmRuleCommand());
		return c;
	}
	
	public static Chain fetchApprovalRuleWithActionsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FetchWorkflowRuleCommand());
		c.addCommand(new FetchChildRulesOfApprovalRuleCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		c.addCommand(new DeConstructApprovalRuleCommand());
		return c;
	}
	
	public static Chain getAlarmListChain() {
		Chain c = getDefaultChain();
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
	
	public static Chain getAlarmDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAlarm());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetAlarmCommand());
		c.addCommand(new GetReadingRuleNameCommand());
		c.addCommand(new GetReadingRuleDetailsCommand());
		return c;
	}
	
	public static Chain getAddOrUpdateReadingValuesChain() {
		Chain c = getDefaultChain();
		c.addCommand(new AddOrUpdateReadingsCommand());
		return c;
	}
	
	public static Chain executeWorkflowsForReadingChain() {
		Chain c = getDefaultChain();
		c.addCommand(new ExecuteAllWorkflowsCommand(false, RuleType.READING_RULE, RuleType.PM_READING_RULE, RuleType.VALIDATION_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE,RuleType.ALARM_RCA_RULES, RuleType.PM_READING_TRIGGER));
		return c;
	}
	
	public static Chain calculateFormulaChain() {
		Chain c = getDefaultChain();
		c.addCommand(new CalculatePostFormulaCommand());
		return c;
	}
	
	public static Chain getUnmodelledInstancesForController() {
		Chain c = getDefaultChain();
		c.addCommand(new GetUnmodelledInstancesForControllerCommand());
		c.addCommand(new GetLatestReadingDataCommand());
		return c;
	}
	
	public static Chain getResourcesListForMultiplePM() {
		Chain c = getDefaultChain();
		c.addCommand(new getResourceListForMultiplePM());
		return c;
	}
	
	public static Chain getPubSubPublishMessageChain() {
		Chain c = getDefaultChain();
		c.addCommand(new PubSubPublishMessageCommand());
		return c;
	}
	
	public static Chain getExportAnalyticsFileChain() {
		Chain c = getDefaultChain();
		c.addCommand(ReadOnlyChainFactory.fetchReadingReportChain());
		c.addCommand(new GetExportReportDataCommand());
		return c;
	}
	
	public static Chain getExportReportFileChain() {
		Chain c = getDefaultChain();
		c.addCommand(fetchReportDataChain());
		c.addCommand(new GetExportReportDataCommand());
		return c;
	}
	
	public static Chain sendAnalyticsMailChain() {
		Chain c = getDefaultChain();
		c.addCommand(getExportAnalyticsFileChain());
		c.addCommand(new SendReadingReportMailCommand());
		return c;
	}
	
	public static Chain sendReportMailChain() {
		Chain c = getDefaultChain();
		c.addCommand(getExportReportFileChain());
		c.addCommand(new SendReadingReportMailCommand());
		return c;
	}
	
	public static Chain getExportNewAnalyticsFileChain() {
		Chain c = getDefaultChain();
		c.addCommand(ReadOnlyChainFactory.newFetchReadingReportChain());
		c.addCommand(new GetExportReportFileCommand());
		return c;
	}
	
	public static Chain getExportNewReportFileChain() {
		Chain c = getDefaultChain();
		c.addCommand(newFetchReportDataChain());
		c.addCommand(new GetExportReportFileCommand());
		return c;
	}
	
	public static Chain sendNewAnalyticsMailChain() {
		Chain c = getDefaultChain();
		c.addCommand(getExportNewAnalyticsFileChain());
		c.addCommand(new SendReadingReportMailCommand());
		return c;
	}
	
	public static Chain fetchTenantDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTenants());
		c.addCommand(new GetTenantDetailCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain sendNewReportMailChain() {
		Chain c = getDefaultChain();
		c.addCommand(getExportNewReportFileChain());
		c.addCommand(new SendReadingReportMailCommand());
		return c;
	}
	
	public static Chain constructAndFetchReportDataChain() {
		Chain c = getDefaultChain();
		c.addCommand(new ReportSpecialHandlingCommand(true));
		c.addCommand(newFetchReportDataChain());
		c.addCommand(new ReportSpecialHandlingCommand(false));
		return c;
	}
	
	private static Chain getDefaultChain() {
		return FacilioChain.getNonTransactionChain();
    }
	
	public static Chain fetchInventoryDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventory());
		c.addCommand(new GetInventoryDetailsCommand());
//		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getInventoryListChain(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventory());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetInventoryListCommand());
		return c;
	}
	
	public static Chain fetchInventoryVendorDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventoryVendor());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static Chain getInventoryVendorsList(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventoryVendor());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain getWorkorderPartsList(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderParts());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkorderPartsListCommand());
		return c;
	}
	
	public static Chain getStoreRoomList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForStoreRoom());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetStoreRoomListCommand());
		return c;
	}
	
	public static Chain fetchStoreRoomDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForStoreRoom());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetStoreRoomDetailsCommand());
		return c;
	}
	
	public static Chain fetchItemTypesDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTypes());
		c.addCommand(new GetItemTypesDetailsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static Chain getItemTypessList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTypes());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetItemTypesListCommand());
		return c;
	}
	
	public static Chain fetchToolDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForToolTypes());
		c.addCommand(new GetItemTypesDetailsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
	
	public static Chain gettoolsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForToolTypes());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetToolsListCommand());
		return c;
	}
	
	public static Chain fetchVendorDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendors());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetVendorDetailsCommand());
		return c;
	}
	
	public static Chain getVendorsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForVendors());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetVendorsListCommand());
		return c;
	}
	
	public static Chain fetchItemDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItem());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetItemDetailsCommand());
		return c;
	}
	
	public static Chain getItemList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItem());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetItemListCommand());
		return c;
	}
	
	public static Chain fetchPurchasesItemDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedItem());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchPurchasedItemDetailsCommand());
		return c;
	}
	
	public static Chain getWorkorderItemsList(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderItems());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkorderItemsListCommand());
		return c;
	}
	
	public static Chain fetchStockedToolsDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTool());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetStockedToolsDetailsCommand());
		return c;
	}
	
	public static Chain getStockedToolsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForTool());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetStockedToolsListCommand());
		return c;
	}
	
	public static Chain getWorkorderToolsList(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderTools());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkorderToolsListCommand());
		return c;
	}
	
	public static Chain getWorkorderCostList(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkOrderCosts());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkorderCostListCommand());
		return c;
	}
	
	public static Chain getActivitiesChain() {
		Chain c = getDefaultChain();
		c.addCommand(new ConstructCriteriaAndSetModuleNameForActivity());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}

	public static Chain getItemVendorsList(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetItemVendorsList());
		return c;
	}
	
	public static Chain getItemTransactionsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTransactions());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetItemTransactionsListCommand());
		return c;
	}
	
	public static Chain getPurchasdItemsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedItem());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetPurchasedItemsListCommand());
		return c;
	}
	
	public static Chain GetItemTypesForVendorCommand(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForItemTypesVendors());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetItemTypesForVendorCommand());
		return c;
	}
	
	public static Chain getPurchasdToolsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedTool());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetPurchasedToolsListCommand());
		return c;
	}
	
	public static Chain getToolTransactionsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForToolTranaction());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetToolTransactionsListCommand());
		return c;
	}
	
	public static Chain getFormList()
	{
		Chain c=getDefaultChain();
		c.addCommand(new GetFormListCommand());
		return c;
	}
	
}
