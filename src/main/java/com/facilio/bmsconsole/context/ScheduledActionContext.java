package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.workflow.rule.ActionContext;

import java.time.LocalTime;

public class ScheduledActionContext {

	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long actionId;
	public long getActionId() {
		return actionId;
	}
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
	
	private ActionContext action;
	public ActionContext getAction() {
		return action;
	}
	public void setAction(ActionContext action) {
		this.action = action;
	}

	private FacilioFrequency frequency;
	public int getFrequency() {
		if (frequency != null) {
			return frequency.getValue();
		}
		return -1;
	}
	public void setFrequency(int frequency) {
		this.frequency = FacilioFrequency.valueOf(frequency);
	}
	public void setFrequency(FacilioFrequency frequency) {
		this.frequency = frequency;
	}
	public FacilioFrequency getFrequencyEnum() {
		return frequency;
	}
	
	private LocalTime time;
	public LocalTime getTimeObj() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public String getTime() {
		return time == null? null : time.toString();
	}
	public void setTime(String time) {
		this.time = LocalTime.parse(time);
	}
}
