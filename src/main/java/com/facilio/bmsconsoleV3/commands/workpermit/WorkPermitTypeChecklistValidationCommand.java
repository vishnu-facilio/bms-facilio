package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistCategoryContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class WorkPermitTypeChecklistValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkPermitTypeChecklistContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (WorkPermitTypeChecklistContext checklist : list) {
                if (checklist.getCategory() == null) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Category is required for adding a checklist");
                } else if(checklist.getCategory().getId() > 0) {
                    WorkPermitTypeChecklistCategoryContext category = (WorkPermitTypeChecklistCategoryContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST_CATEGORY, checklist.getCategory().getId(),WorkPermitTypeChecklistCategoryContext.class);
                    if (category != null) {
                        if (category.getWorkPermitType() != null) {
                            checklist.setWorkPermitType(category.getWorkPermitType());
                        } else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Category Doesn't belongs to work permit type");
                        }
                        if (category.getValidationType() != null) {
                            checklist.setValidationType(category.getValidationType());
                        } else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Category Doesn't belongs to validation type");
                        }
                    } else {
                        throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid Category Id");
                    }
                }
            }
        }

        return false;
    }
}
