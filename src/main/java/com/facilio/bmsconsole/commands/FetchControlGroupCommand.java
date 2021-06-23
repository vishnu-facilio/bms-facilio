package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.controlaction.context.ControlGroupContext;
import com.facilio.controlaction.util.ControlActionUtil;

public class FetchControlGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long controlGroupId = (Long) context.get(ControlActionUtil.CONTROL_ACTION_GROUP_ID);
		if(controlGroupId != null && controlGroupId > 0) {
			List<ControlGroupContext> controlGroups = ControlActionUtil.getControlActionGroups(Collections.singletonList(controlGroupId));
			context.put(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS, controlGroups);
		}
		return false;
	}

}
