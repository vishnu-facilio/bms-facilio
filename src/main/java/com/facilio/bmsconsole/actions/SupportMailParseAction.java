package com.facilio.bmsconsole.actions;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.jobs.WorkOrderRequestEmailParser.Status;
import com.facilio.bmsconsole.util.WorkOrderRequestAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.handler.EmailProcessHandler;
import com.facilio.wmsv2.message.Message;
import com.opensymphony.xwork2.ActionSupport;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

@Getter
@Setter
public class SupportMailParseAction extends ActionSupport {
	
	/**
	 * 
	 */
	private long startTime,endTime;
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
	public void runUnprocessedEmail() throws Exception
	{
		GenericSelectRecordBuilder select=new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkorderEmailFields())
				.table(ModuleFactory.getWorkOrderRequestEMailModule().getTableName())
				.andCustomWhere("STATE = ? AND CREATED_TIME BETWEEN ? AND ?",Status.FAILED.getVal(),getStartTime(),getEndTime());
		List<Map<String,Object>> props=select.get();
		for(int i=0;i<props.size();i++)
		{
			
			Map<String, Object> dataBag = new HashMap<>();
			dataBag.put("state",Status.NEW.getVal());
			EmailProcessHandler.updateEmailProp(FacilioUtil.parseLong(props.get(i).get("id")), dataBag);
					
			Map<String,Object> workOrderEmailProps=props.get(i);
			SessionManager.getInstance().sendMessage(new Message()
			        .setTopic(EmailProcessHandler.TOPIC)
			        .setContent(FieldUtil.getAsJSON(workOrderEmailProps)));
		}
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
