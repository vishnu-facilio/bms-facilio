package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.context.Shift;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class MarkAsNonDefaultShiftCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<Shift> shifts = recordMap.get(moduleName);
        for (Shift s : shifts) {
            s.setIsDefaultShift(false);
        }
        return false;
    }
}
