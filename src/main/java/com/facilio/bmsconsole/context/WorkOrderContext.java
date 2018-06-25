package com.facilio.bmsconsole.context;

import java.text.ParseException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.User;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class WorkOrderContext extends TicketContext {
    private static Logger log = LogManager.getLogger(WorkOrderContext.class.getName());
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
	
	public String toString(){
		return this.getId()+"";
		
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
					log.info("Exception occurred ", e1);
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
	
	private long modifiedTime = -1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	private Boolean isWorkDurationChangeAllowed;
	public Boolean getIsWorkDurationChangeAllowed() {
		return isWorkDurationChangeAllowed;
	}
	public void setIsWorkDurationChangeAllowed(Boolean isWorkDurationChangeAllowed) {
		this.isWorkDurationChangeAllowed = isWorkDurationChangeAllowed;
	}
	public Boolean isWorkDurationChangeAllowed() {
		if(isWorkDurationChangeAllowed != null) {
			return isWorkDurationChangeAllowed.booleanValue();
		}
		return false;
	}
	
	private PreventiveMaintenance pm;
	public PreventiveMaintenance getPm() {
		return pm;
	}
	public void setPm(PreventiveMaintenance pm) {
		this.pm = pm;
	}
	
	public String getUrl() {
//		return "http://"+OrgInfo.getCurrentOrgInfo().getOrgDomain()+".fazilio.com/app/workorders/open/summary/"+getId(); Removing subdomain temp
		if(super.getId() != -1) {
			return AwsUtil.getConfig("clientapp.url")+"/app/wo/orders/summary/"+getId();
		}
		else {
			return null;
		}
	}
	
	public String getMobileUrl() {
		if(super.getId() != -1) {
			return   AwsUtil.getConfig("clientapp.url")+"/mobile/workorder/summary/"+getId();
		}
		else {
			return null;
		}
	}
}
