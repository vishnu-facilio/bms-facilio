package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.view.FacilioView;

public class ViewGroups {
	
	private String name;
	
	private String displayName;
			
	private List<FacilioView> views;
	
	private int sequenceNumber = -1;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	long id = -1;
	
	long moduleId = -1;
	
	long orgId = -1;
	
	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public List<FacilioView> getViews() {
		return views;
	}

	public void setViews(List<FacilioView> views) {
		this.views = views;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}



}
