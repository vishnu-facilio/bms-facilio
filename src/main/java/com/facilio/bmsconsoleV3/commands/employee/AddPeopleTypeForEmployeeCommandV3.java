package com.facilio.bmsconsoleV3.commands.employee;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddPeopleTypeForEmployeeCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3EmployeeContext> employees = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(employees)) {
            for(V3EmployeeContext emp : employees) {
                emp.setPeopleType(V3PeopleContext.PeopleType.EMPLOYEE.getIndex());
            }
        }
        return false;
    }
}
