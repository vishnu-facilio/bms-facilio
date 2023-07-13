package com.facilio.bmsconsoleV3.context.survey;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SurveyUtil{

	public static List<SurveyResponseRuleContext> fetchChildRuleId(List<Long> ruleIds) throws Exception{

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(FieldFactory.getSurveyResponseRuleFields()).table(ModuleFactory.getSurveyResponseRuleModule().getTableName()).andCondition(CriteriaAPI.getCondition("PARENT_RULE_ID","surveyParentRuleId", StringUtils.join(ruleIds,","), NumberOperators.EQUALS));

		return FieldUtil.getAsBeanListFromMapList(builder.get(),SurveyResponseRuleContext.class);
	}

	public static void addSurveyRuleActions(WorkflowRuleContext workflowRule , List<ActionContext> executeOnCreateActions,List<ActionContext> executeOnSubmitActions) throws Exception{

		Long workFlowRuleId = workflowRule.getId();
		SurveyResponseRuleContext surveyResponseRule = new SurveyResponseRuleContext();
		surveyResponseRule.setSurveyParentRuleId(workFlowRuleId);
		surveyResponseRule.setModuleName(FacilioConstants.Survey.SURVEY_RESPONSE);
		surveyResponseRule.setRuleType(WorkflowRuleContext.RuleType.SURVEY_ACTION_RULE);


		if(workFlowRuleId > 0 && CollectionUtils.isNotEmpty(executeOnCreateActions)){

			surveyResponseRule.setActions(executeOnCreateActions);
			surveyResponseRule.setActivityType(EventType.CREATE);
			surveyResponseRule.setActionType(SurveyResponseRuleContext.SurveyActionType.ON_CREATE);
			surveyResponseRule.setName(workflowRule.getName()+"_survey_create_rule_"+workFlowRuleId);

			addRule(surveyResponseRule);
		}

		// OnSubmit rule for survey

		if(CollectionUtils.isNotEmpty(executeOnSubmitActions)) {
			surveyResponseRule.setActions(executeOnSubmitActions);
			surveyResponseRule.setActivityType(EventType.EDIT);
			surveyResponseRule.setActionType(SurveyResponseRuleContext.SurveyActionType.ON_SUBMIT);
			surveyResponseRule.setName(workflowRule.getName()+"_survey_submit_rule_"+workFlowRuleId);

			addRule(surveyResponseRule);
		}
	}

	public static void addRule(WorkflowRuleContext workflowRule) throws Exception{

		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		FacilioContext facilioContext = chain.getContext();
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
		chain.execute();
	}
}
