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

public class V3SpaceBookingFormRelationContext {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long appId;
    private Long orgId;
    private Long parentModuleId;
    private Long categoryId;
    private Long moduleFormId;
    private String parentModuleName;
}


