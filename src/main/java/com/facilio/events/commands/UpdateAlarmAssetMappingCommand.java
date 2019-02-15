package com.facilio.events.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;

public class UpdateAlarmAssetMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String source = (String) context.get(EventConstants.EventContextNames.SOURCE);
		long resourceId = (long) context.get(EventConstants.EventContextNames.RESOURCE_ID);
		
		JSONObject json = new JSONObject();
		json.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		json.put("source", source);
		json.put("resourceId", resourceId);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type","application/json");
		String server = AwsUtil.getConfig("clientapp.url");
		String url = server + "/internal/updateAlarmResource";
		AwsUtil.doHttpPost(url, headers, null, json.toJSONString());
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}

}
