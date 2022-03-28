package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class DeleteSubFormLineItems extends ProcessSubFormLineItemsCommand {

    private List<ModuleBaseWithCustomFields> getRecord(Context context) {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        return recordMap.get(moduleName);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> recordList = getRecord(context);
        String mainModuleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (ModuleBaseWithCustomFields record: recordList) {
            V3Context v3Context = (V3Context) record;
            Map<String, List<SubFormContext>> lineItems = v3Context.getRelations();
            if (MapUtils.isEmpty(lineItems)) {
                continue;
            }

            Set<String> moduleNameList = lineItems.keySet();
            for (String moduleName: moduleNameList) {
                List<SubFormContext> subFormContextList = lineItems.get(moduleName);
                for (SubFormContext subFormContext: subFormContextList) {
                    if (CollectionUtils.isEmpty(subFormContext.getDeleteIds())) {
                        continue;
                    }
                    FacilioModule module = modBean.getModule(moduleName);

                    Map<String, List<LookupField>> allLookupFields = getAllLookupFields(modBean, module);
                    List<LookupField> lookupFieldList = allLookupFields.get(mainModuleName);
                    Map<Long, FacilioField> fieldMap = new HashMap<>();
                    for (LookupField lookupField: lookupFieldList) {
                        fieldMap.put(lookupField.getId(), lookupField);
                    }

                    FacilioField lookupField = getLookupField(subFormContext.getFieldId(), fieldMap, mainModuleName);
                    if (lookupField == null) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid field id in relations");
                    }
                    deleteRows(module, subFormContext.getDeleteIds(), lookupField, record);
                }
            }
        }
        return false;
    }

    private int deleteRows(FacilioModule module, List<Long> rowIds, FacilioField lookupField, ModuleBaseWithCustomFields record) throws Exception {
        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(module)
                .select(Collections.singletonList(FieldFactory.getIdField(module)))
                .andCondition(CriteriaAPI.getIdCondition(rowIds, module))
                .andCondition(CriteriaAPI.getCondition(lookupField, record.getId()+"", NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.getAsProps();
        List<Long> recordIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> map : props) {
                Object id = map.get("id");
                if (id instanceof Number) {
                    recordIds.add(((Number) id).longValue());
                }
            }

            Map<String, Object> deleteObj = new HashMap<>();
            deleteObj.put(module.getName(), recordIds);
            FacilioContext context = V3Util.deleteRecords(module.getName(), deleteObj, null,false);
            Map<String, Integer> countMap = Constants.getCountMap(context);
            if (countMap.containsKey(module.getName())) {
                return countMap.get(module.getName());
            }
        }

        return 0;
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
