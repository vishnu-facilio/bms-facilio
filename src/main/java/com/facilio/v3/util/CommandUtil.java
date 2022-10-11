package com.facilio.v3.util;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class CommandUtil {

    public static List<? extends ModuleBaseWithCustomFields> getModuleDataList(Context context, String moduleName) {
        Map<String, List<? extends ModuleBaseWithCustomFields>> recordMap = (Map<String, List<? extends ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        return recordMap.get(moduleName);
    }

    public static ModuleBaseWithCustomFields getModuleData(Context context, String moduleName, long id) {
        List<? extends ModuleBaseWithCustomFields> moduleDataList = getModuleDataList(context, moduleName);
        if (CollectionUtils.isNotEmpty(moduleDataList)) {
            for (ModuleBaseWithCustomFields moduleData : moduleDataList) {
                if (moduleData.getId() == id) {
                    return moduleData;
                }
            }
        }
        return null;
    }

    /*
     * Finds lookup field relation in child module
     *  if parent module is workorder (workorder extends ticket) and child module is ticketattachment,
     *  the look up is present for ticket module in ticketattachment.
     */
    public static LookupField findLookupFieldInChildModule(@NotNull FacilioModule parentModule, @NotNull Map<String, LookupField> childLookupFields) throws Exception {
        LookupField lookupField = childLookupFields.get(parentModule.getName());
        if (lookupField == null) {
            FacilioModule extendModule = parentModule.getExtendModule();
            if (extendModule == null) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR);
            }
            return findLookupFieldInChildModule(extendModule, childLookupFields);
        }
        return lookupField;
    }
}
