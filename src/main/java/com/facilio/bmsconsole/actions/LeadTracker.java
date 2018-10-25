package com.facilio.bmsconsole.actions;

import org.json.simple.JSONObject;

import com.facilio.license.FreshsalesUtil;
import com.opensymphony.xwork2.ActionSupport;

public class LeadTracker extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		
		FreshsalesUtil.createLead("leads","lead",getData());
		
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
	
	
}
