package com.facilio.bmsconsoleV3.context;


import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class VisitResponseContextV3 extends V3Context{

	private static final long serialVersionUID = 1L;
	
	private String responseName;
	public String getResponseName() {
		return responseName;
	}
	public void setResponseName(String responseName) {
		this.responseName = responseName;
	}
	public String getMessageTitle() {
		return messageTitle;
	}
	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}
	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	private String messageTitle;
	private String messageText;

	private MessageType messageType;
	public Integer getMessageType() {
		if (messageType != null) {
			return messageType.getIndex();
		}
		return null;
	}
	public void setMessageType(Integer messageType) {
		if(messageType != null) {
			this.messageType = VisitResponseContextV3.MessageType.valueOf(messageType);
		}
	}

	public MessageType getMessageTypeEnum(MessageType messageType) {
		return messageType;
	}

	public static enum MessageType implements FacilioIntEnum {
		TEXT("Text"),
		IMAGE("Image"),
		VIDEO("Video");

		private String name;

		MessageType(String name) {
			this.name = name;
		}

		public static VisitResponseContextV3.MessageType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name;
		}
	}
	
}
