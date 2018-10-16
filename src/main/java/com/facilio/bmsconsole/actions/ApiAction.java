package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.data.EventInfo;
import com.opensymphony.xwork2.ActionSupport;

public class ApiAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Org Info is :"+ AccountUtil.getCurrentOrg().getOrgId());
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
