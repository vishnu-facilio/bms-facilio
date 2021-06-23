package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;

public class AddTenantScopeForVisitorLoggingListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Boolean isTenantPortal = (Boolean)context.getOrDefault(FacilioConstants.ContextNames.IS_TENANT_PORTAL, false);
		if(isTenantPortal != null && isTenantPortal) {
			long currentUserId = AccountUtil.getCurrentUser().getOuid();
			ContactsContext userContact = ContactsAPI.getContactsIdForUser(currentUserId);
			if(userContact != null) {
				if(userContact.getContactType() == ContactsContext.ContactType.TENANT.getIndex()) {
					Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
					Condition condition = CriteriaAPI.getCondition("HOST", "host", String.valueOf(userContact.getId()), NumberOperators.EQUALS);
					if(filterCriteria != null) {
						filterCriteria.addAndCondition(condition);
					}
					else {
						filterCriteria = new Criteria();
						filterCriteria.addAndCondition(condition);
					}
					context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, filterCriteria);
					
				}
				
			}
		}
		return false;
	}

}
