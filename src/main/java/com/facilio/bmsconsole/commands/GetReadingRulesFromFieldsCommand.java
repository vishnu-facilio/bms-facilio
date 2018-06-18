package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.util.WorkflowUtil;

public class GetReadingRulesFromFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> fieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.READING_FIELDS);
		RuleType ruleType = (RuleType) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE);
		if(ruleType == null) {
			ruleType = RuleType.READING_RULE;
		}
		if(fieldIds != null && !fieldIds.isEmpty()) {
			FacilioModule module = ModuleFactory.getReadingRuleModule();
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", StringUtils.join(fieldIds,","), NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(ruleType.getIntVal()), NumberOperators.EQUALS));
			
			List<ReadingRuleContext> readingRules = WorkflowRuleAPI.getReadingRules(criteria);
			if (readingRules != null && !readingRules.isEmpty()) {
				for (ReadingRuleContext r:  readingRules) {
					long workflowId = r.getWorkflowId();
					if (workflowId != -1) {
						r.setWorkflow(WorkflowUtil.getWorkflowContext(workflowId, true));
					}
				}
				Map<Long, ReadingRuleContext> rules = new HashMap<>();
				rules = readingRules.stream()
									.collect(Collectors
									.toMap(ReadingRuleContext::getReadingFieldId, Function.identity(), (rule1, rule2) -> rule1)
									);
				context.put(FacilioConstants.ContextNames.READING_RULE_LIST, rules);
				
				
				Map<Long, List<ReadingRuleContext>> fieldIdVsReadingRules = readingRules.stream()
											.collect(Collectors.groupingBy(ReadingRuleContext::getReadingFieldId, Collectors.toList()));
				context.put(FacilioConstants.ContextNames.READING_RULES_LIST, fieldIdVsReadingRules);
			}
		}
		return false;
	}

}
