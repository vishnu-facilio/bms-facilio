package com.facilio.workflowlog.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowVersionHistoryContext extends V3Context {

	public WorkflowVersionHistoryContext(){

	}
	
	public WorkflowVersionHistoryContext(Long workflowId,String workflowString,Long createdTime,Long modifiedTime,V3PeopleContext createdByPeople,V3PeopleContext modifiedByPeople) {
		this.workflowId = workflowId;
		this.workflowString = workflowString;
		this.createdTime = createdTime;
		this.modifiedTime = modifiedTime;
		this.createdByPeople = createdByPeople;
		this.modifiedByPeople = modifiedByPeople;
	}
	
	public WorkflowVersionHistoryContext(Long workflowId,String workflowString,Long createdTime,Long modifiedTime) {
		this.workflowId = workflowId;
		this.workflowString = workflowString;
		this.createdTime = createdTime;
		this.modifiedTime = modifiedTime;
	}

	private static final long serialVersionUID = 1L;
	
	Long workflowId;
	String workflowString;
	int version;
	
	Long createdTime;
	Long modifiedTime;
	
	V3PeopleContext createdByPeople;
	V3PeopleContext modifiedByPeople;

}
