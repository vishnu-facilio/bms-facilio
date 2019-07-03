package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ServiceContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	private double duration;
	
   public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}

private ServiceStatus status;
	
   public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(ServiceStatus status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = ServiceStatus.valueOf(status);
	}
	
	
	public static enum ServiceStatus {
		ACTIVE(),
		INACTIVE(),
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static ServiceStatus valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private List<ServiceVendorContext> serviceVendors;
	public List<ServiceVendorContext> getServiceVendors() {
		return serviceVendors;
	}
	public void setServiceVendors(List<ServiceVendorContext> serviceVendors) {
		this.serviceVendors = serviceVendors;
	}
	

}
