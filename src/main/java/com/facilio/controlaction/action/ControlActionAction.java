package com.facilio.controlaction.action;

import java.util.Collections;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.chain.FacilioContext;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;

public class ControlActionAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	
	long resourceId = -1;
	long fieldId = -1;
	String value;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	} 
	
	public String setReadingValue() throws Exception {
		
		if(resourceId <= 0 || fieldId <= 0 || value == null) {
			throw new IllegalArgumentException("One or more value is missing");
		}
		
		ResourceContext resourceContext = new ResourceContext();
		resourceContext.setId(resourceId);
		
		ControlActionCommandContext controlActionCommand = new ControlActionCommandContext();
		controlActionCommand.setResource(resourceContext);
		controlActionCommand.setFieldId(fieldId);
		controlActionCommand.setValue(value);
		
		FacilioContext context = new FacilioContext();
		
		context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, Collections.singletonList(controlActionCommand));
		context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.CARD);
		
		Chain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandChain();
		executeControlActionCommandChain.execute(context);
		
		return SUCCESS;
	}
	
	
}
