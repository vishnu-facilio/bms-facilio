package com.facilio.bmsconsoleV3.context.floorplan;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.v3.context.V3Context;
public class V3MarkerdZonesContext  extends V3IndoorFloorPlanObjectContext {

    public SpaceContext getSpace() {
		return space;
	}

	public void setSpace(SpaceContext space) {
		this.space = space;
	}

	private SpaceContext space;

}