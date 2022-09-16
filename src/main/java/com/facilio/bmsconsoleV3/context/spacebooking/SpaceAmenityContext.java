package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.AmenitiesContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SpaceAmenityContext extends V3Context{


  private V3SpaceContext left;

    private AmenitiesContext right;

}
