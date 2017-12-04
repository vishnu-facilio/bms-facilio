package com.facilio.wms.message;

import org.json.simple.JSONObject;

public class WmsNotification extends Message {

	public WmsNotification() {
		super.setMessageType(MessageType.NOTIFICATION);
		super.addData("namespace", "notification");
		super.addData("action", "newNotification");
	}
	
	public WmsNotification setFrom(long from) {
		super.addData("from", from);
		return this;
	}
	
	public long getFrom() {
		return (Long) super.getData("from");
	}
	
	public WmsNotification setNotification(JSONObject notification) {
		super.addData("notification", notification);
		return this;
	}
	
	public JSONObject getNotification() {
		return (JSONObject) super.getData("notification");
	}
	
	public static enum WmsEventType {
		USER_STATUS,
		RECORD_UPDATE,
		OTER;
	}
	
	public static enum WmsUserStatus {
		ONLINE,
		IDLE,
		OFFLINE;
	}
}
