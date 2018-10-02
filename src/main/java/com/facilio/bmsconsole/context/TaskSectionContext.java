package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;

public class TaskSectionContext implements Serializable {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long parentTicketId = -1;
	public long getParentTicketId() {
		return parentTicketId;
	}
	public void setParentTicketId(long parentTicketId) {
		this.parentTicketId = parentTicketId;
	}
	
	private Boolean isEditable;
	public Boolean getIsEditable() {
		return isEditable;
	}
	public void setIsEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
	public boolean isEditable() {
		if (isEditable != null) {
			return isEditable.booleanValue();
		}
		return false;
	}
	
	private long sequenceNumber = -1;
	public long getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	private List<Long> taskIds;
	public List<Long> getTaskIds() {
		return taskIds;
	}
	public void setTaskIds(List<Long> taskIds) {
		this.taskIds = taskIds;
	}
}
