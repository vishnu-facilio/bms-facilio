package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.context.shift.Break;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class ValidateBreakUsageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> breakIDs = (List<Long>) context.get("recordIds");

        if (FacilioUtil.isEmptyOrNull(breakIDs)) {
            return false;
        }

        for (Long br: breakIDs) {
            if (hasAssociatedShift(br)){
                throw new IllegalArgumentException("Break has active shifts associated");
            }
        }

        return false;
    }

    private boolean hasAssociatedShift(Long breakID) throws Exception {
        return ShiftAPI.getAssociatedShiftCount(breakID) > 0;
    }
}
