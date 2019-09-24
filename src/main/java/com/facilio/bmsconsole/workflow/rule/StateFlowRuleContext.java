package com.facilio.bmsconsole.workflow.rule;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.UpdateStateCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class StateFlowRuleContext extends WorkflowRuleContext {
	private static final long serialVersionUID = 1L;
	
	private long defaultStateId;
	public long getDefaultStateId() {
		return defaultStateId;
	}
	public void setDefaultStateId(long defaultStateId) {
		this.defaultStateId = defaultStateId;
	}
	
	private Boolean defaltStateFlow;
	public Boolean getDefaltStateFlow() {
		return defaltStateFlow;
	}
	public void setDefaltStateFlow(Boolean defaltStateFlow) {
		this.defaltStateFlow = defaltStateFlow;
	}

//	private long moduleId = -1;
//	public long getModuleId() {
//		return moduleId;
//	}
//	public void setModuleId(long moduleId) {
//		this.moduleId = moduleId;
//	}
//
//	private String moduleName;
//	public String getModuleName() {
//		return moduleName;
//	}
//	public void setModuleName(String moduleName) {
//		this.moduleName = moduleName;
//	}
	
	private String diagramJson;
	public String getDiagramJson() {
		return diagramJson;
	}
	public void setDiagramJson(String diagramJson) {
		this.diagramJson = diagramJson;
	}
	
	@Override
	public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		if (!(record instanceof ModuleBaseWithCustomFields)) {
			throw new Exception("Invalid record");
		}
		
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		StateFlowRuleContext stateFlowContext = StateFlowRulesAPI.getStateFlowContext(getId());
		
		long parentModuleId = stateFlowContext.getModuleId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(parentModuleId);
		
		FacilioContext c = new FacilioContext();
		c.put(FacilioConstants.ContextNames.RECORD, moduleRecord);
		c.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		c.put("default_state_id", stateFlowContext.getDefaultStateId());
		c.put("default_state", true);
		c.put("default_state_flow_id", getId());
		
		FacilioChain chain = FacilioChain.getTransactionChain();
		chain.addCommand(new UpdateStateCommand());
		chain.execute(c);
		
		super.executeTrueActions(record, context, placeHolders);
	}
}
