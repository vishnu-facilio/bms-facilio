package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;

public abstract class FacilioCommand implements Command, Filter {
	
	@Override
	public final boolean execute(Context context) throws Exception {
		return executeCommand(context);
	}
	
	@Override
	public boolean postprocess(Context context, Exception exception) {
		return false;
	}
	
	public abstract boolean executeCommand(Context context) throws Exception;

}
