package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class UpdatePeoplePrimaryContactCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PeopleContext> peopleList = (List<PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(peopleList)) {
			for(PeopleContext people : peopleList) {
				if(people.getPeopleTypeEnum() == PeopleType.TENANT_CONTACT && ((TenantContactContext)people).isPrimaryContact()) {
					PeopleAPI.unMarkPrimaryContact(people, ((TenantContactContext)people).getTenant().getId());
				}
				else if(people.getPeopleTypeEnum() == PeopleType.VENDOR_CONTACT && ((VendorContactContext)people).isPrimaryContact()) {
					PeopleAPI.unMarkPrimaryContact(people, ((VendorContactContext)people).getVendor().getId());
				}
				else if(people.getPeopleTypeEnum() == PeopleType.CLIENT_CONTACT && ((ClientContactContext)people).isPrimaryContact()) {
					PeopleAPI.unMarkPrimaryContact(people, ((ClientContactContext)people).getClient().getId());
				}
			}
		}
		return false;
	}

}
