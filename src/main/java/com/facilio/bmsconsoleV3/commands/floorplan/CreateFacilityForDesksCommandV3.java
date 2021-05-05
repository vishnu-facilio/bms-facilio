package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.commands.facility.GenerateSlotCommand;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.util.DesksAPI;

public class CreateFacilityForDesksCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		HashMap record = (HashMap)context.get("recordMap");
		
		List<V3DeskContext> desks = (List<V3DeskContext>)record.get("desks");
		
		if (desks != null && !desks.isEmpty()) {
			for (V3DeskContext desk : desks) {
				DesksAPI.AddorDeleteFacilityForDesks(desk);
			}
			
		}
		
		return false;
	}
	
}