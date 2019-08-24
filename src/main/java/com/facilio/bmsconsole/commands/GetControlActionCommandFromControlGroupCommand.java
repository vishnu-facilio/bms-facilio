package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControlGroupContext;
import com.facilio.controlaction.util.ControlActionUtil;

public class GetControlActionCommandFromControlGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<ControlGroupContext> controlGroups = (List<ControlGroupContext>) context.get(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS);
		
		String value = (String)context.get(ControlActionUtil.VALUE);
		
		List<ControlActionCommandContext> commands = new ArrayList<ControlActionCommandContext>();
		
		if(controlGroups != null && !controlGroups.isEmpty() && value != null) {
			
			for(ControlGroupContext controlGroup :controlGroups) {

				if(controlGroup.getMatchedResources() != null ) {
					
					for(Long resourceId : controlGroup.getMatchedResources()) {
						
						ControlActionCommandContext controlActionCommand = new ControlActionCommandContext();
						ResourceContext resourceContext = new ResourceContext();
						resourceContext.setId(resourceId);
						controlActionCommand.setResource(resourceContext);
						controlActionCommand.setFieldId(controlGroup.getFieldId());
						controlActionCommand.setValue(value);
						
						commands.add(controlActionCommand);
					}
				}
			}
		}
		if(!commands.isEmpty()) {
			context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS,commands);
		}
		
		return false;
	}

}
