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
	
	private long defaultFloorPlanId;
	
	public long getDefaultFloorPlanId() {
		return defaultFloorPlanId;
	}
	public void setDefaultFloorPlanId(long defaultFloorPlanId) {
		this.defaultFloorPlanId = defaultFloorPlanId;
	}

	private long indoorFloorPlanId;

	

	private String floorPlanInfo;
	
	public String getFloorPlanInfo() {
		return floorPlanInfo;
	}
	public void setFloorPlanInfo(String floorPlanInfo) {
		this.floorPlanInfo = floorPlanInfo;
	}
	
	private long noOfIndependentSpaces = -1;
	
	public long getNoOfIndependentSpaces() {
		return noOfIndependentSpaces;
	}

	public void setNoOfIndependentSpaces(long noOfIndependentSpaces) {
		this.noOfIndependentSpaces = noOfIndependentSpaces;
	}

    /**
     * @return long return the indoorFloorPlanId
     */
    public long getIndoorFloorPlanId() {
        return indoorFloorPlanId;
    }

    /**
     * @param indoorFloorPlanId the indoorFloorPlanId to set
     */
    public void setIndoorFloorPlanId(long indoorFloorPlanId) {
        this.indoorFloorPlanId = indoorFloorPlanId;
    }

}
