package com.facilio.agent.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.constants.FacilioConstants.ContextNames;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AcknowledgeMessageCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long id = (long) context.get(ContextNames.ID);
		String message = (String) context.get(ContextNames.MESSAGE);
		JSONObject payLoad = (JSONObject) context.get(ContextNames.PAY_LOAD);

		IoTMessageAPI.acknowdledgeMessage(id, message, payLoad);
		return false;
	}

}
