package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.log4j.Logger;

public class ForkChainToInstantJobCommand extends FacilioCommand {
	private static final Logger LOGGER = Logger.getLogger(ForkChainToInstantJobCommand.class.getName());
	private List<Command> commands = new ArrayList<>();
	private boolean propogateException = true;

	public ForkChainToInstantJobCommand() {

	}

	public ForkChainToInstantJobCommand(boolean propogateException) {
		this.propogateException = propogateException;
	}
	
	public ForkChainToInstantJobCommand addCommand(FacilioCommand command) {
		if (command instanceof Serializable) {
			commands.add(command);	
		}
		return this;
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		try {
			if (!commands.isEmpty()) {
				Context noSeriablable = new FacilioContext();
				for (Object key : context.keySet()) {
					Object object = context.get(key);
					if (object != null && object instanceof Serializable) {
						noSeriablable.put(key, object);
					}
				}
				noSeriablable.put(FacilioConstants.Job.FORKED_COMMANDS, commands);
				FacilioTimer.scheduleInstantJob("ForkedChain", (FacilioContext) noSeriablable);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while trying to forked commands in instant job", e);
			if (propogateException) {
				throw e;
			}
		}
		return false;
	}

}
