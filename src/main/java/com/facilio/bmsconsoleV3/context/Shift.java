package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class Shift extends V3Context {



    private String name;
    private Boolean defaultShift;
    private String weekend;
    private String colorCode;
    private Long endTime;



    private Long startTime;


}
