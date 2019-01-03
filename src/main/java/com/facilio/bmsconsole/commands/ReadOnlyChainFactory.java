package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioChainFactory.FacilioChain;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;

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
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchModuleDataDetailsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GenericGetModuleDataDetailCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchLatestReadingDataChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GetLatestReadingDataCommand());
		return c;
	}
	
	public static Chain fetchScheduledReportsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new ScheduledV2ReportListCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getWorkOrderDetailsChain() {
		Chain c = new ChainBase();
		c.addCommand(SetTableNamesCommand.getForWorkOrder());
		c.addCommand(new LoadModuleNameCommand());
		c.addCommand(new LoadAllFieldsCommand());
		c.addCommand(new GetWorkOrderCommand());
		c.addCommand(new GetTaskInputDataCommand());
		c.addCommand(new FetchApprovalRulesCommand());
		c.addCommand(new FetchSourceTypeDetailsForWorkorderCommand());
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAssetFromQRChain() {
		Chain c = getDefaultChain();
		c.addCommand(new ParseQRValueCommand());
		c.addCommand(new FetchAssetFromQRValCommand());
		c.addCommand(new SetModuleForSpecialAssetsCommand());
		c.addCommand(new LoadAssetFields());
		c.addCommand(new GetAssetDetailCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchWorkflowRulesOfTypeChain() {
		Chain c = getDefaultChain();
		c.addCommand(new GetWorkFlowOfRuleTypeCommand());
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	
	public static Chain fetchWorkflowRuleWithActionsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FetchWorkflowRuleCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchAlarmRuleWithActionsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FetchAlarmRuleCommand());
		c.addCommand(new GetActionListForAlarmRuleCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain fetchApprovalRuleWithActionsChain() {
		Chain c = getDefaultChain();
		c.addCommand(new FetchWorkflowRuleCommand());
		c.addCommand(new FetchChildRulesOfApprovalRuleCommand());
		c.addCommand(new GetActionListForWorkflowRulesCommand());
		c.addCommand(new DeConstructApprovalRuleCommand());
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
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
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getAddOrUpdateReadingValuesChain() {
		Chain c = new ChainBase();
		c.addCommand(new AddOrUpdateReadingsCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain executeWorkflowsForReadingChain() {
		Chain c = new ChainBase();
		c.addCommand(new ExecuteAllWorkflowsCommand(false, RuleType.READING_RULE, RuleType.PM_READING_RULE, RuleType.VALIDATION_RULE));
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain calculateFormulaChain() {
		Chain c = new ChainBase();
		c.addCommand(new CalculatePostFormulaCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getUnmodelledInstancesForController() {
		Chain c = new ChainBase();
		c.addCommand(new GetUnmodelledInstancesForControllerCommand());
		c.addCommand(new GetLatestReadingDataCommand());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getResourcesListForMultiplePM() {
		Chain c = new ChainBase();
		c.addCommand(new getResourceListForMultiplePM());
		CommonCommandUtil.addCleanUpCommand(c);
		return c;
	}
	
	private static Chain getDefaultChain() {
		return new FacilioChain(false);
    }
}
