package com.facilio.bmsconsoleV3.commands.employee;

import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AssignDefaultShift extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3EmployeeContext> employees = recordMap.get(moduleName);
        for (V3EmployeeContext emp : employees) {
            ShiftAPI.assignDefaultShiftToEmployee(emp.getId());
        }
        return false;
    }
}
