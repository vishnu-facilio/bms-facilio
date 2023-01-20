package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;

public class UpdateEmployeePeopleAppPortalAccessCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<EmployeeContext> employees = (List<EmployeeContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
		if(CollectionUtils.isNotEmpty(employees) && MapUtils.isNotEmpty(changeSet)) {
			for(EmployeeContext emp : employees) {
				List<UpdateChangeSet> changes = changeSet.get(emp.getId());
				if((CollectionUtils.isNotEmpty(changes) && (RecordAPI.checkChangeSet(changes, "isAppAccess", FacilioConstants.ContextNames.EMPLOYEE) || RecordAPI.checkChangeSet(changes, "roleId", FacilioConstants.ContextNames.EMPLOYEE))) || (MapUtils.isNotEmpty(emp.getRolesMap()) && emp.getRolesMap().get(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) != null)) {
					PeopleAPI.updateEmployeeAppPortalAccess(emp, FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
				}
				if((CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isOccupantPortalAccess", FacilioConstants.ContextNames.EMPLOYEE)) || (MapUtils.isNotEmpty(emp.getRolesMap()) && emp.getRolesMap().get(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP) != null)) {
					PeopleAPI.updatePeoplePortalAccess(emp, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
				}
				if((CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "employeePortalAccess", FacilioConstants.ContextNames.EMPLOYEE)) || (MapUtils.isNotEmpty(emp.getRolesMap()) && emp.getRolesMap().get(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP) != null)) {
					PeopleAPI.updatePeoplePortalAccess(emp, FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
				}
			}
		}
		return false;
	}

}
