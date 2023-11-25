package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

public class V2FetchReadingFromAlarmCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long readingRuleId = (Long) context.get(FacilioConstants.ContextNames.READING_RULE_ID);
        Long resourceId = (Long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
        JSONArray measures_arr = V2AnalyticsOldUtil.getDataPointForReadingRule(resourceId, readingRuleId);
        context.put("measures", measures_arr);
        return false;
    }
}
