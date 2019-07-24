package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.serializable.SerializableCommand;
import com.facilio.tasker.FacilioTimer;

public class ForkChainToInstantJobCommand extends FacilioCommand {
	
	private List<Command> commands = new ArrayList<>();
	
	public ForkChainToInstantJobCommand addCommand(SerializableCommand command) {
		commands.add(command);
		return this;
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (!commands.isEmpty()) {
			context.put(FacilioConstants.Job.FORKED_COMMANDS, commands);
			FacilioTimer.scheduleInstantJob("ForkedChain", (FacilioContext) context);
		}
		return false;
	}

}
