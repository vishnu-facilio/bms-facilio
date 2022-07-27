package com.facilio.bmsconsole.actions;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.WorkOrderRequestAPI;
import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

public class SupportMailParseAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(SupportMailParseAction.class.getName());
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		//System.out.println(s3.toJSONString());
		 HttpServletRequest request = ServletActionContext.getRequest();
		String param = request.getQueryString();		
		LOGGER.info("SupportMailParseAction::execute :: called "+param);
		LOGGER.info("Added to WorkorderEmail table with id : "+WorkOrderRequestAPI.addS3MessageId(s3,recipient));
		return SUCCESS;
	}
	
	private JSONObject s3;
	public JSONObject getS3() {
		return s3;
	}
	public void setS3(JSONObject s3) {
		this.s3 = s3;
	}
	public String recipient;
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	private long workOrderId;
 	public long getWorkOrderId() {
 		return workOrderId;
 	}
 	public void setWorkOrderId(long workOrderId) {
 		this.workOrderId = workOrderId;
 	}
}
