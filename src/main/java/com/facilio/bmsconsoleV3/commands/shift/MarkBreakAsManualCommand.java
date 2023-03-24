package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.context.shift.Break;
import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class MarkBreakAsManualCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
        List<Break> breaks = recordMap.get(moduleName);

        if (FacilioUtil.isEmptyOrNull(breaks)) {
            return false;
        }
        for (Break br : breaks) {
           br.setBreakMode(Break.Mode.MANUAL);
        }
        return false;
    }
}
