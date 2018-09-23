package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.view.SLARuleContext;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class SLARuleAPI extends WorkflowRuleAPI {
	public static SLARuleContext updateSLARuleWithChildren(SLARuleContext rule) throws Exception {
		SLARuleContext oldRule = (SLARuleContext) getWorkflowRule(rule.getId());
		updateWorkflowRuleChildIds(rule);
		updateSLARule(rule, rule.getId());
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		return rule;
	}
	
	public static int updateSLARule(SLARuleContext slaRule, long ruleId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getSLARuleFields());
		
		FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();
		FacilioModule slaRuleModule = ModuleFactory.getSLARuleModule();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(slaRuleModule.getTableName())
														.innerJoin(workflowModule.getTableName())
														.on(slaRuleModule.getTableName()+".ID = "+workflowModule.getTableName()+".ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowModule))
														.andCondition(CriteriaAPI.getIdCondition(ruleId, slaRuleModule));
		
		return updateBuilder.update(FieldUtil.getAsProperties(slaRule));
	}
	
	protected static SLARuleContext constructSLARuleFromProps(Map<String, Object> prop, ModuleBean modBean) throws Exception {
		SLARuleContext slaRule = FieldUtil.getAsBeanFromMap(prop, SLARuleContext.class);
		slaRule.setResource(ResourceAPI.getResource(slaRule.getResourceId()));
		return slaRule;
	}
}
