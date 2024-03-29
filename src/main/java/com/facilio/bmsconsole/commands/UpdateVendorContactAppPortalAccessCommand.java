package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;

public class UpdateVendorContactAppPortalAccessCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VendorContactContext> vendorContacts = (List<VendorContactContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
		if(CollectionUtils.isNotEmpty(vendorContacts) && MapUtils.isNotEmpty(changeSet)) {
			for(VendorContactContext vc : vendorContacts) {
				List<UpdateChangeSet> changes = changeSet.get(vc.getId());
				if((CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isVendorPortalAccess", FacilioConstants.ContextNames.VENDOR_CONTACT)) || MapUtils.isNotEmpty(vc.getRolesMap())) {
					PeopleAPI.updateVendorContactAppPortalAccess(vc, FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
				}
			}
		}
		return false;
	}

}
