package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;

public class GetCommissioningLogsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<CommissioningLogContext> commissioniongList = CommissioningApi.commissioniongList(null);
		context.put("logs", commissioniongList);
		
		return false;
	}

}
