package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilitySpecialAvailabilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddSpecialAvailabilitySlotsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FacilitySpecialAvailabilityContext> splAvailabilities = recordMap.get(moduleName);
        List<SlotContext> slots = new ArrayList<>();
        Long canceledBookingCount = 0L;
        if (CollectionUtils.isNotEmpty(splAvailabilities)) {
            DateTimeFormatter formatter = DateTimeUtil.getDateTimeFormat("dd/MM/YYYY");
            for (FacilitySpecialAvailabilityContext splAvailability : splAvailabilities) {
                Boolean hasExecuted = false;
                FacilityContext facilityContext =V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY,splAvailability.getFacility().getId(),FacilityContext.class);
                splAvailability.setFacility(facilityContext);
                if (splAvailability.getFacility() != null) {
                    if (splAvailability.getSpecialType() == FacilitySpecialAvailabilityContext.SpecialType.SPECIAL_AVAILABILITY.getIndex()) {
                        slots = FacilityAPI.getAvailabilitySlots(splAvailability.getFacility(),splAvailability.getStartDate(),splAvailability.getEndDate());
                        canceledBookingCount= FacilityAPI.createSpecialAvailabilitySlots(splAvailability,slots);
                    } else if (splAvailability.getSpecialType() == FacilitySpecialAvailabilityContext.SpecialType.SPECIAL_UNAVAILABILITY.getIndex()) {
                        slots = FacilityAPI.getAvailabilitySlots(splAvailability.getFacility(),splAvailability.getStartDate(),splAvailability.getEndDate());
                        canceledBookingCount= FacilityAPI.executeSpecialUnavailability(splAvailability,slots);
                    }
                }
                splAvailability.setBookingCanceledCount(canceledBookingCount);
            }

        }
        return false;
    }
}
