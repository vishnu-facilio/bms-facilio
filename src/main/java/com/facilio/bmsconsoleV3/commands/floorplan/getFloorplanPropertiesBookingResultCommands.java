package com.facilio.bmsconsoleV3.commands.floorplan;



import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;


import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;



public class getFloorplanPropertiesBookingResultCommands extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		

		JSONObject properties = (JSONObject) context.get(FacilioConstants.ContextNames.Floorplan.PROPERTIES);

		properties.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY, context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY));
		properties.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING));
		properties.put("bookingMap", context.get("bookingMap"));
		return false;
	}


}