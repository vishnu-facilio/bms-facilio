package com.facilio.bmsconsole.context;

import java.io.Serializable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PublishMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private long sentTime = -1;
	public long getSentTime() {
		return sentTime;
	}
	public void setSentTime(long sentTime) {
		this.sentTime = sentTime;
	}
	
	private long acknowledgeTime = -1;
	public long getAcknowledgeTime() {
		return acknowledgeTime;
	}
	public void setAcknowledgeTime(long acknowledgeTime) {
		this.acknowledgeTime = acknowledgeTime;
	}
	
	public boolean isAcknowledged() {
		return acknowledgeTime != -1;
	}
	
	private JSONObject data;
	public JSONObject getData() {
		return data;
	}
	public void setData(JSONObject data) {
		this.data = data;
	}
	
	public String getDataStr() {
		if (data != null) {
			return data.toJSONString();
		}
		return null;
	}
	public void setDataStr(String dataStr) throws ParseException {
		if (dataStr != null) {
			JSONParser parser = new JSONParser();
			this.data = (JSONObject) parser.parse(dataStr);
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder()
					.append("PublishMessage [")
					.append("id : ").append(id).append(", ")
					.append("parentId : ").append(parentId).append(", ")
					.append("sentTime : ").append(sentTime).append(", ")
					.append("acknowledgeTime : ").append(acknowledgeTime).append(", ")
					.append("data : (").append(getDataStr()).append(")")
					.append("]")
					.toString();
	}
}
