package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;


import java.util.List;

public class IndoorFloorPlanContext extends ModuleBaseWithCustomFields {
	
	public List<IndoorFloorPlanObjectContext> getFloorPlanObjects() {
		return floorPlanObjects;
	}

	public void setFloorPlanObjects(List<IndoorFloorPlanObjectContext> floorPlanObjects) {
		this.floorPlanObjects = floorPlanObjects;
	}

	List<IndoorFloorPlanObjectContext> floorPlanObjects;

	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFloorId() {
		return floorId;
	}

	public void setFloorId(long floorId) {
		this.floorId = floorId;
	}

	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private long id;
	private long floorId;
	private long siteId;
	private long orgId;
	private long fileId;
	private String name;
	private String description;
	


}
