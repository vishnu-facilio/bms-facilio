package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.MultiResourceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class ValidateBeforeSaveCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<MultiResourceContext> multiResourceContexts = recordMap.get(moduleName);
        for(MultiResourceContext multiResourceContext:multiResourceContexts ) {
            if ((multiResourceContext.getAsset() == null && multiResourceContext.getSpace() == null)) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Asset and space can't be empty, Please fill either Asset or Space");

            }
        }

        return false;
    }
}
