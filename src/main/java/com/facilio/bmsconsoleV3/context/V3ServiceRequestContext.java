package com.facilio.bmsconsoleV3.context;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ServiceRequestPriorityContext;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext.Email_Status_Type;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class V3ServiceRequestContext extends V3Context{
	
	public static final int DESCIPTION_LENGTH = 1900;  
	
	private static final long serialVersionUID = 1L;
	String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private Group assignmentGroup;
	public Group getAssignmentGroup() {
		return assignmentGroup;
	}
	public void setAssignmentGroup(Group assignmentGroup) {
		this.assignmentGroup = assignmentGroup;
	}
	
	private User assignedTo;
	public User getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	private PeopleContext requester;
	public PeopleContext getRequester() {
		return requester;
	}
	public void setRequester(PeopleContext requester) {
		this.requester = requester;
	}
	
	private long dueDate;
	public long getDueDate() {
		return dueDate;
	}
	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}
	
	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	
	private SourceType sourceType;
	public int getSourceType() {
		if(sourceType != null) {
			return sourceType.getIntVal();
		}
		else {
			return -1;
		}
	}
	public String getSourceTypeVal() {
		if(sourceType != null) {
			return sourceType.getStringVal();
		}
		return null;
	}
	public void setSourceType(int type) {
		this.sourceType = SourceType.typeMap.get(type);
	}
	public SourceType getSourceTypeEnum() {
		return sourceType;
	}
	
	private ServiceRequestPriorityContext urgency;
	public ServiceRequestPriorityContext getUrgency() {
		return urgency;
	}
	
	public void setUrgency(ServiceRequestPriorityContext priority) {
		this.urgency = priority;
	}
	
	private Integer classificationType;
	
	private Integer requestType;
	
	public static enum SourceType {
		
		APP(1, "App"),
		EMAIL_REQUEST(2, "E Mail Request"),
		SMS_REQUEST(3, "SMS Request"),
		KIOSK(4,"Kiosk"),
		;
		
		private int intVal;
		private String strVal;
		
		private SourceType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		public static SourceType getType(int val) {
			return typeMap.get(val);
		}
		
		private static final Map<Integer, SourceType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, SourceType> initTypeMap() {
			Map<Integer, SourceType> typeMap = new HashMap<>();
			
			for(SourceType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, SourceType> getAllTypes() {
			return typeMap;
		}
	}
	
	private int ratingVal;
	public int getRatingVal() {
		return ratingVal;
	}
	public void setRatingVal(int ratingVal) {
		this.ratingVal = ratingVal;
	}
	
	private Email_Status_Type mode;
	public int getMode() {
		if(mode!=null) {
			return mode.getTypeId();
		}
		return -1;
	}
	public void setMode(int mode) {
		this.mode = Email_Status_Type.statusTypeMap.get(mode);
	}
	private PeopleContext affectedPerson;
	private Long reportedDate,affectedDate,actualStartDate,actualFinishDate,responseDueDate  ;
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	private long appId;

}
