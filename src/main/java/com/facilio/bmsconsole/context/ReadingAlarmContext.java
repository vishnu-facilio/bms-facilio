package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.events.context.EventContext;

public class ReadingAlarmContext extends AlarmContext {
	
	List<EventContext> relatedEvents;
	
	public List<EventContext> getRelatedEvents() {
		return relatedEvents;
	}
	public void setRelatedEvents(List<EventContext> relatedEvents) {
		this.relatedEvents = relatedEvents;
	}
	public void addrelatedEvent(EventContext relatedEvent) {
		if(this.relatedEvents == null) {
			this.relatedEvents = new ArrayList<>();
		}
		relatedEvents.add(relatedEvent);
	}

	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}

	private long baselineId = -1;
	public long getBaselineId() {
		return baselineId;
	}
	public void setBaselineId(long baselineId) {
		this.baselineId = baselineId;
	}
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private String readingMessage;
	public String getReadingMessage() {
		return readingMessage;
	}
	public void setReadingMessage(String readingMessage) {
		this.readingMessage = readingMessage;
	}
}
