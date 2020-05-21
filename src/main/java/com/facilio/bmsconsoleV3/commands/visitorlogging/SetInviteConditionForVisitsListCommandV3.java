package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioStatus;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Map;

public class SetInviteConditionForVisitsListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("preRegistered")){
           JSONObject filters = (JSONObject)context.get(FacilioConstants.ContextNames.FILTERS);
           JSONObject isPreregistered = new JSONObject();
           JSONArray array = new JSONArray();
           array.add("true");

           isPreregistered.put("operatorId", (long) BooleanOperators.IS.getOperatorId());
           isPreregistered.put("value", array);

           filters.put("isPreregistered", isPreregistered);

           if(queryParams.containsKey("approvals")) {
               JSONObject status = new JSONObject();

               FacilioStatus requestedStatus = VisitorManagementAPI.getLogStatus("InviteRequested");
               FacilioStatus rejectedStatus = VisitorManagementAPI.getLogStatus("InviteRejected");

               JSONArray possibleStatesIdArray = new JSONArray();
               possibleStatesIdArray.add(String.valueOf(requestedStatus.getId()));
               possibleStatesIdArray.add(String.valueOf(rejectedStatus.getId()));

               status.put("operatorId", (long) PickListOperators.IS.getOperatorId());
               status.put("value", possibleStatesIdArray);

               filters.put("moduleState", status);
           }
        }
        return false;
    }
}
