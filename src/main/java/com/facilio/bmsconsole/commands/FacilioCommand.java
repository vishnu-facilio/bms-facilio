package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class FacilioCommand implements Command, Filter {
	
	private static final Logger LOGGER = LogManager.getLogger(FacilioCommand.class.getName());
	
	@Override
	public final boolean execute(Context context) throws Exception {
		long currentMillis = System.currentTimeMillis();
		boolean result = executeCommand(context);
		long executionTime = System.currentTimeMillis() - currentMillis;
		// if the execution takes more than 50 millis, log them
		if (executionTime > 50) {
			if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
				LOGGER.info("### time taken: " + this.getClass().getSimpleName() + ": " + executionTime);
			}
			else {
				LOGGER.debug("### time taken: " + this.getClass().getSimpleName() + ": " + executionTime);
			}
		}

		return result;
	}
	
	@Override
	public boolean postprocess(Context context, Exception exception) {
		return false;
	}
	
	public abstract boolean executeCommand(Context context) throws Exception;

}
