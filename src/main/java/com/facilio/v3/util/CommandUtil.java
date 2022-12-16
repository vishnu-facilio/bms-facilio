package com.facilio.v3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static Map<String, List<LookupField>> getAllLookupFields(ModuleBean modBean, FacilioModule module) throws Exception {
        List<LookupField> lookupFields = new ArrayList<>();
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(allFields)) {
            for (FacilioField f : allFields) {
                if (f instanceof LookupField) {
                    lookupFields.add((LookupField) f);
                }
            }
        }

        Map<String, List<LookupField>> lookupFieldMap = new HashMap<>();
        for (LookupField l : lookupFields) {
            FacilioModule lookupModule = l.getLookupModule();
            if (lookupModule != null) {
                lookupFieldMap.computeIfAbsent(lookupModule.getName(), k -> new ArrayList<>());
                lookupFieldMap.get(lookupModule.getName()).add(l);
            }
        }
        return lookupFieldMap;
    }

    public static  List<LookupField> getLookupFieldListFromModuleName (Map<String, List<LookupField>> allLookupFields, String moduleName) throws Exception {
        List<LookupField> lookupFieldList = allLookupFields.get(moduleName);
        if (lookupFieldList == null) {
            ModuleBean modBean = Constants.getModBean();
            FacilioModule module = modBean.getModule(moduleName);
            FacilioModule currentModule = module.hideFromParents() ? null : module.getExtendModule(); // If a module is hidden from parent it's not considered as the same module. Just the structure is used. Because this won't work when fetching lookup
            while (currentModule != null) {
                lookupFieldList = allLookupFields.get(currentModule.getName());
                if (lookupFieldList != null) {
                    return lookupFieldList;
                }
                currentModule = currentModule.hideFromParents() ? null : currentModule.getExtendModule();
            }
        }
        return lookupFieldList;
    }
}
