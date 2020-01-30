package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.constants.FacilioConstants;

public class ComputeScheduleForWorkPermitCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkPermitContext> workPermitList = (List<WorkPermitContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		if(CollectionUtils.isNotEmpty(workPermitList)) {
			for(WorkPermitContext permit : workPermitList) {
				if(permit.getRequestedBy() != null) {
					ContactsContext contact = ContactsAPI.getContactsIdForUser(permit.getRequestedBy().getId());
					if(contact != null && contact.getTenant() != null) {
						permit.setTenant(contact.getTenant());
					}
				}
				else {
					permit.setRequestedBy(AccountUtil.getCurrentUser());
				}
				BusinessHoursContext visitTime = permit.getRecurringInfo();
				if(permit.isRecurring() && visitTime != null) {
					if(visitTime.getId() > 0) {
						BusinessHoursAPI.updateBusinessHours(visitTime);
						BusinessHoursAPI.deleteSingleBusinessHour(visitTime.getId());
						BusinessHoursAPI.addSingleBusinessHours(visitTime);
					}
					else {
						long id = BusinessHoursAPI.addBusinessHours(permit.getRecurringInfo());
						permit.setRecurringInfoId(id);
					}
				}
			}
		}
		return false;
	}

}
