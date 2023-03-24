package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.context.shift.Break;
import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
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

    private boolean emptyShiftAssociationBreach(Break br) throws Exception {
        return CollectionUtils.isEmpty(br.getShifts());
    }

    private boolean emptyModeBreach(Break br) {
        return false;
    }

    private boolean emptyTypeBreach(Break br) {
        return br.getBreakType() == null;
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
            if (emptyTypeBreach(br)) {
                throw new IllegalArgumentException("Type cannot be empty");
            }
        }
        return false;
    }

}
