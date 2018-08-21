package com.facilio.wms.message;

import org.json.simple.JSONObject;

public class WmsRemoteScreenMessage extends Message {

	public WmsRemoteScreenMessage() {
		super.setMessageType(MessageType.REMOTE_SCREEN);
		super.setNamespace("remotescreen");
	}
	
	public WmsRemoteScreenMessage setAction(RemoteScreenAction action) {
		super.setAction(action.name());
		return this;
	}
	
	public WmsRemoteScreenMessage setFrom(long from) {
		super.addData("from", from);
		return this;
	}
	
	public long getFrom() {
		return (Long) super.getData("from");
	}
	
	public WmsRemoteScreenMessage setAdditionalInfo(JSONObject info) {
		super.addData("additionalInfo", info);
		return this;
	}
	
	public JSONObject getAdditionalInfo() {
		return (JSONObject) super.getData("additionalInfo");
	}
	
	public static enum RemoteScreenAction {
		REFRESH
	}
}
