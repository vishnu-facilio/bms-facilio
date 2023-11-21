package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

public class V2FetchReadingFromAlarmCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long alarmId = (Long) context.get("alarmId");
        boolean isWithPrerequisite = (boolean) context.get("isWithPrerequisite");
        JSONArray measures_arr = V2AnalyticsOldUtil.getDataPointFromNewAlarm(alarmId, isWithPrerequisite, null);
        context.put("measures", measures_arr);
        return false;
    }
}
