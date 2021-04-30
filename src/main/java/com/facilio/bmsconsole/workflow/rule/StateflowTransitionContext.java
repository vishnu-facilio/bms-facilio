package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import java.util.Map;

public class StateflowTransitionContext extends AbstractStateTransitionRuleContext {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders,
								FacilioContext context) throws Exception {
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		return evaluateStateFlow(moduleRecord.getStateFlowId(), moduleRecord.getModuleState(), moduleName, record, placeHolders, context);
	}

	@Override
	protected void executeTrue(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(getModuleId());
		StateFlowRulesAPI.updateState(moduleRecord, module, StateFlowRulesAPI.getStateContext(getToStateId()), false, context);
	}
}
