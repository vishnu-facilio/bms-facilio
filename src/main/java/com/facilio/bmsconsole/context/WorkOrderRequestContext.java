package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.aws.util.AwsUtil;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorkOrderRequestContext extends TicketContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(WorkOrderRequestContext.class.getName());
	private User requester;
	public User getRequester() {
		return requester;
	}
	public void setRequester(User requester) {
		this.requester = requester;
	}
	
	private RequestStatus requestStatus;
	public int getRequestStatus() {
		if(requestStatus != null) {
			return requestStatus.getIntVal();
		}
		return -1;
	}
	public void setRequestStatus(int requestStatus) {
		this.requestStatus = RequestStatus.statusMap.get(requestStatus);
	}
	public void setRequestStatus(RequestStatus requestStatus) {
		this.requestStatus = requestStatus;
	}
	public String getRequestStatusVal() {
		if(requestStatus != null) {
			return requestStatus.getStringVal();
		}
		return null;
	}
	
	private WORUrgency urgency;
	public int getUrgency() {
		if(urgency != null) {
			return urgency.getIntVal();
		}
		return -1;
	}
	public void setUrgency(int urgency) {
		this.urgency = WORUrgency.urgencyMap.get(urgency);
	}
	public void setUrgency(WORUrgency urgency) {
		this.urgency = urgency;
	}
	public String getUrgencyVal() {
		if(urgency != null) {
			return urgency.getStringVal();
		}
		return null;
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
					log.info("Exception occurred ", e1);
				}
			}
		}
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	public static enum WORUrgency {
		NOTURGENT(1, "Not Urgent"),
		URGENT(2, "Urgent"),
		EMERGENCY(3, "Emergency");
		
		private int intVal;
		private String strVal;
		
		private WORUrgency(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, WORUrgency> urgencyMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, WORUrgency> initTypeMap() {
			Map<Integer, WORUrgency> typeMap = new HashMap<>();
			
			for(WORUrgency type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public static WORUrgency getWORUrgency(Integer intval) {
			return urgencyMap.get(intval);
		}
		public Map<Integer, WORUrgency> getAllTypes() {
			return urgencyMap;
		}
	}
	
	public String getUrl() {
		if(super.getId() != -1) {
			return AwsUtil.getConfig("clientapp.url")+"/app/wo/requests/all/summary/"+getId();
		}
		else {
			return null;
		}
	}
	
	public String getMobileUrl() {
		if(super.getId() != -1) {
			return   AwsUtil.getConfig("clientapp.url")+"/mobile/workrequest/summary/"+getId();
		}
		else {
			return null;
		}
	}
	
	public static enum RequestStatus {
		OPEN(1, "Open"),
		APPROVED(2, "Approved"),
		REJECTED(3, "Rejected"),
		CLOSED(4, "Closed");
		private int intVal;
		private String strVal;
		
		private RequestStatus(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, RequestStatus> statusMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, RequestStatus> initTypeMap() {
			Map<Integer, RequestStatus> typeMap = new HashMap<>();
			
			for(RequestStatus type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, RequestStatus> getAllTypes() {
			return statusMap;
		}
	}
}
