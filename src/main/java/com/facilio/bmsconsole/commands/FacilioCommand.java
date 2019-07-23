package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public abstract class FacilioCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		return executeCommand(context);
	}
	
	public abstract boolean executeCommand(Context context) throws Exception;

}
