package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.util.FacilioStreamUtil.distinctByKey;

public class CheckPMForAssetsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("fetchChildCount")) {
            List<Long> recordIds = (List<Long>) bodyParams.get("recordIds");
            List<PMResourcePlannerContext> pms = PreventiveMaintenanceAPI.getPMForResources(recordIds);
            JSONObject errorObject = new JSONObject();
            if (CollectionUtils.isNotEmpty(pms)) {
                pms = pms.stream().filter(distinctByKey(PMResourcePlannerContext::getPmId)).collect(Collectors.toList());
                errorObject.put("Planned Maintenance", pms.size());
                throw new RESTException(ErrorCode.VALIDATION_ERROR, errorObject.toString());
            }
            throw new RESTException(ErrorCode.DEPENDENCY_EXISTS, errorObject.toString());
        }
        return false;
    }
}
