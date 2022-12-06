package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveSubFormFromLineItemsCommand extends ProcessSubFormLineItemsCommand {
    private List<ModuleBaseWithCustomFields> getRecord(Context context) {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        return recordMap.get(moduleName);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> records = getRecord(context);
        if (CollectionUtils.isEmpty(records)) {
            return false;
        }

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String mainModuleName = Constants.getModuleName(context);

        for (ModuleBaseWithCustomFields record: records) {
            V3Context v3Record = (V3Context) record;
            Map<String, List<SubFormContext>> lineItems = v3Record.getRelations();
            if (MapUtils.isEmpty(lineItems)) {
                continue;
            }

            for (String moduleName : lineItems.keySet()) {
                List<SubFormContext> subFormContextList = lineItems.get(moduleName);
                List<ModuleBaseWithCustomFields> beanList = insert(context, mainModuleName, moduleName, subFormContextList, record.getId());
            }
        }
        return false;
    }

    private List<LookupField> getLookupFieldListFromModuleName (Map<String, List<LookupField>> allLookupFields, String moduleName) throws Exception {
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
                currentModule = module.hideFromParents() ? null : module.getExtendModule();
            }
        }
        return lookupFieldList;
    }

    private List<ModuleBaseWithCustomFields> insert(Context context, String mainModuleName, String moduleName, List<SubFormContext> subFormContextList, long recordId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
//        List<FacilioField> fields = modBean.getAllFields(moduleName);

//        List<FacilioField> fileFields = new ArrayList<>();
//        for (FacilioField f : fields) {
//            if (f instanceof FileField) {
//                fileFields.add(f);
//            }
//        }

        Map<String, Object> parentObject = new HashMap<>();
        parentObject.put("id", recordId);

        Map<String, List<LookupField>> allLookupFields = getAllLookupFields(modBean, module);
        List<LookupField> lookupFieldList = getLookupFieldListFromModuleName(allLookupFields, mainModuleName);

        Map<Long, FacilioField> fieldMap = new HashMap<>();
        for (LookupField lookupField: lookupFieldList) {
            fieldMap.put(lookupField.getId(), lookupField);
        }

        List<Map<String, Object>> subFormDataList = new ArrayList<>();
        for (SubFormContext subFormContext : subFormContextList) {
            List<V3Context> recordList = subFormContext.getData();
            if (CollectionUtils.isEmpty(recordList)) {
                continue;
            }

            FacilioField lookup = getLookupField(subFormContext.getFieldId(), fieldMap, mainModuleName);
            if (lookup == null) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid field id in relations");
            }

            for (V3Context record: recordList) {
                // ignoring patched records
                if (record.getId() > 0) {
                    continue;
                }
                Map<String, Object> recordMap = FieldUtil.getAsProperties(record);
                recordMap.put(lookup.getName(), parentObject);
                subFormDataList.add(recordMap);
            }
        }

        List<ModuleBaseWithCustomFields> addedRecords = null;
        if (CollectionUtils.isNotEmpty(subFormDataList)) {
            FacilioContext recordListContext = V3Util.createRecordList(module, subFormDataList, null, null);
            addedRecords = Constants.getRecordList(recordListContext);
        }
        if (addedRecords == null) {
            addedRecords = new ArrayList<>();
        }
        return addedRecords;
    }

    private Map<String, List<LookupField>> getAllLookupFields(ModuleBean modBean, FacilioModule module) throws Exception {
        List<LookupField> lookupFields = new ArrayList<>();
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        if (CollectionUtils.isNotEmpty(allFields)) {
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


}
