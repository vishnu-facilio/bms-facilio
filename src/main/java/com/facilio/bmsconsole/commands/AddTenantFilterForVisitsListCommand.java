package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;

public class AddTenantFilterForVisitsListCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Boolean isFromTenantPortal = (Boolean) context.get(FacilioConstants.ContextNames.IS_TENANT_PORTAL);
		if(isFromTenantPortal) {
			Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
			ContactsContext contact = ContactsAPI.getContactsIdForUser(AccountUtil.getCurrentUser().getOuid());
			if(contact!=null) {
				filterCriteria.addAndCondition(CriteriaAPI.getCondition("HOST", "host", contact.getId()+"", PickListOperators.IS));
			}
			context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, filterCriteria);
		}
		return false;
	}

}
