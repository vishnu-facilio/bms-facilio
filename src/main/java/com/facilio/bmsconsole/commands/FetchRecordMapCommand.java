package com.facilio.bmsconsole.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchRecordMapCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
       String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

        Constants.setModuleName(context,moduleName);
        FacilioContext recordMapContext = V3Util.getSummary(moduleName,recordIds);
        context.put(FacilioConstants.ContextNames.RECORD_MAP,Constants.getRecordMap(recordMapContext));

        return false;
    }
}
