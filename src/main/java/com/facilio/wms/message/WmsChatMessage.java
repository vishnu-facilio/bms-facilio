package com.facilio.wms.message;

public class WmsChatMessage extends Message {

	public WmsChatMessage() {
		super.setMessageType(MessageType.CHAT);
	}
}