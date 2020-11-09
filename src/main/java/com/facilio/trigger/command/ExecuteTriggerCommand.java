package com.facilio.trigger.command;

import java.util.Collections;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.trigger.context.Trigger;
import com.facilio.trigger.util.TriggerUtil;

public class ExecuteTriggerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Trigger triggerContext = (Trigger)context.get(TriggerUtil.TRIGGER_CONTEXT);
		
		TriggerUtil.executeTriggerActions(Collections.singletonList(triggerContext), (FacilioContext) context);
		
		return false;
	}

}
