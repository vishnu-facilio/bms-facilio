package com.facilio.bmsconsoleV3.commands.workpermit;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Map;
public class ValidationForWorkPermitDateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkPermitContext> workPermitContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(workPermitContexts)) {
            for (V3WorkPermitContext wp : workPermitContexts) {

                if ( wp.getExpectedStartTime() > wp.getExpectedEndTime()) {
                    {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Valid From should be less than Valid To.");
                    }

                }
            }
        }
        return false;
    }
}
