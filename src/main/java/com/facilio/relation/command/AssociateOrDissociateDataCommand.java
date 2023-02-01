package com.facilio.relation.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.util.RelationshipDataUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class AssociateOrDissociateDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject data = (JSONObject) context.get(FacilioConstants.ContextNames.DATA);
        JSONObject params = (JSONObject) context.get(FacilioConstants.ContextNames.PARAMS);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String relationshipActionType = (String) context.get(FacilioConstants.ContextNames.RELATIONSHIP_ACTION_TYPE);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);

        JSONObject result = null;
        switch (relationshipActionType) {
            case "ASSOCIATE":
                result = RelationshipDataUtil.associateRelation(moduleName, data, queryParams, params);
                break;
            case "DISSOCIATE":
                result = RelationshipDataUtil.dissociateRelation(moduleName, data, queryParams, params);
                break;
        }

        context.put(FacilioConstants.ContextNames.RESULT, result);
        return false;
    }
}
