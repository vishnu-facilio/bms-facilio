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

	public static Chain getLastAssetBreakDownChain() {
		Chain c = getDefaultChain();
		c.addCommand(new getAssetDownTimeDetailsCommand());
		c.addCommand(new getAssetBreakdownCommand());
		return c;
	}

	public static Chain getBusinessHoursChain () {
		Chain c = getDefaultChain();
		c.addCommand(new GetBusinessHourCommand());
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
		c.addCommand(new FetchExtraMetaForAlarmRuleCommand());
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
		c.addCommand(new GetResourceListForMultiplePM());
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
	
	public static Chain getExportNewModuleReportFileChain() {
		Chain c = getDefaultChain();
		c.addCommand(newFetchReportDataChain());
		c.addCommand(new GetExportModuleReportFileCommand());
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

	public static Chain getPageChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetPageCommand());
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
		c.addCommand(new GetToolTypesListCommand());
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
		c.addCommand(new LoadViewCommand());
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
		c.addCommand(new LoadViewCommand());
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
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetToolsListCommand());
		return c;
	}
	
	public static Chain getWorkorderToolsList(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderTools());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkorderToolsListCommand());
		return c;
	}
	
	public static Chain getWorkorderLabourList(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForWorkorderLabour());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderLabourListCommand());
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
	
	public static Chain getPurchasedItemsViewsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedItem());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetPurchasedItemsViewsListCommand());
		return c;
	}
	
	public static Chain getGatePassList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForGatePass());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetGatePassListCommand());
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
	
	public static Chain getUnusedPurchasdToolsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedTool());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetUnusedPurchasedToolsList());
		return c;
	}
	
	public static Chain getUnusedPurchasdItemsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchasedItem());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetUnusedPurchasedItemsList());
		return c;
	}
	public static Chain getLabourList(){
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForLabour());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetLabourListCommand());
		return c;
	}
	
	public static Chain fetchLabourDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForLabour());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetLabourDetailsCommand());
		return c;
	}
	
	public static Chain getFormList()
	{
		Chain c=getDefaultChain();
		c.addCommand(new GetFormListCommand());
		return c;
	}

	public static Chain getPurchaseRequestListChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForPurchaseRequest());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetPurchaseRequestListCommand());
		return chain;
	}

	public static Chain getPurchaseRequestDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchaseRequest());
		c.addCommand(new PurchaseRequestPurchaseOrderLookUpsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchPurchaseRequestDetailsCommand());
		return c;
	}

	public static Chain getPurchaseOrderListChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForPurchaseOrder());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetPurchaseOrderListCommand());
		return chain;
	}

	public static Chain getPurchaseOrderDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchaseOrder());
		c.addCommand(new PurchaseRequestPurchaseOrderLookUpsCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchPurchaseOrderDetailsCommand());
		return c;
	}

	public static Chain getAllReceiptsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForReceipt());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetReceiptsListCommand());
		return c;
	}

	public static Chain getAllReceivablesChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForReceivables());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetReceivablesListCommand());
		return c;
	}
	
	public static Chain getPurchaseContractListChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForPurchaseContract());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetPurchaseContractListCommand());
		return chain;
	}

	public static Chain getPurchaseContractDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPurchaseContract());
		c.addCommand(new LoadContractLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchPurchaseContractDetailsCommand());
		return c;
	}
	public static Chain getLabourContractListChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForLabourContract());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetLabourContractListCommand());
		return chain;
	}
	
	public static Chain getLabourContractDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForLabourContract());
		c.addCommand(new LoadContractLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchLabourContractDetailsCommand());
		return c;
	}

	public static Chain getPoLineItemsSerialNumberList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForPoLineItemSerialNumber());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GetPoLineItemSerialNumbersCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}

	public static Chain getAssetModuleReportCardChain() {
		Chain c = getDefaultChain();
		c.addCommand(new ConstructAssetReportCardsCommand());
		c.addCommand(new GetModuleReportCardsCommand());
		return c;
	}

	public static Chain getAssetDowntimeMetricsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FetchAssetDowntimeMetricsCommand());
		return c;
	}


	public static Chain getAlarmInsightChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FetchAlarmInsightCommand());
		return c;
	}

	public static Chain fetchGatePassDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForGatePass());
		c.addCommand(new LoadGatePassDetailLookupCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new GetGatePassDetailsCommand());
		return c;
	}
	
	public static Chain fetchConnectedAppDetails() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForConnectedApps());
		c.addCommand(new GetConnectedAppDetailsCommand());
		return c;
	}
	
	public static Chain getConnectedAppsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForConnectedApps());
		c.addCommand(new GetConnectedAppsListCommand());
		return c;
	}

	public static Chain getStateFlowList() {
		Chain c = getDefaultChain();
		c.addCommand(new GetStateFlowListCommand());
		return c;
	}

	public static Chain getStateTransitionList() {
		Chain c = getDefaultChain();
		c.addCommand(new GetStateTransitionListCommand());
		return c;
	}

	public static Chain viewStateFlow() {
		Chain c = getDefaultChain();
		c.addCommand(new GetStateFlowCommand());
		return c;
	}

	public static Chain viewStateTransition() {
		Chain c = getDefaultChain();
		c.addCommand(new GetStateTransitionCommand());
		return c;
	}

	public static Chain getInventoryRequestListChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetInventoryRequestListCommand());
		return chain;
	}

	public static Chain getInventoryRequestDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForInventoryRequest());
		c.addCommand(new LoadInventoryRequestLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchInventoryRequestDetailsCommand());
		return c;
	}

	public static Chain getInventoryRequestLineItemListByRequesterIdChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
		chain.addCommand(new GetInventoryRequestLineItemsByRequesterIdCommand());
		return chain;
	}

	public static Chain getInventoryRequestLineItemListByParentIdChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
		chain.addCommand(new GetInventoryRequestLineItemsForParentIdCommand());
		return chain;
	}

	public static Chain getInventoryRequestLineItemListByStoreRoomIdChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForInventoryRequest());
		chain.addCommand(new GetInventoryRequestLineItemsForStoreRoomIdCommand());
		return chain;
	}

	public static Chain getAssetForTypeAndStoreChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForAsset());
		chain.addCommand(new GetAssetForTypeAndStoreCommand());
		return chain;
	}
	public static Chain getShiftUserMappingChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GetShiftUserMappingCommand());
		return c;
	}
	
	public static Chain getAttendanceList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAttendance());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain getAttendanceTransactionsList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAttendanceTransaction());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	public static Chain getAllBreakChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GetAllBreaksCommand());
		return c;
	}
	
	public static Chain getBreakList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForBreak());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain getShiftList() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForShift());
		c.addCommand(new LoadViewCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GenerateCriteriaFromFilterCommand());
		c.addCommand(new GenerateSearchConditionCommand());
		c.addCommand(new GenericGetModuleDataListCommand());
		return c;
	}
	
	public static Chain getBreakChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GetBreakCommand());
		return c;
	}


	public static Chain getPMPlannerSettingschain() {
		Chain c = getDefaultChain();
		c.addCommand(new LoadPMPlannerSettingCommand());
		return c;
	}

	public static Chain getShipmentListChain() {
		Chain chain = getDefaultChain();
		chain.addCommand(SetTableNamesCommand.getForShipment());
		chain.addCommand(new LoadAllFieldsCommand());
		chain.addCommand(new LoadViewCommand());
		chain.addCommand(new GenerateCriteriaFromFilterCommand());
		chain.addCommand(new GenerateSearchConditionCommand());
		chain.addCommand(new GetShipmentListCommand());
		return chain;
	}

	public static Chain getShipmentDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForShipment());
		c.addCommand(new LoadShipmentLookUpCommand());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		c.addCommand(new FetchShipmentDetailsCommand());
		return c;
	}
	
	public static Chain getAttendanceDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(SetTableNamesCommand.getForAttendance());
		c.addCommand(new GenericGetModuleDataDetailCommand());
		return c;
	}
}
