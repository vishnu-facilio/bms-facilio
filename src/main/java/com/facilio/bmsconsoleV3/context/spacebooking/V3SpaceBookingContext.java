package com.facilio.bmsconsoleV3.context.spacebooking;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ParkingStallContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3ExternalAttendeeContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3InternalAttendeeContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter

public class V3SpaceBookingContext extends V3Context {
    private String name;
    private String description;
    private V3SpaceContext space;
    private V3DeskContext desk;
    private V3ParkingStallContext parkingStall;
    private V3PeopleContext host;
    private V3PeopleContext reservedBy;
    private Long parentModuleId;
    private Integer noOfAttendees ;
    private Long bookingStartTime;
    private Long bookingEndTime;
    private List<V3SpaceBookingInternalAttendeeContext> internalAttendees;
    private List<V3SpaceBookingExternalAttendeeContext> externalAttendees;

}


