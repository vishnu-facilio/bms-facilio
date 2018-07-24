package com.facilio.bmsconsole.context;

public class SpaceContext extends BaseSpaceContext {
	
	private SiteContext site;
	public SiteContext getSite() {
		if ((site == null || site.getId() == -1) && super.getSiteId() != -1) {
			SiteContext site = new SiteContext();
			site.setId(super.getSiteId());
			return site;
		}
		return site;
	}
	public void setSite(SiteContext site) {
		this.site = site;
		if(site != null) {
			super.setSiteId(site.getId());
		}
	}
	
	@Override
	public long getSiteId() {
		if(site != null) {
			return site.getId();
		}
		return super.getSiteId();
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
	
	private FloorContext floor;
	public FloorContext getFloor() {
		if ((floor == null || floor.getId() == -1) && super.getFloorId() != -1) {
			FloorContext floor = new FloorContext();
			floor.setId(super.getFloorId());
			return floor;
		}
		return floor;
	}
	public void setFloor(FloorContext floor) {
		this.floor = floor;
		if(floor != null) {
			super.setFloorId(floor.getId());
		}
	}
	@Override
	public long getFloorId() {
		if(floor != null) {
			return floor.getId();
		}
		return super.getFloorId();
	}

	private SpaceCategoryContext spaceCategory;
	public SpaceCategoryContext getSpaceCategory() {
		return spaceCategory;
	} 
	public void setSpaceCategory(SpaceCategoryContext spaceCategory) {
		this.spaceCategory = spaceCategory;
	}
}
