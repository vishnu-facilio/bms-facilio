package com.facilio.common.reading.util;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import org.apache.commons.chain.Context;

import java.time.ZonedDateTime;

public class ReadingUtils {
    public static void adjustTtime(ReadingContext reading) {
        ZonedDateTime zdt = DateTimeUtil.getDateTime(reading.getTtime());
        if (reading.getDataInterval() > 0) {
            int interval = reading.getDataInterval();
            zdt = zdt.truncatedTo(new SecondsChronoUnit(interval * 60L));
        }
        reading.setTtime(DateTimeUtil.getMillis(zdt, true));
    }

    public static <T> T getOrDefaultValue(Context ctx, String key, T defVal) {
        if(ctx.get(key) == null) {
            return defVal;
        }
        return (T) ctx.get(key);
    }

}
