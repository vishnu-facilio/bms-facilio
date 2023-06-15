package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
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

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.SLOTS);
        if(CollectionUtils.isNotEmpty(bookings)) {

            Map<String, Object> bodyParams = Constants.getBodyParams(context);
            if (MapUtils.isEmpty(bodyParams) || (!bodyParams.containsKey("cancel") && !bodyParams.containsKey("cancelBooking"))) {

                for (V3FacilityBookingContext booking : bookings) {
                    Map<String, List<SubFormContext>> relations = booking.getRelations();

                    if(booking.getId() <= 0) {
                        booking.setBookingRequestedBy(AccountUtil.getCurrentUser());
                    }
                    Map<String, List<Map<String, Object>>> subformMap = booking.getSubForm();
                    if (booking.getFacility() == null || booking.getFacility().getId() <= 0) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Facility cannot be empty");
                    }
                    if (MapUtils.isEmpty(subformMap) || !subformMap.containsKey(FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS)) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Time slot cannot be empty");
                    }
                    if(booking.getBookingDate()==null){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Date cannot be null");
                    }
                    FacilityContext facility = (FacilityContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY, booking.getFacility().getId(), FacilityContext.class);
                    List<BookingSlotsContext> slotList = FieldUtil.getAsBeanListFromMapList(subformMap.get(FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS), BookingSlotsContext.class);
                    if(CollectionUtils.isEmpty(slotList)){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Time slot cannot be empty");
                    }
                    if (facility.getMaxSlotBookingAllowed() != null && slotList.size() > facility.getMaxSlotBookingAllowed()) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Maximum slots permitted for a booking have been utilized.");
                    }
                    for (BookingSlotsContext bookingSlot : slotList) {
                        if (bookingSlot == null) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Time slot cannot be empty");
                        }
                        SlotContext slot = (SlotContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.SLOTS, bookingSlot.getSlot().getId(), SlotContext.class);
                        if (slot != null) {
                            long validBookingTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis(), false) +  ((facility.getBookingAdvancePeriodInDays()+1) * 1000 * 60 * 60 * 24) - 1;
                            if(slot.getSlotStartTime()> validBookingTime)
                            {
                                String timeUnit = facility.getBookingAdvancePeriodInDays() > 1 ? "days" : "day";
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Booking can be done only before "+facility.getBookingAdvancePeriodInDays()+" "+timeUnit );
                            }
                            if (facility.getBookingAdvancePeriodInDays() != null) {
                                Long slotStartDayTime = DateTimeUtil.getDayStartTimeOf(slot.getSlotStartTime());
                                Long currentTimeStartDayTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis());

                                int daysBetween = DateTimeUtil.getDaysBetween(currentTimeStartDayTime, slotStartDayTime);
//                                if (daysBetween != facility.getBookingAdvancePeriodInDays()) {
//                                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Booking not permitted");
//                                }
                            }

                            int bookedCount = slot.getBookingCount() != null ? slot.getBookingCount() : 0;
                            if (slot.getBookingCount() != null && slot.getBookingCount() > 0 && !facility.isMultiBookingPerSlotAllowed()) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Booking already exists in the selected slot ");
                            }
                            if (booking.getNoOfAttendees() != null && facility.getUsageCapacity() != null && booking.getNoOfAttendees() > (facility.getUsageCapacity() - bookedCount)) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "The current booking count exceeds the maximum number of bookings for the selected time slot");
                            }
                            if(slot.getSlotStartTime() <= System.currentTimeMillis()) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Booking is not allowed for earlier time slots; pick a suitable time slot instead " );
                            }
                            //setting booking count in slot
                            if (slot.getBookingCount() != null) {
                                slot.setBookingCount(slot.getBookingCount() + booking.getNoOfAttendees());
                            } else {
                                slot.setBookingCount(booking.getNoOfAttendees());
                            }
                            V3RecordAPI.updateRecord(slot, module, modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.SLOTS));
                        }
                    }
                    if (facility.isAttendeeListNeeded() && (CollectionUtils.isEmpty(booking.getInternalAttendees()) && (MapUtils.isEmpty(relations) || !relations.containsKey(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE) || CollectionUtils.isEmpty(relations.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE).get(0).getData())))) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Attendee list cannot be empty");
                    }
                    if (booking.getNoOfAttendees() != null && facility.getMaxAttendeeCountPerBooking() != null && booking.getNoOfAttendees() > facility.getMaxAttendeeCountPerBooking()) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Maximum number of attendees per booking reached");
                    }
                    int bookingAttendeesCount = 0;
                    if(facility.isAttendeeListNeeded()) {
                        if (CollectionUtils.isNotEmpty(booking.getInternalAttendees())) {
                            bookingAttendeesCount += booking.getInternalAttendees().size();
                        }
                        if (!MapUtils.isEmpty(relations) && relations.containsKey(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE)) {
                            List<SubFormContext> externalattendees = relations.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE);
                            if(CollectionUtils.isNotEmpty(externalattendees)) {
                                bookingAttendeesCount += externalattendees.get(0).getData().size();
                            }
                        }
                        if (bookingAttendeesCount != booking.getNoOfAttendees()) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The number of attendees does not match the specified count");
                        }
                    }
                }
            }
        }

        return false;
    }
}
