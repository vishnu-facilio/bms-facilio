package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioStatus;
import com.facilio.v3.context.Constants;

public class SetInviteStatusConditionForVisitsListCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("preRegistered")){
           JSONObject filters = (JSONObject)context.get(Constants.FILTERS);

           if(queryParams.containsKey("approvals")) {
               JSONObject status = new JSONObject();

               FacilioStatus requestedStatus = VisitorManagementAPI.getInviteVisitorLogStatus("InviteRequested");
               FacilioStatus rejectedStatus = VisitorManagementAPI.getInviteVisitorLogStatus("InviteRejected");

               JSONArray possibleStatesIdArray = new JSONArray();
               possibleStatesIdArray.add(String.valueOf(requestedStatus.getId()));
               possibleStatesIdArray.add(String.valueOf(rejectedStatus.getId()));

               status.put("operatorId", (long) PickListOperators.IS.getOperatorId());
               status.put("value", possibleStatesIdArray);

               filters.put("moduleState", status);
           }
           context.put(FacilioConstants.ContextNames.FILTERS, filters);
        }
        return false;
    }
}
