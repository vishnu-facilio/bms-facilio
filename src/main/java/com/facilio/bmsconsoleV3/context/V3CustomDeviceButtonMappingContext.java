package com.facilio.bmsconsoleV3.context;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class V3CustomDeviceButtonMappingContext extends V3Context {
    private V3CustomKioskContext left;
    private V3CustomKioskButtonContext right;
}
