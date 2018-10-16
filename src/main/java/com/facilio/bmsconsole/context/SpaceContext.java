package com.facilio.bmsconsole.context;

public class SpaceContext extends BaseSpaceContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	private SpaceContext space1;
	public SpaceContext getSpace1() {
		if ((space1 == null || space1.getId() == -1) && super.getSpaceId1() > -1) {
			SpaceContext space = new SpaceContext();
			space.setId(super.getSpaceId1());
			return space;
		}
		return space1;
	}
	public void setSpace1(SpaceContext space1) {
		this.space1 = space1;
		if(space1 != null) {
			super.setSpaceId1(space1.getId());
		}
	}
	
	private SpaceContext space2;
	public SpaceContext getSpace2() {
		if ((space2 == null || space2.getId() == -1) && super.getSpaceId2() != -1) {
			SpaceContext space = new SpaceContext();
			space.setId(super.getSpaceId2());
			return space;
		}
		return space2;
	}
	public void setSpace2(SpaceContext space2) {
		this.space2 = space2;
		if(space2 != null) {
			super.setSpaceId2(space2.getId());
		}
	}
	
	private SpaceContext space3;
	public SpaceContext getSpace3() {
		if ((space3 == null || space3.getId() == -1) && super.getSpaceId3() != -1) {
			SpaceContext space = new SpaceContext();
			space.setId(super.getSpaceId3());
			return space;
		}
		return space3;
	}
	public void setSpace3(SpaceContext space3) {
		this.space3 = space3;
		if(space3 != null) {
			super.setSpaceId3(space3.getId());
		}
	}
	
	private SpaceContext space4;
	public SpaceContext getSpace4() {
		if ((space4 == null || space4.getId() == -1) && super.getSpaceId4() != -1) {
			SpaceContext space = new SpaceContext();
			space.setId(super.getSpaceId4());
			return space;
		}
		return space4;
	}
	public void setSpace4(SpaceContext space4) {
		this.space4 = space4;
		if(space4 != null) {
			super.setSpaceId4(space4.getId());
		}
	}
	
	private SpaceContext parentSpace;
	public SpaceContext getParentSpace() {
		return parentSpace;
	}
	public void setParentSpace(SpaceContext parentSpace) {
		this.parentSpace = parentSpace;
	}
	//	private SpaceContext space1;
//	public SpaceContext getSpace() {
//		if ((space1 == null || space1.getId() == -1) && super.getSpaceId() != -1) {
//			SpaceContext space = new SpaceContext();
//			space.setId(super.getSpaceId());
//			return space;
//		}
//		return space1;
//	}
//	public void setSpace(SpaceContext space) {
//		this.space1 = space;
//		if(space != null) {
//			super.setSpaceId(space.getId());
//		}
//	}
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
