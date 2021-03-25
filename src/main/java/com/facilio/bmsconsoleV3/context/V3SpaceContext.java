package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;

public class V3SpaceContext extends V3BaseSpaceContext{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
    
	private V3SpaceContext parentSpace;
	public V3SpaceContext getParentSpace() {
		return parentSpace;
	}
	public void setParentSpace(V3SpaceContext parentSpace) {
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

	private SpaceCategoryContext spaceCategory;
	public SpaceCategoryContext getSpaceCategory() {
		return spaceCategory;
	}
	public void setSpaceCategory(SpaceCategoryContext spaceCategory) {
		this.spaceCategory = spaceCategory;
	}

	private Boolean reservable;
	public Boolean getReservable() {
		return reservable;
	}
	public void setReservable(Boolean reservable) {
		this.reservable = reservable;
	}
	public void setReservable(boolean reservable) {
		this.reservable = reservable;
	}
	public boolean isReservable() {
		if (reservable != null) {
			return reservable.booleanValue();
		}
		return false;
	}

	private float unitReservationCost = -1;
	public float getUnitReservationCost() {
		return unitReservationCost;
	}
	public void setUnitReservationCost(float unitReservationCost) {
		this.unitReservationCost = unitReservationCost;
	}
	
	private long noOfSubSpaces = -1;
	public long getNoOfSubSpaces() {
		return noOfSubSpaces;
	}
	public void setNoOfSubSpaces(long noOfSubSpaces) {
		this.noOfSubSpaces = noOfSubSpaces;
	}
	
	private LocationContext location;
	public LocationContext getLocation() {
        return location;
    }

    public void setLocation(LocationContext location) {
        this.location = location;
    }

}
