package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class AddDefaultShiftToEmployeeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<EmployeeContext> employees = (List<EmployeeContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
        Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
        if(CollectionUtils.isNotEmpty(employees) && MapUtils.isNotEmpty(changeSet)) {
            for(EmployeeContext emp : employees) {
				ShiftAPI.assignDefaultShiftToEmployee(emp.getId());
            }
        }
        return false;
    }
}
