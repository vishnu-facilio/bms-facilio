package com.facilio.bmsconsoleV3.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.activity.EmailConversationThreadingActivityType;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.workflowlog.context.WorkflowLogContext.WorkflowLogType;

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
	public Long getParentBaseMailId(){
		if(getParentBaseMail() != null) {
			return this.getParentBaseMail().getId();
		}
		return null;
	}
	
	
	public enum Email_Status_Type implements FacilioIntEnum {
		ISNEW(1,"emailConversationIsNewRecord"),
		CUSTOMER_REPLIED(2,"emailConversationCustomerReplied"),
		AGENT_REPLIED(3,"emailConversationAgentReplied");

		public int typeId;
		private String status;
		
		public int getTypeId() {
			return typeId;
		}

		public void setTypeId(int typeId) {
			this.typeId = typeId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
		
		Email_Status_Type(int typeId, String status) {
			this.typeId = typeId;
			this.status = status;
		}
		public static final Map<Integer, Email_Status_Type> statusTypeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, Email_Status_Type> initTypeMap() {
			Map<Integer, Email_Status_Type> typeMap = new HashMap<>();
			
			for(Email_Status_Type type : values()) {
				typeMap.put(type.getTypeId(), type);
			}		
		return typeMap;
	}
	}
	
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
		REPLY(EmailConversationThreadingActivityType.REPLIED,"reply"),
		PUBLIC_NOTE(EmailConversationThreadingActivityType.ADDED_PUBLIC_NOTE,"public"),
		PRIVATE_NOTE(EmailConversationThreadingActivityType.ADDED_PRIVATE_NOTE,"private")
		;
		
		EmailConversationThreadingActivityType activityType;
		String name;
		
		Message_Type(EmailConversationThreadingActivityType activityType,String name) {
			this.activityType = activityType;
			this.name = name;
		}
		
		public EmailConversationThreadingActivityType getActivityType() {
			return activityType;
		}
		public String getName() {
			return name;
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
	
	public From_Type getFromTypeEnum() {
		return fromType;
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
