package com.facilio.bmsconsole.context;

import java.text.ParseException;

import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class WorkOrderContext extends TicketContext {
	private UserContext requester;
	public UserContext getRequester() {
		return requester;
	}
	public void setRequester(UserContext requester) {
		this.requester = requester;
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
	
	public String getUrl() {
//		return "http://"+OrgInfo.getCurrentOrgInfo().getOrgDomain()+".fazilio.com/app/workorders/open/summary/"+getId(); Removing subdomain temp
		return "http://www.fazilio.com/app/workorders/open/summary/"+getId();
	}
}
