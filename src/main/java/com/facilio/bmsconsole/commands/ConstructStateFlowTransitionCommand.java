package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class ConstructStateFlowTransitionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		StateflowTransitionContext stateFlowRuleContext = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (stateFlowRuleContext != null) {
			if (StringUtils.isEmpty(moduleName)) {
				throw new IllegalArgumentException("Module name cannot be empty");
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField field = modBean.getField("moduleState", moduleName);
			if (field == null) {
				throw new IllegalArgumentException("StateFlow is not active for module " + moduleName);
			}
			
//			List<FieldChangeFieldContext> fields = new ArrayList<>();
//			FieldChangeFieldContext fieldChange = new FieldChangeFieldContext();
//			fieldChange.setField(field);
//			fields.add(fieldChange);
//			stateFlowRuleContext.setFields(fields);
			
			WorkflowEventContext event = new WorkflowEventContext();
			event.setActivityType(EventType.STATE_TRANSITION);
			event.setModuleName(moduleName);
			stateFlowRuleContext.setEvent(event);
		}
		return false;
	}

}
