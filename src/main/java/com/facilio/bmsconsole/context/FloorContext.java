package com.facilio.bmsconsole.context;

public class FloorContext extends BaseSpaceContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int floorlevel;
	
	public int getFloorlevel() {
		return floorlevel;
	}
	public void setFloorlevel(int floorlevel) {
		this.floorlevel = floorlevel;
	}
	
	private long floorPlanId;

	public long getFloorPlanId() {
		return floorPlanId;
	}
	public void setFloorPlanId(long floorPlanId) {
		this.floorPlanId = floorPlanId;
	}
	
	private String floorPlanInfo;
	
	public String getFloorPlanInfo() {
		return floorPlanInfo;
	}
	public void setFloorPlanInfo(String floorPlanInfo) {
		this.floorPlanInfo = floorPlanInfo;
	}
}
