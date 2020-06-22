package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class FloorPlanContext extends ModuleBaseWithCustomFields{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private long floorPlanId;

	public long getFloorPlanId() {
		return floorPlanId;
	}

	public void setFloorPlanId(long floorPlanId) {
		this.floorPlanId = floorPlanId;
	}
	
	private long orgId = -1;

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	private long floorId = -1;
	private String name;
	private long fileId = -1;
	private String canvas;
	private Boolean isActive;
	
	public long getFloorId() {
		return floorId;
	}

	public void setFloorId(long floorId) {
		this.floorId = floorId;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}


	public String getCanvas() {
		return canvas;
	}

	public void setCanvas(String canvas) {
		this.canvas = canvas;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}


	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String leagend;


	public String getLeagend() {
		return leagend;
	}

	public void setLeagend(String leagend) {
		this.leagend = leagend;
	
	}

}
