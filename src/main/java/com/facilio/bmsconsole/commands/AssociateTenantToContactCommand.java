package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class AssociateTenantToContactCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Boolean isTenantPortal = (Boolean)context.getOrDefault(FacilioConstants.ContextNames.IS_TENANT_PORTAL, false);
		if(isTenantPortal) {
			long currentUserId = AccountUtil.getCurrentUser().getOuid();
			if(AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE_CONTACTS)) {
				TenantContext tenant = PeopleAPI.getTenantForUser(currentUserId);
				context.put(FacilioConstants.ContextNames.ID, tenant.getId());
			}
			else {
				ContactsContext userContact = ContactsAPI.getContactsIdForUser(currentUserId);
				if(userContact != null) {
					if(userContact.getContactType() == ContactsContext.ContactType.TENANT.getIndex() && userContact.getTenant() != null) {
						context.put(FacilioConstants.ContextNames.ID, userContact.getTenant().getId());
					}
				}
			}
		}
		return false;
	}

}
