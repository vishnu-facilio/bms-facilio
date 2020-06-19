package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class FloorPlanContext extends ModuleBaseWithCustomFields{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long floorPlanId;

	public long getFloorPlanId() {
		return floorPlanId;
	}

	public void setFloorPlanId(long floorPlanId) {
		this.floorPlanId = floorPlanId;
	}

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
