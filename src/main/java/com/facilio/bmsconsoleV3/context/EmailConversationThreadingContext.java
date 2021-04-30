package com.facilio.bmsconsoleV3.context;

import com.facilio.modules.FacilioIntEnum;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailConversationThreadingContext extends BaseMailMessageContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	Long dataModuleId;
	Long recordId;
	BaseMailMessageContext parentBaseMail;
	String replyCC;
	String replyBCC;
	String noteNotifyTo;
	From_Type fromType;
	Message_Type messageType;
	
	public enum From_Type implements FacilioIntEnum {
		CLIENT,
		ADMIN
		;
		
		public static From_Type valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	public enum Message_Type implements FacilioIntEnum {
		REPLY,
		PUBLIC_NOTE,
		PRIVATE_NOTE
		;
		
		public static Message_Type valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	public int getFromType() {
		if(fromType != null) {
			return fromType.getIndex();
		}
		return -1;
	}

	public void setFromType(int fromType) {
		this.fromType = From_Type.valueOf(fromType);
	}

	public int getMessageType() {
		if(messageType != null) {
			return messageType.getIndex();
		}
		return -1;
	}

	public void setMessageType(int messageType) {
		this.messageType = Message_Type.valueOf(messageType);
	}
}
