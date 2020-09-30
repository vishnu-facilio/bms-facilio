package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.v3.context.V3Context;

public class FacilityAmenitiesContext extends V3Context {

    private AmenitiesContext amenity;
    private  FacilityContext  facility;

    public AmenitiesContext getAmenity() {
        return amenity;
    }

    public void setAmenity(AmenitiesContext amenity) {
        this.amenity = amenity;
    }

    public FacilityContext getFacility() {
        return facility;
    }

    public void setFacility(FacilityContext facility) {
        this.facility = facility;
    }
}
