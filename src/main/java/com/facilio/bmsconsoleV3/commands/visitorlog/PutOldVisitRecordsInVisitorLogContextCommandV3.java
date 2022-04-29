package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections.MapUtils;

public class PutOldVisitRecordsInVisitorLogContextCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<VisitorLogContextV3> list = recordMap.get(moduleName);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>) context.get(Constants.QUERY_PARAMS);

        context.put(FacilioConstants.ContextNames.VISITOR_LOG_RECORDS, list);
        if (MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("withQrUrl") && queryParams.get("withQrUrl") != null && !queryParams.get("withQrUrl").isEmpty() && Boolean.parseBoolean((String) queryParams.get("withQrUrl").get(0))) {
            context.put(FacilioConstants.ContextNames.WITH_QRURL, true);
        }
        else {
            context.put(FacilioConstants.ContextNames.WITH_QRURL, false);
        }

        return false;
    }
}
