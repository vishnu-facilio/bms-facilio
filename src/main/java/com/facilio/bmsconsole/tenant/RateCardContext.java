package com.facilio.bmsconsole.tenant;

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
	public void setServices(List<RateCardServiceContext> services) {
		this.services = services;
	}
}
