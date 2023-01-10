package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;

public class AddPeopleAccessCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
	   List<PeopleContext> people = (List<PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
	   Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
	   Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
	   Boolean verifyUser = (Boolean) context.get(FacilioConstants.ContextNames.VERIFY_USER);
	   if (verifyUser == null) {
	   		verifyUser = false;
	   }

		if (CollectionUtils.isNotEmpty(people) && MapUtils.isNotEmpty(changeSet)) {
			for (PeopleContext ppl : people) {
				List<UpdateChangeSet> changes = changeSet.get(ppl.getId());
				if ((CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "employeePortalAccess", FacilioConstants.ContextNames.PEOPLE))
						|| (MapUtils.isNotEmpty(ppl.getRolesMap()) && ppl.getRolesMap().get(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP) != null)) {
					PeopleAPI.updatePeoplePortalAccess(ppl, FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
				}
				if ((CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isOccupantPortalAccess", FacilioConstants.ContextNames.PEOPLE))
						|| (MapUtils.isNotEmpty(ppl.getRolesMap()) && ppl.getRolesMap().get(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP) != null)) {
					PeopleAPI.updatePeoplePortalAccess(ppl, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
				}
			}
		}
		return false;
	}

}
