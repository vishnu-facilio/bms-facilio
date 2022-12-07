package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.bmsconsoleV3.util.V3SpaceBookingApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class SpaceBookingCancelCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String action = (String) context.get(FacilioConstants.ContextNames.ACTION);
        List<V3SpaceBookingContext> bookingList = (List<V3SpaceBookingContext>)context.get("spacebookingList");
        if(action.equals(FacilioConstants.ContextNames.SpaceBooking.CANCEL))
        {
            V3SpaceBookingApi.updateSpaceBookingCancelState(bookingList,"cancelled");
        }
        return false;
    }
}
