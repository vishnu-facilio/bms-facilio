package com.facilio.bmsconsoleV3.context.floorplan;

import com.facilio.bmsconsole.context.SpaceContext;
public class V3MarkerdZonesContext  extends V3IndoorFloorPlanObjectContext {

    public SpaceContext getSpace() {
		return space;
	}

	public void setSpace(SpaceContext space) {
		this.space = space;
	}

	private Boolean isReservable;

	private SpaceContext space;


    /**
     * @return Boolean return the isReservable
     */
    public Boolean isIsReservable() {
        return isReservable;
    }

    /**
     * @param isReservable the isReservable to set
     */
    public void setIsReservable(Boolean isReservable) {
        this.isReservable = isReservable;
    }

}