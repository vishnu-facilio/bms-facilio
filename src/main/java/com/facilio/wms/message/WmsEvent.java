package com.facilio.wms.message;

public class WmsEvent extends Message {

	public WmsEvent() {
		super.setMessageType(MessageType.EVENT);
	}
	
	public WmsEvent setEventType(WmsEventType eventType) {
		super.addData("eventType", eventType.toString());
		return this;
	}
	
	public WmsEvent setUserStatus(long uid, WmsUserStatus status) {
		super.addData("uid", uid);
		super.addData("status", status);
		return this;
	}
	
	public static enum WmsEventType {
		USER_STATUS,
		RECORD_UPDATE,
		OTHER;
	}
	
	public static enum WmsUserStatus {
		ONLINE,
		IDLE,
		OFFLINE;
	}
}