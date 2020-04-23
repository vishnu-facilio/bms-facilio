package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.floorplan.FloorPlanMode;
import com.facilio.bmsconsole.floorplan.FloorPlanViewContext;
import com.facilio.constants.FacilioConstants;

public class ExecuteFloorPlanWorkflowCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FloorPlanViewContext floorPlanViewContext = (FloorPlanViewContext) context.get(FacilioConstants.ContextNames.FLOORPLAN_VIEW_CONTEXT);
		
		FloorPlanMode fl = FloorPlanMode.getFloorPlanMode(floorPlanViewContext.getViewMode());
		if (fl != null) {
			Object floorPlanData = fl.getResult(floorPlanViewContext);
		
			context.put(FacilioConstants.ContextNames.RESULT, floorPlanData);
		}
		else {
			throw new IllegalArgumentException("Invalid floor plan mode.");
		}
		
		return false;
	}
}
