package com.facilio.bmsconsole.context;

import java.util.List;

public class GraphicsFolderContext {
	private String name;
	
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	public List<GraphicsContext> getGraphics() {
		return graphics;
	}
	public void setGraphics(List<GraphicsContext> graphics) {
		this.graphics = graphics;
	}

	private String description;
	private long id;
	private long orgId;
	private List<GraphicsContext> graphics;

}
