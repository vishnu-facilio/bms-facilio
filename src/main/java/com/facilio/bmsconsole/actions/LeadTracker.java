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
		
		
		if(getUserDetails() != null)
		{
			JSONObject microsoftLeadUserDetails = getUserDetails();
			
			
			JSONObject lead = new JSONObject();
			lead.put("first_name", microsoftLeadUserDetails.get("firstName"));
			lead.put("last_name", microsoftLeadUserDetails.get("lastName"));
			lead.put("email", microsoftLeadUserDetails.get("email"));
			lead.put("lead_source_id", "2000597960");
			JSONObject company = new JSONObject();
			company.put("name",microsoftLeadUserDetails.get("company"));
			lead.put("company", company);
			lead.put("mobile_number", microsoftLeadUserDetails.get("phone"));
			lead.put("country", microsoftLeadUserDetails.get("country"));
			
			
			FreshsalesUtil.createLead("leads","lead",lead);

		}
		else
		{
			FreshsalesUtil.createLead("leads","lead",getData());
		}
		
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
	
	private JSONObject userDetails;
	
	public void setUserDetails(JSONObject userDetails) {
		this.userDetails = userDetails;
	}
	
	public JSONObject getUserDetails() {
		return this.userDetails;
	}
}
