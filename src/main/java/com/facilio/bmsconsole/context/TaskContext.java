package com.facilio.bmsconsole.context;

import java.text.ParseException;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class TaskContext extends TicketContext {
	private long parentTicketId = -1;
	public long getParentTicketId() {
		return parentTicketId;
	}
	public void setParentTicketId(long parentTicketId) {
		this.parentTicketId = parentTicketId;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setCreatedTime(String createdTime) {
		if(createdTime != null && !createdTime.isEmpty()) {
			try {
				this.createdTime = FacilioConstants.HTML5_DATE_FORMAT.parse(createdTime).getTime();
			}
			catch (ParseException e) {
				try {
					this.createdTime = FacilioConstants.HTML5_DATE_FORMAT_1.parse(createdTime).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private Boolean isReadingTask;
	public Boolean getIsReadingTask() {
		return isReadingTask;
	}
	public void setIsReadingTask(Boolean isReadingTask) {
		this.isReadingTask = isReadingTask;
	}
	public boolean isReadingTask() {
		if(isReadingTask != null) {
			return isReadingTask.booleanValue();
		}
		return false;
	}
	
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingId) {
		this.readingFieldId = readingId;
	}
	
	private FacilioField readingField;
	public FacilioField getReadingField() {
		return readingField;
	}
	public void setReadingField(FacilioField readingField) {
		this.readingField = readingField;
	}

	private ReadingContext readingData;
	public ReadingContext getReadingData() {
		return readingData;
	}
	public void setReadingData(ReadingContext readingData) {
		this.readingData = readingData;
	}

	private long readingDataId = -1;
	public long getReadingDataId() {
		if(readingDataId == -1 && readingData != null && readingData.getId() != -1) {
			return readingData.getId();
		}
		return readingDataId;
	}
	public void setReadingDataId(long readingDataId) {
		this.readingDataId = readingDataId;
	}
}
