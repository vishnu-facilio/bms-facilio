package com.facilio.bmsconsole.commands;

import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class ConstructStateFlowTransitionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		StateflowTransitionContext stateFlowRuleContext = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (stateFlowRuleContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (module == null) {
				module = modBean.getModule(moduleName);
			}
			if (module == null) {
				throw new IllegalArgumentException("Module name cannot be empty");
			}
			
			FacilioField field = modBean.getField("moduleState", module.getName());
			if (field == null) {
				throw new IllegalArgumentException("StateFlow is not active for module " + module.getName());
			}
			
//			List<FieldChangeFieldContext> fields = new ArrayList<>();
//			FieldChangeFieldContext fieldChange = new FieldChangeFieldContext();
//			fieldChange.setField(field);
//			fields.add(fieldChange);
//			stateFlowRuleContext.setFields(fields);
			
			stateFlowRuleContext.setActivityType(EventType.STATE_TRANSITION);
			stateFlowRuleContext.setModuleName(module.getName());
		}
		return false;
	}

}
