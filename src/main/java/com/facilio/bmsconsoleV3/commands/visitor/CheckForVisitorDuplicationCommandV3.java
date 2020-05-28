package com.facilio.bmsconsoleV3.commands.visitor;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CheckForVisitorDuplicationCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorContext> visitors = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(visitors)) {
            for(V3VisitorContext visitor : visitors) {
                boolean visitorExisiting = V3VisitorManagementAPI.checkForDuplicateVisitor(visitor);
                if(visitorExisiting) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "A visitor already exists with this phone number");
                }
            }
        }
        return false;
    }
}
