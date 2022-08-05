package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.context.Break;
import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;


public class ValidateBreakCommand extends FacilioCommand {

    private boolean emptyBreakNameBreach(Break br) {
        return StringUtils.isEmpty(br.getName());
    }

    private boolean emptyBreakTimeBreach(Break br) {
        return br.getBreakTime() == null || br.getBreakTime() == 0;
    }

    private boolean emptyShiftAssociationBreach(Break br) {
        return br.getShifts() == null || br.getShifts().size() == 0;
    }

    private boolean emptyModeBreach(Break br) {
        return br.getBreakMode() == -1;
    }

    private boolean emptyTypeBreach(Break br) {
        return br.getBreakType() == -1;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
        List<Break> breaks = recordMap.get(moduleName);

        if (FacilioUtil.isEmptyOrNull(breaks)) {
            return false;
        }

        for (Break br : breaks) {
            if (emptyBreakNameBreach(br)) {
                throw new IllegalArgumentException("Name is mandatory");
            }
            if (emptyBreakTimeBreach(br)) {
                throw new IllegalArgumentException("Time cannot be zero");
            }
            if (emptyShiftAssociationBreach(br)) {
                throw new IllegalArgumentException("Applicable shifts cannot be empty");
            }
            if (emptyModeBreach(br)) {
                throw new IllegalArgumentException("Mode cannot be empty");
            }
            if (emptyTypeBreach(br)) {
                throw new IllegalArgumentException("Type cannot be empty");
            }
        }
        return false;
    }

}
