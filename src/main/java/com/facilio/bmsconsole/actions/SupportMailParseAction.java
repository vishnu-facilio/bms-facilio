package com.facilio.bmsconsole.actions;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.opensymphony.xwork2.ActionSupport;

public class SupportMailParseAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		//System.out.println(s3.toJSONString());
		System.out.println("Added to WorkorderEmail table with id : "+WorkOrderAPI.addS3MessageId((String) s3.get("messageId")));
		return SUCCESS;
	}
	
	private JSONObject s3;
	public JSONObject getS3() {
		return s3;
	}
	public void setS3(JSONObject s3) {
		this.s3 = s3;
	}

	private JSONObject from;
	public JSONObject getFrom() {
		return from;
	}
	public void setFrom(JSONObject from) {
		this.from = from;
	}
	
	private JSONObject to;
	public JSONObject getTo() {
		return to;
	}
	public void setTo(JSONObject to) {
		this.to = to;
	}
	
	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private String body;
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	private long workOrderId;
 	public long getWorkOrderId() {
 		return workOrderId;
 	}
 	public void setWorkOrderId(long workOrderId) {
 		this.workOrderId = workOrderId;
 	}
}
