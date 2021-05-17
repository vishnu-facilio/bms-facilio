package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.activity.EmailConversationThreadingActivityType;
import com.facilio.bmsconsole.context.PeopleContext;
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
	PeopleContext fromPeople;
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
		REPLY(EmailConversationThreadingActivityType.REPLIED),
		PUBLIC_NOTE(EmailConversationThreadingActivityType.ADDED_PUBLIC_NOTE),
		PRIVATE_NOTE(EmailConversationThreadingActivityType.ADDED_PRIVATE_NOTE)
		;
		
		EmailConversationThreadingActivityType activityType;
		
		Message_Type(EmailConversationThreadingActivityType activityType) {
			this.activityType = activityType;
		}
		
		public EmailConversationThreadingActivityType getActivityType() {
			return activityType;
		}
				
		public static Message_Type valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	public Message_Type getMessageTypeEnum() {
		return messageType;
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
