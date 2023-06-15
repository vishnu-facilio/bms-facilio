package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateCancelBookingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        String moduleName = Constants.getModuleName(context);
        if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("skipCancelBookingValidation"))
        {
            if ((Boolean) bodyParams.get("skipCancelBookingValidation") == true && !bodyParams.containsKey("cancel")) {
            return false;
            }
        }
        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("cancelBooking")) {
            if ((Boolean) bodyParams.get("cancelBooking") == true && !bodyParams.containsKey("cancel")) {
                Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
                List<V3FacilityBookingContext> bookings = recordMap.get(moduleName);
                Map<Long, FacilityContext> facilities = new HashMap<>();
                if(CollectionUtils.isNotEmpty(bookings)) {
                    for(V3FacilityBookingContext booking : bookings){
                        FacilityContext facility = null;
                        if(!facilities.containsKey(booking.getFacility().getId())){
                            facility = (FacilityContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY, booking.getFacility().getId(), FacilityContext.class);
                            facilities.put(facility.getId(), facility);
                        }
                        else {
                            facility = facilities.get(booking.getFacility().getId());
                        }
                        if(facility != null && facility.getAllowCancellationBefore() != null && facility.getAllowCancellationBefore() > 0){
                            Long cancellationAllowedTill = DateTimeUtil.minusDays(booking.getBookingDate(), facility.getAllowCancellationBefore().intValue());
                            if(DateTimeUtil.getDaysBetween(System.currentTimeMillis(), cancellationAllowedTill) < 0) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cancellation of this booking is permitted until "+ DateTimeUtil.getFormattedTime(cancellationAllowedTill, "dd/MM/yyyy"));
                            }
                        }
                    }
                }
            }
        }

                return false;
    }
}
