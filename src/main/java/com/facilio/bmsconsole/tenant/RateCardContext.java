package com.facilio.bmsconsole.tenant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RateCardContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private List<RateCardServiceContext> services;
	public List<RateCardServiceContext> getServices() {
		return services;
	}
	public List<RateCardServiceContext> getServiceOfType(Integer... serviceTypes) {
		List<RateCardServiceContext> services1 = null;
		if(services != null && !services.isEmpty() && serviceTypes != null) {
			List<Integer> serviceTypeList = Arrays.asList(serviceTypes); 
			for(RateCardServiceContext service :services) {
				
				if(serviceTypeList.contains(service.getServiceType())) {
					if(services1 == null) {
						services1 = new ArrayList<>();
					}
					services1.add(service);
				}
			}
		}
		return services1;
	}
	public void setServices(List<RateCardServiceContext> services) {
		this.services = services;
	}
}
