package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsoleV3.context.facilitybooking.WeekDayAvailability;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilitySpecialAvailabilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;

public class ValidateSpecialAvailabilityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FacilitySpecialAvailabilityContext> splAvailabilities = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(splAvailabilities)) {
            DateTimeFormatter formatter = DateTimeUtil.getDateTimeFormat("dd/MM/YYYY");
            for (FacilitySpecialAvailabilityContext splAvailability : splAvailabilities) {
                if (splAvailability.getFacility() != null) {
                    FacilityContext facility = (FacilityContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY, splAvailability.getFacility().getId(), FacilityContext.class);
                    if (facility != null) {
                        if(splAvailability.getEndDate() == null) {
                            splAvailability.setEndDate(FacilityAPI.getEndTimeOfTheDay(splAvailability.getStartDate()));
                        }
                        if(splAvailability.getSpecialType() == null) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Availability Type is a mandatory field");
                        }
                        else if(splAvailability.getEndDate()>FacilityAPI.getEndTimeOfTheDay(splAvailability.getStartDate())){
                            //need to remove this check if we need to support Special Availability for multiple days
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Special Availability is supported for a single day");
                        }
                        if (splAvailability.getStartDate() != null && splAvailability.getStartTime() != null && splAvailability.getEndTime() != null) {
                            if (splAvailability.getActualStartTime() > splAvailability.getActualEndTime()) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please enter valid time.");
                            }
                        } else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please enter all the mandatory fields.");
                        }
                        if(splAvailability.getCost()<0 && splAvailability.getSpecialType().equals(FacilitySpecialAvailabilityContext.SpecialType.SPECIAL_AVAILABILITY.getIndex()) && splAvailability.getCancelOnCostChange())
                        {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cost is Mandatory if you want to cancel the booking for cost change.");
                        }
                        if(splAvailability.getCost()<0 && splAvailability.getSpecialType().equals(FacilitySpecialAvailabilityContext.SpecialType.SPECIAL_AVAILABILITY.getIndex()))
                        {
                            ZonedDateTime cal = DateTimeUtil.getDateTime(splAvailability.getStartDate());
                            int day = cal.get(ChronoField.DAY_OF_WEEK);

                            List<WeekDayAvailability> weekDayList = FacilityAPI.getWeekDayAvailabilityForDay(facility.getId(), day);
                            if(CollectionUtils.isNotEmpty(weekDayList))
                            {
                                WeekDayAvailability dayAvailability = weekDayList.get(0);
                                splAvailability.setCost(dayAvailability.getCost());
                            }
                        }
                        if(splAvailability.getCost()<0)
                        {
                            splAvailability.setCost((double) 0);
                        }
                    }
                }
            }
        }
        return false;
    }
}
