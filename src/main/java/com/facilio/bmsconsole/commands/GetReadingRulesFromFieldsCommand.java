package com.facilio.bmsconsole.commands;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
