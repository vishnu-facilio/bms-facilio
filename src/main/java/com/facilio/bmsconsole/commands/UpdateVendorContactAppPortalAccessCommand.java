package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateVendorContactAppPortalAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VendorContactContext> vendorContacts = (List<VendorContactContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Integer accessChangeFor = (Integer)context.getOrDefault(FacilioConstants.ContextNames.ACCESS_NEEDED_FOR, 0);
		Long appId = (Long)context.get(FacilioConstants.ContextNames.APP_ID);
		
		if(CollectionUtils.isNotEmpty(vendorContacts)) {
			if(accessChangeFor == 1) {
				for(VendorContactContext vc : vendorContacts) {
					PeopleAPI.updateVendorContactAppPortalAccess(vc, AppDomainType.VENDOR_PORTAL, appId);
				}
			}
		}
		return false;
	}

}
