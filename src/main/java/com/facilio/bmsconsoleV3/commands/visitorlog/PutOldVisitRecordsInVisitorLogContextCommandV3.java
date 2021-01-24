package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;

public class PutOldVisitRecordsInVisitorLogContextCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<VisitorLogContextV3> list = recordMap.get(moduleName);

        context.put(FacilioConstants.ContextNames.VISITOR_LOG_RECORDS, list);
        return false;
    }
}
