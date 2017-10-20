package com.facilio.bmsconsole.context;

public class FloorContext extends BaseSpaceContext {
	private BuildingContext building;
	public BuildingContext getBuilding() {
		return building;
	}
	public void setBuilding(BuildingContext building) {
		this.building = building;
		if(building != null) {
			super.setBuildingId(building.getId());
		}
	}
	
	@Override
	public long getBuildingId() {
		if(building != null) {
			return building.getId();
		}
		return super.getBuildingId();
	}
}
