package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.constants.FacilioConstants;

public class GetCommissioningLogsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		
		List<CommissioningLogContext> commissioniongList = CommissioningApi.commissioniongList(null, true, pagination);
		context.put("logs", commissioniongList);
		
		return false;
	}

}
