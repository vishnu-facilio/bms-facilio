package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.bmsconsoleV3.util.V3SpaceBookingApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class FetchSpaceBookingCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> spaceIds = (List<Long>) context.get(FacilioConstants.ContextNames.SPACE_LIST);
        long startTime =  (long) context.get(FacilioConstants.ContextNames.START_TIME);
        long endTime =  (long) context.get(FacilioConstants.ContextNames.END_TIME);

        List<V3SpaceBookingContext> bookingList = V3SpaceBookingApi.getActiveBookingListFromSpaceIds(spaceIds, startTime, endTime);

        context.put(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_LIST,bookingList);

        Map<Long, List<V3SpaceBookingContext>> spaceMap = new JSONObject();


        bookingList.forEach(booking -> {
            List<V3SpaceBookingContext> spaceBookingList = new ArrayList<>();

            if (booking.getSpace() != null) {
               if (spaceMap.get(booking.getSpace().getId()) != null) {
                   spaceBookingList = spaceMap.get(booking.getSpace().getId());
                   spaceBookingList.add(booking);
                   spaceMap.put(booking.getSpace().getId(), spaceBookingList);
               }
               else {
                   spaceMap.put(booking.getSpace().getId(), Collections.singletonList(booking));
               }

           }
        });

        context.put(FacilioConstants.ContextNames.Floorplan.SPACE_BOOKING_MAP, spaceMap);


        return false;
    }
}
