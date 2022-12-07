package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class ExtendSpaceBookingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String action = (String) context.get(FacilioConstants.ContextNames.ACTION);
        Long extendTime = (Long) context.get(FacilioConstants.ContextNames.SpaceBooking.EXTEND_TIME);
        List<V3SpaceBookingContext> bookingList = (List<V3SpaceBookingContext>)context.get("spacebookingList");
        if(action.equals(FacilioConstants.ContextNames.SpaceBooking.EXTEND)) {
            for(V3SpaceBookingContext booking : bookingList)
            {

            }

        }
    return false;
    }
}
