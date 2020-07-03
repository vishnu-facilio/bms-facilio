package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistCategoryContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class WorkPermitChecklistCategoryValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkPermitTypeChecklistCategoryContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (WorkPermitTypeChecklistCategoryContext category : list) {
                if (category.getValidationType() == null) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Validation Type is required for adding a category");
                }
                if (category.getWorkPermitType() == null) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Work Permit Type is required for adding a category");
                }
            }
        }

        return false;
    }
}
