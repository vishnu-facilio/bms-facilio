package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatchSubFormLineItemsCommand extends FacilioCommand {
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
            V3Context v3Context = (V3Context) record;
            Map<String, List<SubFormContext>> lineItems = v3Context.getRelations();
            if (MapUtils.isEmpty(lineItems)) {
                continue;
            }

            for (String moduleName : lineItems.keySet()) {
                List<SubFormContext> subFormContextList = lineItems.get(moduleName);
                update(mainModuleName, moduleName, subFormContextList, record.getId());
            }
        }
        return false;
    }

    private void update(String mainModuleName, String moduleName, List<SubFormContext> subFormContextList, long recordId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, List<LookupField>> allLookupFields = getAllLookupFields(modBean, module);
        List<LookupField> lookupFieldList = allLookupFields.get(mainModuleName);

        Map<Long, FacilioField> fieldMap = new HashMap<>();
        for (LookupField lookupField: lookupFieldList) {
            fieldMap.put(lookupField.getId(), lookupField);
        }

        Map<String, Object> parentObject = new HashMap<>();
        parentObject.put("id", recordId);

        for (SubFormContext subFormContext: subFormContextList) {
            List<V3Context> recordList = subFormContext.getData();
            if (CollectionUtils.isEmpty(recordList)) {
                continue;
            }

            long fieldId = subFormContext.getFieldId();
            FacilioField lookup = fieldMap.get(fieldId);

            for (V3Context record: recordList) {
                if (record.getId() <= 0) {
                    continue;
                }

                UpdateRecordBuilder<ModuleBaseWithCustomFields> updateRecordBuilder = new UpdateRecordBuilder<>()
                        .module(module)
                        .fields(fields)
                        .andCondition(CriteriaAPI.getIdCondition(record.getId(), module))
                        .andCondition(CriteriaAPI.getCondition(lookup, recordId+"", NumberOperators.EQUALS));

                Map<String, Object> properties = FieldUtil.getAsProperties(record);
                properties.put(lookup.getName(), parentObject);

                updateRecordBuilder.updateViaMap(properties);
            }
        }
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
