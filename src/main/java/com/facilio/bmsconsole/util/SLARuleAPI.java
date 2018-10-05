package com.facilio.bmsconsole.util;

import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.rule.SLARuleContext;

public class SLARuleAPI extends WorkflowRuleAPI {
	public static SLARuleContext updateSLARuleWithChildren(SLARuleContext rule) throws Exception {
		SLARuleContext oldRule = (SLARuleContext) getWorkflowRule(rule.getId());
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
