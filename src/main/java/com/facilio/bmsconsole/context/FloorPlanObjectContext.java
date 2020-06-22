package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class FloorPlanObjectContext extends ModuleBaseWithCustomFields{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long getId() {
	return Id;
}
public void setId(long id) {
	Id = id;
}
	private long Id;

private long floorBoundaryId;
	
	private long floorPlanId = -1;
	
	private long resourceId = -1;
	
	FloorPlanObjectType objectType;

	private String info;
	
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	private long x;
	private long y;
	private long w;
	private long h;
	public long getFloorBoundaryId() {
		return floorBoundaryId;
	}
	public void setFloorBoundaryId(long floorBoundaryId) {
		this.floorBoundaryId = floorBoundaryId;
	}
	public long getFloorPlanId() {
		return floorPlanId;
	}
	public void setFloorPlanId(long floorPlanId) {
		this.floorPlanId = floorPlanId;
	}
	public long getX() {
		return x;
	}
	public void setX(long x) {
		this.x = x;
	}
	public long getY() {
		return y;
	}
	public void setY(long y) {
		this.y = y;
	}
	public long getW() {
		return w;
	}
	public void setW(long w) {
		this.w = w;
	}
	public long getH() {
		return h;
	}
	public void setH(long h) {
		this.h = h;
	}
	public enum FloorPlanObjectType {
		RESOURCE(1), MARKER(2);

		private int type;

		FloorPlanObjectType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

	}
}
