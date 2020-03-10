package com.facilio.wms.message;

import org.json.simple.JSONObject;

public class WmsNotification extends Message {

	public WmsNotification() {
		super.setMessageType(MessageType.NOTIFICATION);
		super.setNamespace("notification");
		super.setAction("newNotification");
	}
	
	public WmsNotification setFrom(long from) {
		super.addData("from", from);
		return this;
	}
	
	public Long getFrom() {
		return (Long) super.getData("from");
	}
	
	public WmsNotification setNotification(JSONObject notification) {
		super.addData("notification", notification);
		return this;
	}
	
	public JSONObject getNotification() {
		return (JSONObject) super.getData("notification");
	}
}
