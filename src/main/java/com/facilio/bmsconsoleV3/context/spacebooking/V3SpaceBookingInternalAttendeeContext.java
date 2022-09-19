package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter

public class V3SpaceBookingInternalAttendeeContext extends V3Context {

    private V3SpaceBookingContext left;
    private V3PeopleContext right;

}
