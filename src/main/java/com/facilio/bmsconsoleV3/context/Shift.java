package com.facilio.bmsconsoleV3.context;

import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import lombok.Data;
import org.bouncycastle.util.Strings;

@Data
public class Shift extends V3Context {

    private long toSeconds(String timeStr) {
        // guaranteed to have two shards, hence 0 index is used.
        String hour = Strings.split(timeStr, ':')[0];
        long time = FacilioUtil.parseLong(hour);
        // converting to second of the day
        return time * 60 * 60;
    }

    private String name;
    private Boolean isDefaultShift;
    private String weekend;
    private String colorCode;
    private Long endTime;

    public void setEndTime(String endTime) {
        this.endTime = toSeconds(endTime);
    }

    private Long startTime;

    public void setStartTime(String startTime) {
        this.startTime = toSeconds(startTime);
    }
}
