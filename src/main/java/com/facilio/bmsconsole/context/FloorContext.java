package com.facilio.bmsconsole.context;

public class FloorContext extends BaseSpaceContext {
	private int floorlevel;
	
	public int getFloorlevel() {
		return floorlevel;
	}
	public void setFloorlevel(int floorlevel) {
		this.floorlevel = floorlevel;
	}
	private BuildingContext building;
	public BuildingContext getBuilding() {
		if ((building == null || building.getId() == -1) && super.getBuildingId() != -1) {
			BuildingContext building = new BuildingContext();
			building.setId(super.getBuildingId());
			return building;
		}
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
