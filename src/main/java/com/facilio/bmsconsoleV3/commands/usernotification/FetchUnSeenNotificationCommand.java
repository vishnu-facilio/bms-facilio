package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.util.UserNotificationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class FetchUnSeenNotificationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        if (MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("unseen")) {

            Map<String, Object> record = UserNotificationAPI.getUserNotificationMapping(AccountUtil.getCurrentUser().getId());
            if (MapUtils.isNotEmpty(record)) {
//                Map<String, Object> record = mappingRecord.get(0);
                long lastSeen = (long) record.get("lastSeen");
                JSONObject filters = (JSONObject)context.get(Constants.FILTERS);
                JSONObject lastSeenFilter = new JSONObject();
                JSONArray time = new JSONArray();
                time.add(String.valueOf(lastSeen));
                lastSeenFilter.put("operatorId", (long) NumberOperators.GREATER_THAN.getOperatorId());
                lastSeenFilter.put("value", time);

                if(filters == null){
                    filters = new JSONObject();
                }
                filters.put("sysCreatedTime", lastSeenFilter);


                context.put(FacilioConstants.ContextNames.FILTERS, filters);
            }
        }
        return false;
    }
}
