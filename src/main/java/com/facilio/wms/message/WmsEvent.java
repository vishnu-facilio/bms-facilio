package com.facilio.wms.message;

import com.facilio.wms.constants.WmsEventType;

public class WmsEvent extends Message {

	public WmsEvent() {
		super.setMessageType(MessageType.EVENT);
	}
	
	public WmsEvent setEventType(WmsEventType eventType) {
		super.addData("eventType", eventType.toString());
		return this;
	}
}