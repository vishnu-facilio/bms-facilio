package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.facilio.v3.context.Constants;

public class ValidateBookingCancelCommand extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        Context vcontext = context;
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
        List<V3SpaceBookingContext> bookings=recordMap.get(moduleName);
        for(V3SpaceBookingContext booking: bookings)
        {
            String status= booking.getModuleState().getStatus();
            List<String> notShow= Arrays.asList("cancelled","checkedIn","checkedOut","expired","autocheckedOut");
            if(!notShow.contains(status) && (booking.getHost() != null && booking.getReservedBy() != null) && (AccountUtil.getCurrentUser().getId() == booking.getSysCreatedBy().getId() || PeopleAPI.getPeople(AccountUtil.getCurrentUser().getEmail()).getId() == booking.getHost().getId() || PeopleAPI.getPeople(AccountUtil.getCurrentUser().getEmail()).getId() == booking.getReservedBy().getId()))
            {
                booking.setShowCancel(true);
            }
        }

        return false;
    }
}
