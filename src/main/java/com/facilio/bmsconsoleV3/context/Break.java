package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.v3.context.V3Context;
import lombok.Data;

import java.util.List;


@Data
public class Break extends V3Context {

    private String name;
    private Long BreakTime;
    private List<Shift> shifts;

    public enum Type implements FacilioStringEnum {
        PAID,
        UNPAID
    }
    private Break.Type breakType;


    public enum Mode implements FacilioStringEnum {
        MANUAL,
        AUTOMATIC
    }
    private Break.Mode breakMode;
}
