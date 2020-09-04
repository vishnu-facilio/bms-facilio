package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AddUserCriteriaMyNotification extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject filters = (JSONObject)context.get(Constants.FILTERS);
        JSONObject userFilter = new JSONObject();
        JSONArray ids = new JSONArray();
        ids.add(AccountUtil.getCurrentUser().getId()+"");

        userFilter.put("operatorId", (long) PickListOperators.IS.getOperatorId());
        userFilter.put("value", ids);

        if(filters == null){
            filters = new JSONObject();
        }
        filters.put("user", userFilter);

        context.put(FacilioConstants.ContextNames.SORTING_QUERY, "User_Notification.SYS_CREATED_TIME desc");
        context.put(FacilioConstants.ContextNames.FILTERS, filters);
        return false;
    }
}
