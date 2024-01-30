package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;

public class AddTenantFilterForVisitsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		//This class can be removed once new tenant portal is released
		Boolean isFromTenantPortal = (Boolean) context.get(FacilioConstants.ContextNames.IS_TENANT_PORTAL);
		if (isFromTenantPortal) {
			String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
			Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
			if (filterCriteria == null) {
				filterCriteria = new Criteria();
			}
			//need to change this after people contacts is released and host field is changed in visitor mgmt 
			//this is temp fix
			ContactsContext contact = null;
				//need to uncomment when all users are migrated as employees
				long pplId = PeopleAPI.getPeopleIdForUser(AccountUtil.getCurrentUser().getOuid());
				if(pplId > 0) {
					TenantContactContext people = (TenantContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, pplId);
					if(people != null) {
						contact = ContactsAPI.getContact(people.getEmail(), false);
					}
				}


			if (contact != null) {
				filterCriteria.addAndCondition(
						CriteriaAPI.getCondition("HOST", "host", String.valueOf(contact.getId()), PickListOperators.IS));
				if (viewName.equals("myPendingVisits")) {
					filterCriteria.addAndCondition(CriteriaAPI.getCondition("CHECKIN_TIME", "checkInTime", "-1",
							CommonOperators.IS_EMPTY));
				} else {
					filterCriteria.addAndCondition(CriteriaAPI.getCondition("CHECKIN_TIME", "checkInTime", "-1",
							CommonOperators.IS_NOT_EMPTY));
				}
			}
			context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, filterCriteria);
		}
		return false;
	}

}
