package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FillFacilityBookingDetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);

        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long recId : recordIds) {
                V3FacilityBookingContext booking = (V3FacilityBookingContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, recId);
                if (booking != null) {
                    booking.setSlotList(FacilityAPI.getBookingSlots(booking.getId()));
                    booking.setFacilityBookingExternalAttendee(FacilityAPI.getExternalAttendees(booking.getId()));
                    if(CollectionUtils.isNotEmpty(booking.getSlotList()))
                    {
                        BookingSlotsContext firstSlot = booking.getSlotList().get(0);
                        boolean showCancel = false;
                        if(firstSlot.getSlot().getSlotStartTime() > System.currentTimeMillis() && !booking.isCancelled()){
                            if(booking.getBookingRequestedBy().getOuid() == AccountUtil.getCurrentUser().getOuid()) {
                                showCancel = true;
                            }
                            showCancel = checkTabPermissions(showCancel);
                        }
                        booking.setCanShowCancel(showCancel);
                    }
                }
            }
        }
        return false;
    }

    private boolean checkTabPermissions(boolean showCancel) throws Exception{
        boolean hasUpdatePermission = WebTabUtil.checkModulePermissionForTab("UPDATE",FacilioConstants.ContextNames.FACILITY_BOOKING);
        boolean hasDeletePermission = WebTabUtil.checkModulePermissionForTab("DELETE",FacilioConstants.ContextNames.FACILITY_BOOKING);
        if(hasDeletePermission || hasUpdatePermission){
            return true;
        }else{
            return showCancel;
        }
    }

}
