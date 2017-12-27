package com.facilio.bmsconsole.context;

import java.text.ParseException;

import com.facilio.accounts.dto.User;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class WorkOrderContext extends TicketContext {
	private User requester;
	public User getRequester() {
		return requester;
	}
	public void setRequester(User requester) {
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
	public String getCreatedTimeString() {
		if(createdTime != -1) {
			return DateTimeUtil.getZonedDateTime(createdTime).format(FacilioConstants.READABLE_DATE_FORMAT);
		}
		return null;
	}
	
	private long duration = -1; //In Seconds
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public String getUrl() {
//		return "http://"+OrgInfo.getCurrentOrgInfo().getOrgDomain()+".fazilio.com/app/workorders/open/summary/"+getId(); Removing subdomain temp
		if(super.getId() != -1) {
			return AwsUtil.getConfig("clientapp.url")+"/app/wo/orders/all/summary/"+getId();
		}
		else {
			return null;
		}
	}
}
