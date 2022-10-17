package com.facilio.bmsconsoleV3.context;

import lombok.Getter;
import lombok.Setter;

public class V3RoomsContext extends V3SpaceContext{

    @Getter @Setter
    private Integer roomType;

    @Getter @Setter
    private Long usageCapacity;
}
