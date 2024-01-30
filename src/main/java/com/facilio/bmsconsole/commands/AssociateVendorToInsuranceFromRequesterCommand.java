package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.InsuranceAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AssociateVendorToInsuranceFromRequesterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<InsuranceContext> insurances = (List<InsuranceContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(insurances)) {
			for(InsuranceContext ins : insurances) {
				if(ins.getAddedBy() != null && ins.getAddedBy().getId() > 0 && ins.getVendor() == null) {
						long pplId = PeopleAPI.getPeopleIdForUser(ins.getAddedBy().getOuid());
						if(pplId > 0) {
							VendorContactContext people = (VendorContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, pplId);
							if(people != null) {
								ins.setVendor(people.getVendor());
							}
						}

				}
				if(ins.getVendor() != null) {
					InsuranceAPI.updateVendorRollUp(ins.getVendor().getId());
				}
			}
		}
		return false;
	}

}
