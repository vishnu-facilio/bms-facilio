package com.facilio.bmsconsole.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.tasker.ScheduleInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class VisitorInviteContext extends ModuleBaseWithCustomFields{
	
	private static final long serialVersionUID = 1L;

	private String inviteName;
	private long expectedStartTime = -1;
	private long expectedEndTime = -1;
	private long expectedDuration = -1;
	
	private User inviteHost;
	

	public String getInviteName() {
		return inviteName;
	}

	public void setInviteName(String inviteName) {
		this.inviteName = inviteName;
	}

	public User getInviteHost() {
		return inviteHost;
	}

	public void setInviteHost(User inviteHost) {
		this.inviteHost = inviteHost;
	}

	public long getExpectedStartTime() {
		return expectedStartTime;
	}

	public void setExpectedStartTime(long expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}

	public long getExpectedEndTime() {
		return expectedEndTime;
	}

	public void setExpectedEndTime(long expectedEndTime) {
		this.expectedEndTime = expectedEndTime;
	}

	public long getExpectedDuration() {
		return expectedDuration;
	}

	public void setExpectedDuration(long expectedDuration) {
		this.expectedDuration = expectedDuration;
	}

	private List<InviteVisitorRelContext> invitees;

	public List<InviteVisitorRelContext> getInvitees() {
		return invitees;
	}

	public void setInvitees(List<InviteVisitorRelContext> invitees) {
		this.invitees = invitees;
	}
	
	private Boolean isApprovalNeeded;

	public Boolean getIsApprovalNeeded() {
		return isApprovalNeeded;
	}

	public void setIsApprovalNeeded(Boolean isApprovalNeeded) {
		this.isApprovalNeeded = isApprovalNeeded;
	}

	public boolean isApprovalNeeded() {
		if (isApprovalNeeded != null) {
			return isApprovalNeeded.booleanValue();
		}
		return false;
	}
	
	
	private Boolean isInviteeApprovalNeeded;

	public Boolean getIsInviteeApprovalNeeded() {
		return isInviteeApprovalNeeded;
	}

	public void setIsInviteeApprovalNeeded(Boolean isApprovalNeeded) {
		this.isInviteeApprovalNeeded = isApprovalNeeded;
	}

	public boolean isInviteeApprovalNeeded() {
		if (isInviteeApprovalNeeded != null) {
			return isInviteeApprovalNeeded.booleanValue();
		}
		return false;
	}
	
	private InviteSource inviteSource;
	public int getInviteSource() {
		if (inviteSource != null) {
			return inviteSource.getIndex();
		}
		return -1;
	}
	public void setInviteSource(int inviteSource) {
		this.inviteSource = InviteSource.valueOf(inviteSource);
	}
	public InviteSource getInviteSourceEnum() {
		return inviteSource;
	}
	public void setInviteSource(InviteSource inviteSource) {
		this.inviteSource = inviteSource;
	}

	public static enum InviteSource implements FacilioEnum {
		WORKORDER, PURCHASE_ORDER, TENANT, MANUAL;

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static InviteSource valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private long sourceId;


	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
	
	private VisitorTypeContext visitorType;

	public VisitorTypeContext getVisitorType() {
		return visitorType;
	}

	public void setVisitorType(VisitorTypeContext visitorType) {
		this.visitorType = visitorType;
	}
	
	public String getScheduleJson() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(schedule != null) {
			return FieldUtil.getAsJSON(schedule).toJSONString();
		}
		return null;
	}
	public void setScheduleJson(String jsonString) throws JsonParseException, JsonMappingException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		this.schedule = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(jsonString), ScheduleInfo.class);
	}
	
	private ScheduleInfo schedule;
	public ScheduleInfo getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleInfo schedule) {
		this.schedule = schedule;
	}

	private long endExecutionTime = -1;
	public long getEndExecutionTime() {
		return endExecutionTime;
	}
	public void setEndExecutionTime(long endExecutionTime) {
		this.endExecutionTime = endExecutionTime;
	}
	
	private long nextExecutionTime = -1;
	public long getNextExecutionTime() {
		return nextExecutionTime;
	}
	public void setNextExecutionTime(long nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}


	private Boolean isRecurring;

	public Boolean getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public boolean isRecurring() {
		if (isRecurring != null) {
			return isRecurring.booleanValue();
		}
		return false;
	}
	
}
