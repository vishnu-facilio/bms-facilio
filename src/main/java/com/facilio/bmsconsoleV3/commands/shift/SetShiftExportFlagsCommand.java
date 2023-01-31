package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class SetShiftExportFlagsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        validateProps(context);
        Long employeeID = (Long) context.get(FacilioConstants.Shift.EMPLOYEE_ID);
        // if employeeID is given, export is for list and not calendar;
        if (employeeID != null && employeeID > 0){
            context.put(FacilioConstants.Shift.EXPORT_LIST, true);
        }
        return false;
    }

    private void validateProps(Context context) {
        Long rangeFrom = (Long) context.get(FacilioConstants.Shift.RANGE_FROM);
        if (rangeFrom == null || rangeFrom <= 0){
            throw new IllegalArgumentException("rangeFrom is a mandatory prop");
        }

        Long rangeTo = (Long) context.get(FacilioConstants.Shift.RANGE_TO);
        if (rangeTo == null || rangeTo <= 0){
            throw new IllegalArgumentException("rangeTo is a mandatory prop");
        }

        String format = (String) context.get(FacilioConstants.Shift.FORMAT);
        if (StringUtils.isEmpty(format)){
            throw new IllegalArgumentException("format is a mandatory prop");
        }
    }
}
