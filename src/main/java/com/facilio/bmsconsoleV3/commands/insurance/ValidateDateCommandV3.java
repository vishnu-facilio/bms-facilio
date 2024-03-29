package com.facilio.bmsconsoleV3.commands.insurance;

import com.facilio.bmsconsoleV3.context.V3InsuranceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateDateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InsuranceContext> insurances = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(insurances)) {
            for (V3InsuranceContext ins : insurances) {
                if (ins.getValidFrom() != null && ins.getValidTill() != null && ins.getValidFrom() > ins.getValidTill()) {
                    {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please enter proper validity Dates.");
                    }
                }
            }
        }
        return false;
    }
}
