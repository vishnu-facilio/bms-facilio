package com.facilio.bmsconsoleV3.commands.employee;

import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOldEmployeeRecordMap extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<Long, V3PeopleContext> oldPeopleRecordMap = new HashMap<>();
        List<V3EmployeeContext> employeeList = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(employeeList)) {
            for(V3EmployeeContext employee : employeeList) {
                V3EmployeeContext existingPeople = V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, employee.getId(), V3EmployeeContext.class);
                if(existingPeople!= null){
                    oldPeopleRecordMap.put(employee.getId(),existingPeople);
                }
            }
        }
        context.put(FacilioConstants.ContextNames.OLD_RECORD_MAP,oldPeopleRecordMap);
        return false;
    }
}
