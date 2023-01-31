package com.facilio.bmsconsoleV3.context.shift;

import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import lombok.Data;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.util.Calendar;
import java.util.List;

@Data
public class Shift extends V3Context {

    private String name;
    private Boolean defaultShift;
    private String weekend;
    private String colorCode;
    private Long endTime;
    private Long startTime;
    private Integer associatedBreaks;
    private Integer associatedEmployees;

    public boolean isWeeklyOff(long epoch) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(epoch);

        JSONObject weeklyOff = FacilioUtil.parseJson(weekend);

        int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        List<Long> weekConfig = (List<Long>) weeklyOff.get(Integer.toString(weekOfMonth));
        if (weekConfig == null){
            return false;
        }
        return weekConfig.contains(FacilioUtil.parseLong(dayOfWeek));
    }
}
