package com.facilio.bmsconsole.util;

import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.SLARuleContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class SLARuleAPI extends WorkflowRuleAPI {
	public static SLARuleContext updateSLARuleWithChildren(SLARuleContext rule, SLARuleContext oldRule) throws Exception {
		updateWorkflowRuleChildIds(rule);
		updateExtendedRule(rule, ModuleFactory.getSLARuleModule(), FieldFactory.getSLARuleFields());
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		return rule;
	}
	
	protected static SLARuleContext constructSLARuleFromProps(Map<String, Object> prop, ModuleBean modBean) throws Exception {
		SLARuleContext slaRule = FieldUtil.getAsBeanFromMap(prop, SLARuleContext.class);
		slaRule.setResource(ResourceAPI.getResource(slaRule.getResourceId()));
		return slaRule;
	}
}
