package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ParkingStallContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class setSpaceBookingVariableCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        try {

            String moduleName = Constants.getModuleName(context);

            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3SpaceBookingContext> spaceBooking = recordMap.get(moduleName);
            long spaceId = -1;

            if (CollectionUtils.isNotEmpty(spaceBooking)) {

                V3SpaceBookingContext bookingData = spaceBooking.get(0);


                long startTime = bookingData.getBookingStartTime();
                long endTime = bookingData.getBookingEndTime();
                long currentTime = bookingData.getCurrentTime();


                if (bookingData.getDesk() != null) {
                    spaceId = bookingData.getDesk().getId();
                    V3DeskContext deskContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, spaceId);
                    bookingData.setParentModuleId(deskContext.getModuleId());

                } else if (bookingData.getParkingStall() != null) {
                    spaceId = bookingData.getParkingStall().getId();
                    V3ParkingStallContext parkingContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PARKING_STALL, spaceId);
                    bookingData.setParentModuleId(parkingContext.getModuleId());

                } else {
                    spaceId = bookingData.getSpace().getId();
                    V3SpaceContext spaceContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SPACE, spaceId, V3SpaceContext.class);
                    bookingData.setParentModuleId(spaceContext.getModuleId());
                }
                V3SpaceContext spaces = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SPACE, spaceId, V3SpaceContext.class);
                Long buildingId = spaces.getBuildingId();
                context.put(FacilioConstants.ContextNames.SpaceBooking.BUILDING_ID, buildingId);
                context.put(FacilioConstants.ContextNames.SpaceBooking.BOOKING_START_TIME, startTime);
                context.put(FacilioConstants.ContextNames.SpaceBooking.BOOKING_END_TIME, endTime);
                context.put(FacilioConstants.ContextNames.SpaceBooking.SPACE_ID, spaceId);
                context.put(FacilioConstants.ContextNames.SpaceBooking.CURRENT_TIME,currentTime);
                bookingData.setSpace(spaces);

            }
        }
          catch (Exception e) {
                e.printStackTrace();
            }
        return false;
    } }


