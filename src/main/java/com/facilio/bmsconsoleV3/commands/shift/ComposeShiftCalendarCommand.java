package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComposeShiftCalendarCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long rangeFrom = (Long) context.get(FacilioConstants.Shift.RANGE_FROM);
        Long rangeTo = (Long) context.get(FacilioConstants.Shift.RANGE_TO);

        List<V3EmployeeContext> employees = (List<V3EmployeeContext>)
                context.get(FacilioConstants.ContextNames.EMPLOYEES);
        List<Map<String, Object>> employeesWithWhiteListedFields = stripEmployeeFields(employees);

        Map<Long, List<Map<String, Object>>> employeeShiftMapping = new HashMap<>();

        for (V3EmployeeContext emp: employees) {
            List<Map<String, Object>> shifts = ShiftAPI.getCompactedShiftList(emp.getId(), rangeFrom, rangeTo);
            employeeShiftMapping.put(emp.getId(), shifts);
        }

        context.put(FacilioConstants.ContextNames.SHIFTS, employeeShiftMapping);
        context.put(FacilioConstants.ContextNames.EMPLOYEES, employeesWithWhiteListedFields);

        return false;
    }

    private List<Map<String, Object>> stripEmployeeFields(List<V3EmployeeContext> employees) {
        List<Map<String, Object>> strippedEmployees = new ArrayList<>();
        for (V3EmployeeContext e: employees) {
            HashMap<String, Object> strippedE = new HashMap<>();
            strippedE.put("id", e.getId());
            strippedE.put("name", e.getName());
            strippedEmployees.add(strippedE);
        }
        return strippedEmployees;
    }
}