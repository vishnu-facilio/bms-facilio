package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

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
                Map<String, List<Map<String, Object>>> subformMap = booking.getSubForm();
                if(booking.getFacility() == null && booking.getFacility().getId() <= 0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Facility is mandatory for a booking");
                }
                if(MapUtils.isEmpty(subformMap) || subformMap.containsKey(FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Slot is mandatory for a booking");
                }
                FacilityContext facility = (FacilityContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY, booking.getFacility().getId(), FacilityContext.class);
                List<BookingSlotsContext> slotList = FieldUtil.getAsBeanListFromMapList(subformMap.get(FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS), BookingSlotsContext.class);
                if(facility.getMaxSlotBookingAllowed() != null && slotList.size() > facility.getMaxSlotBookingAllowed()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Max slots booking allowed is " + facility.getMaxSlotBookingAllowed());
                }
                for(BookingSlotsContext bookingSlot : slotList) {
                    if(bookingSlot == null){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Slot is mandatory for a booking");
                    }
                    SlotContext slot = (SlotContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.SLOTS, bookingSlot.getSlot().getId(), SlotContext.class);
                    if(slot != null) {
                        if(booking.getNoOfAttendees() != null && facility.getUsageCapacity() != null && booking.getNoOfAttendees() > (facility.getUsageCapacity() - slot.getBookingCount())) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The current booking count exceeds the permitted bookings for the slot");
                        }
                        if(slot.getBookingCount() != null && !facility.isMultiBookingPerSlotAllowed()){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Parallel booking in a slot is not allowed for this facility");
                        }
                    }
                }

            }
        }

        return false;
    }
}
