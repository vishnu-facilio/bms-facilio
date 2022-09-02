package com.facilio.bmsconsoleV3.context;

import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import lombok.Data;
import org.bouncycastle.util.Strings;

@Data
public class Shift extends V3Context {



    private String name;
    private Boolean isDefaultShift;
    private String weekend;
    private String colorCode;
    private Long endTime;



    private Long startTime;


}
