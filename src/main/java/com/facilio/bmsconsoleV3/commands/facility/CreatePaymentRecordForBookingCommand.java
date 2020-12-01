package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingPaymentContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreatePaymentRecordForBookingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3FacilityBookingContext> bookings = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(bookings)){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule payment = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_PAYMENTS);

            List<BookingPaymentContext> payments = new ArrayList<>();
            for(V3FacilityBookingContext booking : bookings) {
                Map<String, List<Map<String, Object>>> subformMap = booking.getSubForm();
                if(MapUtils.isEmpty(subformMap) || subformMap.containsKey(FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Slot is mandatory for a booking");
                }
                if(booking.getId() > 0){
                    //deleting payment records and re-adding
                    DeleteRecordBuilder<BookingPaymentContext> deleteBuilder = new DeleteRecordBuilder<BookingPaymentContext>()
                            .module(payment)
                            .andCondition(CriteriaAPI.getCondition("FACILITY_BOOKING_ID", "booking", String.valueOf(booking.getId()), NumberOperators.EQUALS));
                    deleteBuilder.delete();
                }
                List<BookingSlotsContext> slotList = FieldUtil.getAsBeanListFromMapList(subformMap.get(FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS), BookingSlotsContext.class);
                Double amount = 0.0;
                for(BookingSlotsContext bookingSlot : slotList) {
                    if(bookingSlot == null){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Slot is mandatory for a booking");
                    }
                    SlotContext slot = (SlotContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.SLOTS, bookingSlot.getSlot().getId(), SlotContext.class);
                    if(slot != null) {
                        amount = amount + slot.getSlotCost();
                    }
                }
                BookingPaymentContext bookingPayment = new BookingPaymentContext();
                bookingPayment.setAmount(amount);
                bookingPayment.setBooking(booking);
                bookingPayment.setPaymentStatus(BookingPaymentContext.PaymentStatus.DUE.getIndex());
                payments.add(bookingPayment);
                booking.setBookingAmount(amount);
            }
            if(CollectionUtils.isNotEmpty(payments)) {
                V3RecordAPI.addRecord(false, payments, payment, modBean.getAllFields(payment.getName()));
            }

        }

        return false;
    }
}
