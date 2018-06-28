package com.facilio.bmsconsole.actions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.opensymphony.xwork2.ActionSupport;

public class LeadTracker extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		
		createLead(getData());
		
		return SUCCESS;
	}
	
	private String response = "success";
	
	private JSONObject data;
	
	public void setData(JSONObject data) {
		this.data = data;
	}
	
	public JSONObject getData() {
		return this.data;
	}
	
	private void createLead(JSONObject lead) throws IOException {
		
		if (lead == null || lead.size() == 0) {
			return;
		}
		
		JSONObject postData = new JSONObject();
		postData.put("lead", lead);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type","application/json");
		headers.put("Authorization", "Token token=erLKqferUgGcY6sy_4qVJw");
		 
		String url = "https://facilio.freshsales.io/api/leads";
		AwsUtil.doHttpPost(url, headers, null, postData.toJSONString());
	}
}
