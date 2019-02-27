package com.facilio.bmsconsole.context;

import java.util.List;

public class ZoneContext extends BaseSpaceContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<BaseSpaceContext> basespaces;
	public List<BaseSpaceContext> getBasespaces() {
		return basespaces;
	}
	public void setBasespaces(List<BaseSpaceContext> basespaces) {
		this.basespaces = basespaces;
	}
	private BaseSpaceContext baseSpaceContext;
	public BaseSpaceContext getBaseSpaceContext() {
		return baseSpaceContext;
	}
	public void setBaseSpaceContext(BaseSpaceContext baseSpaceContext) {
		this.baseSpaceContext = baseSpaceContext;
	}
	private FloorContext floor ;
	public FloorContext getFloor() {
		return floor;
	}
	public void setFloor(FloorContext floor) {
		this.floor = floor;
	}
	private SiteContext site;
	public SiteContext getSite() {
		return site;
	}
	public void setSite(SiteContext site) {
		this.site = site;
	}
	private BuildingContext building;
	public BuildingContext getBuilding() {
		return building;
	}
	public void setBuilding(BuildingContext building) {
		this.building = building;
	}
	private boolean tenantZone;
	public boolean getTenantZone() {
		return tenantZone;
	}
	public void setTenantZone(boolean tenantZone) {
		this.tenantZone = tenantZone;
	}
	
}
