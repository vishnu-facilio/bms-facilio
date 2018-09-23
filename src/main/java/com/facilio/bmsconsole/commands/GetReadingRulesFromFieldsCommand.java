package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;

public class GetReadingRulesFromFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> fieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.READING_FIELDS);
		
		if(fieldIds != null && !fieldIds.isEmpty()) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", StringUtils.join(fieldIds,","), NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.READING_RULE.getIntVal()), NumberOperators.EQUALS));
			
			List<ReadingRuleContext> readingRules = ReadingRuleAPI.getReadingRules(criteria);
			if (readingRules != null && !readingRules.isEmpty()) {
				
				Map<Long, ReadingRuleContext> rules = new HashMap<>();
				rules = readingRules.stream()
									.collect(Collectors
									.toMap(ReadingRuleContext::getReadingFieldId, Function.identity(), (rule1, rule2) -> rule1)
									);
				context.put(FacilioConstants.ContextNames.READING_RULE_LIST, rules);
			}
		}
		return false;
	}

}
