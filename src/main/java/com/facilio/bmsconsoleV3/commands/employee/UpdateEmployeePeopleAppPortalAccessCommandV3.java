package com.facilio.bmsconsoleV3.commands.employee;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class UpdateEmployeePeopleAppPortalAccessCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3EmployeeContext> employees = recordMap.get(moduleName);
        Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);

        if(CollectionUtils.isNotEmpty(employees) && MapUtils.isNotEmpty(changeSet)) {
            for(V3EmployeeContext emp : employees) {
                List<UpdateChangeSet> changes = changeSet.get(emp.getId());
                if((CollectionUtils.isNotEmpty(changes) && (RecordAPI.checkChangeSet(changes, "isAppAccess", FacilioConstants.ContextNames.EMPLOYEE) || RecordAPI.checkChangeSet(changes, "roleId", FacilioConstants.ContextNames.EMPLOYEE))) || (MapUtils.isNotEmpty(emp.getRolesMap()) && emp.getRolesMap().get(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) != null)) {
                    V3PeopleAPI.updateEmployeeAppPortalAccess(emp, FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                }
                if((CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isOccupantPortalAccess", FacilioConstants.ContextNames.EMPLOYEE)) || (MapUtils.isNotEmpty(emp.getRolesMap())  && emp.getRolesMap().get(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP) != null)) {
                    V3PeopleAPI.updatePeoplePortalAccess(emp, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
                }
            }
        }
        return false;
    }
}
