package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ServiceRequestContext extends ModuleBaseWithCustomFields{
	/**
	 * 
	 */
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
	
	private User requester;
	public User getRequester() {
		return requester;
	}
	public void setRequester(User requester) {
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
	public void setSourceType(SourceType type) {
		this.sourceType = type;
	}
	public SourceType getSourceTypeEnum() {
		return sourceType;
	}
	
	private ServiceRequestPriorityContext priority;
	public ServiceRequestPriorityContext getPriority() {
		return priority;
	}
	
	public void setPriority(ServiceRequestPriorityContext priority) {
		this.priority = priority;
	}
	
	private Classification classification;
	public int getClassification() {
		if(classification != null) {
			return classification.getIntVal();
		}
		else {
			return -1;
		}
	}
	
	public String getClassificationVal() {
		if(classification != null) {
			return classification.getStringVal();
		}
		return null;
	}
	
	public void setClassification(int type) {
		this.classification = Classification.classificationType.get(type);
	}
	
	public void setClassification(Classification classification) {
		this.classification = classification;
	}
	
	public Classification getClassificationEnum() {
		return classification;
	}
	
	public static enum SourceType {
		
		WEB_REQUEST(1, "Web Request"),
		EMAIL_REQUEST(2, "E Mail Request"),
		SMS_REQUEST(3, "SMS Request"),
		KIOSK(4,"Kiosk"),
		PORTAL(5,"Portal")		
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
	
	public static enum Classification {
		
		QUESTION(1, "Question"),
		PROBLEM(2, "Problem"),
		FEATURE(3, "Feature"),		
		;
		
		private int intVal;
		private String strVal;
		
		private Classification(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		public static Classification getClassification(int val) {
			return classificationType.get(val);
		}
		
		private static final Map<Integer, Classification> classificationType = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, Classification> initTypeMap() {
			Map<Integer, Classification> classificationType = new HashMap<>();
			
			for(Classification type : values()) {
				classificationType.put(type.getIntVal(), type);
			}
			return classificationType;
		}
		public Map<Integer, Classification> getAllClassification() {
			return classificationType;
		}
	}
}
