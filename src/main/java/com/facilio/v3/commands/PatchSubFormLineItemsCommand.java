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
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.CommandUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class PatchSubFormLineItemsCommand extends ProcessSubFormLineItemsCommand {
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

        Collection<Map<String, Object>> bulkRawInput = Constants.getBulkRawInput(context);
        Map<Long, Map<String, Object>> bulkRawInputMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(bulkRawInput)) {
            for (Map<String, Object> jsonObject : bulkRawInput) {
                Object idObject = jsonObject.get("id");
                if (idObject != null && idObject instanceof Number) {
                    bulkRawInputMap.putIfAbsent(((Number) idObject).longValue(), jsonObject);
                }
            }
        }

        for (ModuleBaseWithCustomFields record: records) {
            V3Context v3Context = (V3Context) record;
            Map<String, List<SubFormContext>> lineItems = v3Context.getRelations();
            if (MapUtils.isEmpty(lineItems)) {
                continue;
            }

            for (String moduleName : lineItems.keySet()) {
                List<SubFormContext> subFormContextList = lineItems.get(moduleName);
                Map<String, Object> jsonObject = bulkRawInputMap.get(record.getId());
                Map<String, Object> relationRawInput = (Map<String, Object>) jsonObject.get("relations");
                List<Map<String, Object>> relationData = (List<Map<String, Object>>) relationRawInput.get(moduleName);
                update(mainModuleName, moduleName, subFormContextList, record.getId(), relationData);
            }
        }
        return false;
    }

    private void update(String mainModuleName, String moduleName, List<SubFormContext> subFormContextList,
                        long recordId, List<Map<String, Object>> relationData) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, List<LookupField>> allLookupFields = CommandUtil.getAllLookupFields(modBean, module);
        List<LookupField> lookupFieldList = CommandUtil.getLookupFieldListFromModuleName(allLookupFields, mainModuleName);

        Map<Long, FacilioField> fieldMap = new HashMap<>();
        for (LookupField lookupField: lookupFieldList) {
            fieldMap.put(lookupField.getId(), lookupField);
        }

        Map<String, Object> parentObject = new HashMap<>();
        parentObject.put("id", recordId);

        List<Map<String, Object>> subFormDataList = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> relData: relationData) {
            List<Map<String, Object>> recordList = (List<Map<String, Object>>) relData.get("data");
            if (CollectionUtils.isEmpty(recordList)) {
                continue;
            }

            Long fieldId = (Long) relData.get("fieldId");

            FacilioField lookup = getLookupField(fieldId, fieldMap, mainModuleName);
            if (lookup == null) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid field id in relations");
            }

            for (Map<String, Object> record : recordList) {
                Object idObj = record.get("id");
                if (!(idObj instanceof Number)) {
                    continue;
                }

                long id = ((Number) idObj).longValue();
                if (((Number) idObj).longValue() <= 0) {
                    continue;
                }

                ids.add(id);
                subFormDataList.add(record);
            }
        }

        if (CollectionUtils.isNotEmpty(ids)) {
            FacilioContext summaryContext = V3Util.getSummary(moduleName, ids);
            List<ModuleBaseWithCustomFields> oldRecordList = Constants.getRecordListFromContext(summaryContext, moduleName);
            if (CollectionUtils.isNotEmpty(oldRecordList)) {
                Map<Long, Map<String, Object>> idVsRecordMap = new HashMap<>();
                for (ModuleBaseWithCustomFields record: oldRecordList) {
                    idVsRecordMap.put(record.getId(), FieldUtil.getAsJSON(record));
                }

                for (Map<String, Object> rec: subFormDataList) {
                    Map<String, Object> jsonObject = idVsRecordMap.get((long) rec.get("id"));
                    Set<String> keys = rec.keySet();
                    for (String key : keys) {
                        jsonObject.put(key, rec.get(key));
                    }
                }
                Collection<Map<String, Object>> values = idVsRecordMap.values();

                V3Config v3Config = ChainUtil.getV3Config(module);
                V3Util.updateBulkRecords(module, v3Config, oldRecordList, new ArrayList<>(values), ids,
                        null, null, null, null, null,null, null,false);
            }
        }
    }
}
