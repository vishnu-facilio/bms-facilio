package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.constants.FacilioConstants;

public class GetCommissioningLogsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		String status = (String) context.get("status");

		List<CommissioningLogContext> commissioningList = CommissioningApi.commissioniongList(null, true, pagination,status);
		context.put("logs", commissioningList);

		return false;
	}

}
