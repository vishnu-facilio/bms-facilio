package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

public class ConstructStateFlowTransitionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		StateflowTransitionContext stateTransition = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (stateTransition != null) {
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

			stateTransition.setActivityType(EventType.STATE_TRANSITION);
			stateTransition.setModuleName(module.getName());
		}
		return false;
	}

}
