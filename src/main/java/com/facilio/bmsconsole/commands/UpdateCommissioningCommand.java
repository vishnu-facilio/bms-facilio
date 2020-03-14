package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.constants.FacilioConstants.ContextNames;

public class UpdateCommissioningCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		CommissioningLogContext log = (CommissioningLogContext) context.get(ContextNames.LOG);
		if (log.getPoints() != null) {
			CommissioningApi.checkRDMType(log.getPoints());
		}
		
		log.setSysModifiedTime(System.currentTimeMillis());
		log.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
		
		CommissioningApi.updateLog(log);
		
		return false;
	}

}
