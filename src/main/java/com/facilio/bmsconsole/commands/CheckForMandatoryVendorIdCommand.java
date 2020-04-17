package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.constants.FacilioConstants;

public class CheckForMandatoryVendorIdCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VendorContactContext> vendorContacts = (List<VendorContactContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(vendorContacts)) {
			for(VendorContactContext vc : vendorContacts) {
				vc.setPeopleType(PeopleType.VENDOR_CONTACT);
				if(vc.getVendor() == null || vc.getVendor().getId() <=0 ) {
					throw new IllegalArgumentException("Vendor Contact must have a vendor id associated");
				}
			}
		}
		return false;
	}

}
