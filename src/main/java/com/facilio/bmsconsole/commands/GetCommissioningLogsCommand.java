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
		List<CommissioningLogContext>published = new ArrayList<>();
		List<CommissioningLogContext>draft = new ArrayList<>();
		
		List<CommissioningLogContext> commissioningList = CommissioningApi.commissioniongList(null, true, pagination);
		for (CommissioningLogContext commissioning : commissioningList ){
			long publishedTime = commissioning.getPublishedTime();
			if (publishedTime==-1){
				draft.add(commissioning);
			}
			else{
				published.add(commissioning);
			}
		}
		if (status.equals("draft")){
			context.put("logs", draft);
		} else if (status.equals("published")) {
			context.put("logs", published);
		}
		else {
			context.put("logs", commissioningList);
		}

		return false;
	}

}
