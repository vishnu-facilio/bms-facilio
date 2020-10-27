package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateFacilityBookingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3FacilityBookingContext> bookings = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(bookings)) {
            for(V3FacilityBookingContext booking : bookings) {
                if(booking.getFacility() != null && booking.getFacility().getId() > 0){
                    FacilityContext facility = (FacilityContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY, booking.getFacility().getId(), FacilityContext.class);
                    if(facility.isMultiBookingPerSlotAllowed()) {
                        List<V3FacilityBookingContext> bookingsInTheSlot = FacilityAPI.getBookingsPerSlot(booking.getScheduledStartTime(), booking.getScheduledEndTime(), true);
                        if(CollectionUtils.isNotEmpty(bookingsInTheSlot) && (bookingsInTheSlot.size() + booking.getNoOfAttendees()) > facility.getMaxMultiBookingPerSlot()){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The current booking count exceeds the permitted bookings for the slot");
                        }
                    }
                }
                else {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Facility is mandatory for a booking");
                }
            }
        }

        return false;
    }
}
