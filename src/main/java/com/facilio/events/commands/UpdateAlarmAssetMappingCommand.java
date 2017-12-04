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
		String node = (String) context.get(EventConstants.EventContextNames.NODE);
		long assetId = (long) context.get(EventConstants.EventContextNames.ASSET_ID);
		
		JSONObject json = new JSONObject();
		json.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		json.put("node", node);
		json.put("assetId", assetId);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type","application/json");
		String server = AwsUtil.getConfig("servername");
		String url = "http://" + server + "/internal/updateAlarmAsset";
		AwsUtil.doHttpPost(url, headers, null, json.toJSONString());
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}

}
