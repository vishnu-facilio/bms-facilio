package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;

public class UpdateEmployeePeopleAppPortalAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<EmployeeContext> employees = (List<EmployeeContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Long appId = (Long)context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);
		Long occupantPortalId = (Long)context.getOrDefault(FacilioConstants.ContextNames.SERVICE_PORTAL_APP_ID, -1l);
		
		if(CollectionUtils.isNotEmpty(employees)) {
			for(EmployeeContext emp : employees) {
				PeopleAPI.updateEmployeeAppPortalAccess(emp, AppDomainType.FACILIO, appId);
				PeopleAPI.updatePeoplePortalAccess(emp, AppDomainType.SERVICE_PORTAL, occupantPortalId);
			}
		}
		return false;
	}

}
