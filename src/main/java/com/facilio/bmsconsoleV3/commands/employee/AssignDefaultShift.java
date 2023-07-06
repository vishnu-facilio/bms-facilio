package com.facilio.bmsconsoleV3.commands.employee;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AssignDefaultShift extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> records = FieldUtil.getAsBeanListFromMapList(recordMap.get(moduleName),V3PeopleContext.class);
        for (V3PeopleContext record : records) {
            ShiftAPI.assignDefaultShiftToEmployee(record.getId());
        }
        return false;
    }
}
