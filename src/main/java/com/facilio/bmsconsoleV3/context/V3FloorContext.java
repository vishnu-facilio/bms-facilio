package com.facilio.bmsconsoleV3.context;

public class V3FloorContext extends V3BaseSpaceContext {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Integer floorlevel;

    public Integer getFloorlevel() {
        return floorlevel;
    }
    public void setFloorlevel(Integer floorlevel) {
        this.floorlevel = floorlevel;
    }

    private Long floorPlanId;

    public Long getFloorPlanId() {
        return floorPlanId;
    }
    public void setFloorPlanId(Long floorPlanId) {
        this.floorPlanId = floorPlanId;
    }

    private Long defaultFloorPlanId;

    public Long getDefaultFloorPlanId() {
        return defaultFloorPlanId;
    }
    public void setDefaultFloorPlanId(Long defaultFloorPlanId) {
        this.defaultFloorPlanId = defaultFloorPlanId;
    }

    private String floorPlanInfo;

    public String getFloorPlanInfo() {
        return floorPlanInfo;
    }
    public void setFloorPlanInfo(String floorPlanInfo) {
        this.floorPlanInfo = floorPlanInfo;
    }

    private Long noOfIndependentSpaces;

    public Long getNoOfIndependentSpaces() {
        return noOfIndependentSpaces;
    }

    public void setNoOfIndependentSpaces(Long noOfIndependentSpaces) {
        this.noOfIndependentSpaces = noOfIndependentSpaces;
    }
}
