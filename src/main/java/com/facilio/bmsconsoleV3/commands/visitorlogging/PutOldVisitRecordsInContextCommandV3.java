package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class PutOldVisitRecordsInContextCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorLoggingContext> list = recordMap.get(moduleName);

        context.put(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, list);
        return false;
    }
}
