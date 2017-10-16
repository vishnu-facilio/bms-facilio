package com.facilio.bmsconsole.actions;

import java.util.HashMap;

import com.facilio.bmsconsole.commands.data.EventInfo;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class ApiAction extends ActionSupport {

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Org Info is :"+OrgInfo.getCurrentOrgInfo());
		EventInfo.save(events.toArray(new EventInfo[events.size()]));
		return super.execute();
	}
	

	public java.util.ArrayList<EventInfo> getEvents() {
		return events;
	}


	public void setEvents(java.util.ArrayList<EventInfo> events) {
		this.events = events;
	}


	private java.util.ArrayList<EventInfo> events = new java.util.ArrayList<EventInfo>();
			
	
}
