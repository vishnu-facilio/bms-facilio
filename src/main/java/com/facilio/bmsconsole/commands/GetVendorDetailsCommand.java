package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.constants.FacilioConstants;

public class GetVendorDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			VendorContext vendor = (VendorContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (vendor != null && vendor.getId() > 0) {
				if (vendor.getAddress() != null && vendor.getAddress().getId() != -1) {
					Map<Long, LocationContext> spaceMap = LocationAPI
							.getLocationMap(Collections.singleton(vendor.getAddress().getId()));
					vendor.setAddress(spaceMap.get(vendor.getAddress().getId()));
				}
				vendor.setVendorContacts(ContactsAPI.getVendorContacts(vendor.getId()));
			}
			context.put(FacilioConstants.ContextNames.VENDOR, vendor);
		}
		return false;
	}

}
