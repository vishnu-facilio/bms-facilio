package com.facilio.bmsconsole.commands;

import java.text.MessageFormat;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;

public abstract class FacilioCommand implements Command, Filter {
	
	private static final Logger LOGGER = LogManager.getLogger(FacilioCommand.class.getName());
	
	@Override
	public final boolean execute(Context context) throws Exception {
		long currentMillis = System.currentTimeMillis();
		int selectCount = AccountUtil.getCurrentSelectQuery(), pSelectCount = AccountUtil.getCurrentPublicSelectQuery();
		boolean result = executeCommand(context);
		long executionTime = System.currentTimeMillis() - currentMillis;
		// if the execution takes more than 50 millis, log them
		int totalSelect = AccountUtil.getCurrentSelectQuery() - selectCount;
		int totalPublicSelect = AccountUtil.getCurrentPublicSelectQuery() - pSelectCount;
		if (executionTime > 50 || totalSelect > 10 || totalPublicSelect > 10) {
			String msg = MessageFormat.format("### time taken for command ({0}) is {1}, select : {2}, pSelect : {3}", this.getClass().getSimpleName(), executionTime, totalSelect, totalPublicSelect);
			if (AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getOrgId() == 274 || AccountUtil.getCurrentOrg().getOrgId() == 317) ) {
				LOGGER.info(msg);
			}
			else {
				LOGGER.debug(msg);
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
