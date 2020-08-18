package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ViewGroups extends ModuleBaseWithCustomFields{
	
	private String name;
	
	private String displayName;
			
	private List<FacilioView> views;
	
	private int sequenceNumber = -1;
	
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
