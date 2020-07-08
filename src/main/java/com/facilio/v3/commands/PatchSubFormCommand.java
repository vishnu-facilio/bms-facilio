package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class PatchSubFormCommand extends FacilioCommand {

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

        String mainModuleName = Constants.getModuleName(context);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);

        for (ModuleBaseWithCustomFields record: records) {
            Map<String, List<Map<String, Object>>> subFormMap = record.getSubForm();
            if (MapUtils.isEmpty(subFormMap)) {
                continue;
            }

            for (String moduleName : subFormMap.keySet()) {
                List<Map<String, Object>> subForm = subFormMap.get(moduleName);
                update(mainModuleName, moduleName, subForm, record.getId());
                recordMap.computeIfAbsent(moduleName, k -> new ArrayList<>());
            }
        }
        return false;
    }

    private void update(String mainModuleName, String moduleName, List<Map<String, Object>> subFormList, long recordId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, LookupField> allLookupFields = getAllLookupFields(modBean, module);
        LookupField lookupField = allLookupFields.get(mainModuleName);

        Map<String, Object> parentObject = new HashMap<>();
        parentObject.put("id", recordId);

        for (Map<String, Object> subForm: subFormList) {
            if (subForm.get("id") == null) {
                continue;
            }
            UpdateRecordBuilder<ModuleBaseWithCustomFields> updateRecordBuilder = new UpdateRecordBuilder<>()
                    .module(module)
                    .fields(fields)
                    .andCondition(CriteriaAPI.getIdCondition((Long) subForm.get("id"), module));
            subForm.put(lookupField.getName(), parentObject);
            updateRecordBuilder.updateViaMap(subForm);
        }
    }

    private Map<String, LookupField> getAllLookupFields(ModuleBean modBean, FacilioModule module) throws Exception {
        List<LookupField> lookupFields = new ArrayList<>();
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        if (CollectionUtils.isNotEmpty(allFields)) {
            for (FacilioField f : allFields) {
                if (f instanceof LookupField) {
                    lookupFields.add((LookupField) f);
                }
            }
        }

        Map<String, LookupField> lookupFieldMap = new HashMap<>();
        for (LookupField l : lookupFields) {
            FacilioModule lookupModule = l.getLookupModule();
            if (lookupModule != null) {
                lookupFieldMap.put(lookupModule.getName(), l);
            }
        }
        return lookupFieldMap;
    }
}
