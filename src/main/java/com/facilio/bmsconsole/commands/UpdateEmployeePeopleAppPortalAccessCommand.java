package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateEmployeePeopleAppPortalAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<EmployeeContext> employees = (List<EmployeeContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Integer accessChangeFor = (Integer)context.get(FacilioConstants.ContextNames.ACCESS_NEEDED_FOR);
		Long appId = (Long)context.get(FacilioConstants.ContextNames.APP_ID);
		
		if(CollectionUtils.isNotEmpty(employees)) {
			if(accessChangeFor == 1) {
				for(EmployeeContext emp : employees) {
					if(emp.isAppAccess() && emp.getRoleId() <= 0) {
						throw new IllegalArgumentException("Role cannot be null");
					}
					PeopleAPI.updateEmployeeAppPortalAccess(emp, AppDomainType.FACILIO, appId);
				}
			}
			else if(accessChangeFor == 2){
				for(EmployeeContext emp : employees) {
					PeopleAPI.updateEmployeeAppPortalAccess(emp, AppDomainType.SERVICE_PORTAL, appId);
				}
			}
		}
		return false;
	}

}
