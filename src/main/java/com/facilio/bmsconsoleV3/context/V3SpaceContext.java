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

	private Long spaceCategoryId;
	public Long getSpaceCategoryId() {
		if (spaceCategoryId == null) {
			if (spaceCategory != null) {
				return spaceCategory.getId();
			}
			return null;
		}
		return this.spaceCategoryId;
	}

	public void setSpaceCategoryId(Long spaceCategoryId) {
		this.spaceCategoryId = spaceCategoryId;
		if (spaceCategory == null) {
			this.spaceCategory = new SpaceCategoryContext();
		}
		this.spaceCategory.setId(spaceCategoryId);
	}

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

	private Float unitReservationCost;
	public Float getUnitReservationCost() {
		return unitReservationCost;
	}
	public void setUnitReservationCost(Float unitReservationCost) {
		this.unitReservationCost = unitReservationCost;
	}
	
	private Long noOfSubSpaces;
	public Long getNoOfSubSpaces() {
		return noOfSubSpaces;
	}
	public void setNoOfSubSpaces(Long noOfSubSpaces) {
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
