package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.constants.FacilioConstants;

public class AssociateVendorToInsuranceFromRequesterCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<InsuranceContext> insurances = (List<InsuranceContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(insurances)) {
			for(InsuranceContext ins : insurances) {
				if(ins.getAddedBy() != null && ins.getAddedBy().getId() > 0) {
					ContactsContext contact = ContactsAPI.getContactsIdForUser(ins.getAddedBy().getId());
					ins.setVendor(contact.getVendor());
				}
			}
		}
		return false;
	}

}
