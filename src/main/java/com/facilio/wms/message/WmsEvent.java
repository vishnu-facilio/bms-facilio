package com.facilio.wms.message;

public class WmsEvent extends Message {

	public WmsEvent() {
		super.setMessageType(MessageType.EVENT);
	}
	
	public WmsEvent setNamespace(String namespace) {
		super.addData("namespace", namespace);
		return this;
	}
	
	public String getNamespace() {
		return (String) super.getData("namespace");
	}
	
	public WmsEvent setAction(String action) {
		super.addData("action", action);
		return this;
	}
	
	public String getAction() {
		return (String) super.getData("action");
	}
	
	public WmsEvent setUserStatus(long uid, WmsUserStatus status) {
		super.addData("uid", uid);
		super.addData("status", status);
		return this;
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