package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.bmsconsole.workflow.rule.StateflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class StateRulesAPI {

	public static WorkflowRuleContext constructStateRuleFromProps(Map<String, Object> prop, ModuleBean modBean) {
		StateflowRuleContext stateFlowRule = FieldUtil.getAsBeanFromMap(prop, StateflowRuleContext.class);
		return stateFlowRule;
	}

	public static StateContext getStateContext(long toStageId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STATE);
		SelectRecordsBuilder<StateContext> builder = new SelectRecordsBuilder<StateContext>()
				.beanClass(StateContext.class)
				.module(module)
				.select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getIdCondition(toStageId, module));
		StateContext stateContext = builder.fetchFirst();
		return stateContext;
	}

}
