package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class AmenitiesContext extends V3Context {

    private String name;
    private Integer category;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    private AmenitiesContext.AmenityLogo amenityLogo;
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum AmenityLogo implements FacilioEnum {
        NETWORK("network"),
        HOUSEKEEPING("housekeeping"),
        OTHERS("others")
        ;

        private String name;
        AmenityLogo (String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static AmenitiesContext.AmenityLogo valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }


    public AmenitiesContext.AmenityLogo getAmenityLogoEnum() {
        return amenityLogo;
    }

    public int getAmenityLogo() {
        if(amenityLogo!=null)
        {
            return amenityLogo.getIndex();
        }
        return -1;
    }
    @JsonIgnore
    public Map<String, Object> getAmenityLogoObj() throws Exception {
        return FieldUtil.getAsProperties(amenityLogo);
    }


    public void setAmenityLogo(int amenityLogo) {
        this.amenityLogo = AmenitiesContext.AmenityLogo.valueOf(amenityLogo);
    }
    public void setAmenityLogo(AmenitiesContext.AmenityLogo amenityLogo) {
        this.amenityLogo = amenityLogo;
    }

}
