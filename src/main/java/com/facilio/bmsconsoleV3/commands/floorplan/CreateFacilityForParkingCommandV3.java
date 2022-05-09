package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.HashMap;
import java.util.List;

import com.facilio.bmsconsoleV3.context.V3ParkingStallContext;
import com.facilio.bmsconsoleV3.util.ParkingAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;

public class CreateFacilityForParkingCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		HashMap record = (HashMap)context.get("recordMap");
		
		List<V3ParkingStallContext> parkings = (List<V3ParkingStallContext>)record.get(FacilioConstants.ContextNames.Floorplan.PARKING);
		
		if (parkings != null && !parkings.isEmpty()) {
			for (V3ParkingStallContext parking : parkings) {
				ParkingAPI.AddorDeleteFacilityForParkings(parking);
			}
			
		}
		
		return false;
	}
	
}