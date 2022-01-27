package com.facilio.weekends;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class ValidateWeekendCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WeekendContext weekend = (WeekendContext) context.get(FacilioConstants.ContextNames.WEEKEND);
        if(weekend == null)
        {
            throw new IllegalArgumentException("Invalid weekend data");
        }
        else if(StringUtils.isEmpty(weekend.getName()))
        {
            throw new IllegalArgumentException("Weekend name cannot be empty");
        }
        else if(StringUtils.isEmpty(weekend.getValue()))
        {
            throw new IllegalArgumentException("Weekend value cannot be empty");
        }
        return false;
    }
}
