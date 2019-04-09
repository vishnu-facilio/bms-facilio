package com.facilio.bmsconsole.workflow.rule;

import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.GenericUpdateModuleDataCommand;
import com.facilio.bmsconsole.commands.UpdateStateCommand;
import com.facilio.bmsconsole.context.StateFlowContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class StateFlowRuleContext extends WorkflowRuleContext {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		if (!(record instanceof ModuleBaseWithCustomFields)) {
			throw new Exception("Invalid record");
		}
		
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		StateFlowContext stateFlowContext = StateFlowRulesAPI.getStateFlowContext(getId());
		
		long parentModuleId = stateFlowContext.getModuleId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(parentModuleId);
		
		FacilioContext c = new FacilioContext();
		c.put(FacilioConstants.ContextNames.RECORD, moduleRecord);
		c.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		c.put("default_state_id", stateFlowContext.getDefaultStateId());
		c.put("default_state", true);
		c.put("default_state_flow_id", getId());
		
		Chain chain = FacilioChain.getTransactionChain();
		chain.addCommand(new UpdateStateCommand());
		chain.addCommand(new GenericUpdateModuleDataCommand());
		chain.execute(c);
		
		super.executeTrueActions(record, context, placeHolders);
	}
}
