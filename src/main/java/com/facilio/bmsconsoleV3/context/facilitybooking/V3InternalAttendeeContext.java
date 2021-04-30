package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;

public class V3InternalAttendeeContext extends V3Context {
    private V3FacilityBookingContext left;

    private V3PeopleContext right;

    public V3FacilityBookingContext getLeft() {
        return left;
    }

    public void setLeft(V3FacilityBookingContext left) {
        this.left = left;
    }

    public V3PeopleContext getRight() {
        return right;
    }

    public void setRight(V3PeopleContext right) {
        this.right = right;
    }

}
