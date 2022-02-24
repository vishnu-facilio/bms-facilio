package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.ActionRuleCondition;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Log4j
public class DeleteQandARuleActions extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);

		if (!type.isConditionBasedActions()) {
			return false;
		}
		long conditionId = getConditionId (context);
		List<ActionRuleCondition> actionRuleConditions = (List<ActionRuleCondition>) context.get(Constants.Command.CONDITIONS_TO_BE_DELETED);
		if (conditionId > 0L) {
			if (CollectionUtils.isNotEmpty(actionRuleConditions)) {
				ActionRuleCondition con = actionRuleConditions.stream ().filter (p -> conditionId == p.getId ()).findFirst ().orElse (null);
				if (con != null) {
					List<Map<String, Object>> ruleActionRel = QAndAUtil.fetchEvalRuleActionRel(conditionId);
					if (CollectionUtils.isNotEmpty(ruleActionRel)) {
						List<Long> actionIds = ruleActionRel.stream().map(p->(Long)p.get("actionId")).collect(Collectors.toList());
						if (CollectionUtils.isNotEmpty(actionIds)) {
							deleteEvalRuleAction(con.getRuleId(),actionIds,conditionId);
							ActionAPI.deleteActions(actionIds);
						}
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty (actionRuleConditions)){
			actionRuleConditions.removeIf (actionRuleCondition -> conditionId != actionRuleCondition.getId ());
		}
		LOGGER.info ("## Q and A rule action need to be delete list size : "+actionRuleConditions.size ());
		return false;
	}

	private long getConditionId (Context context) {

		List< QAndARule > rules = (List<QAndARule>) context.get(Constants.Command.RULES);
		if (CollectionUtils.isNotEmpty (rules)){
			QAndARule rule = rules.get (0);
			if (CollectionUtils.isNotEmpty (rule.getRuleConditions ())){
				ActionRuleCondition con = ( ActionRuleCondition ) rule.getRuleConditions ().get (0);
				if (con != null){
					return con.getId ();
				}
			}
		}
		return -1L;
	}

	private void deleteEvalRuleAction(long ruleId,List<Long> actionsIds,long conditionId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(Constants.ModuleFactory.evalRuleActionRelModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ACTION_ID","actionId", StringUtils.join(actionsIds,","), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("Q_AND_A_RULE_CONDITION_ID","conditionId", String.valueOf(conditionId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RULE_ID","ruleId", String.valueOf(ruleId), NumberOperators.EQUALS));
		builder.delete();
	}
}
