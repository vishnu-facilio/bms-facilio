package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class UpdateEmployeePortalAccessCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<EmployeeContext> employees = (List<EmployeeContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
        Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
        if(CollectionUtils.isNotEmpty(employees) && MapUtils.isNotEmpty(changeSet)) {
            for(EmployeeContext emp : employees) {
                List<UpdateChangeSet> changes = changeSet.get(emp.getId());
                if((CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "employeePortalAccess", FacilioConstants.ContextNames.EMPLOYEE)) || MapUtils.isNotEmpty(emp.getRolesMap())) {
                    PeopleAPI.updatePeoplePortalAccess(emp, FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
                }
            }
        }
        return false;
    }

}
