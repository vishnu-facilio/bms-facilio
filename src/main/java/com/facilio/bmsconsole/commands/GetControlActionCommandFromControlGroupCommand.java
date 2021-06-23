package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
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
						
						ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(resourceId, controlGroup.getField());
						
						if(rdm != null && rdm.isControllable() && rdm.getReadingTypeEnum() == ReadingDataMeta.ReadingType.WRITE) {
							
							ControlActionCommandContext controlActionCommand = new ControlActionCommandContext();
							ResourceContext resourceContext = new ResourceContext();
							resourceContext.setId(resourceId);
							controlActionCommand.setResource(resourceContext);
							controlActionCommand.setFieldId(controlGroup.getFieldId());
							controlActionCommand.setValue(value);
							controlActionCommand.setControlActionMode(controlGroup.getMode());
							controlActionCommand.setRdm(rdm);
							
							commands.add(controlActionCommand);
						}
						
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
