package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Fetches multi lookup relationship
 *
 *  Map of module name Vs lookup field vs record id vs record
 *     {
 *         "workorder": {
 *            "attachments": {
 *              "parent": {
 *                  1234: {
 *                      record obj
 *                  }
 *              }
 *            }
 *         }
 *     }
 */
public class FetchRelationsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isV4 = Constants.isV4(context);
        if (isV4) {
            return false;
        }

        Map<String, List<String>> listRelationCriteria = Constants.getListRelationCriteria(context);
        if (MapUtils.isEmpty(listRelationCriteria)) {
            return false;
        }

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        JSONObject jsonRecordMap = Constants.getJsonRecordMap(context);
        ArrayList recordProps = (ArrayList) jsonRecordMap.get(moduleName);

        if (CollectionUtils.isEmpty(records)) {
            return false;
        }

        List<Long> idList = records.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());

        Set<Map.Entry<String, List<String>>> entries = listRelationCriteria.entrySet();

        Map<String, Map<String, Object>> relationRecords = new HashMap<>();
        relationRecords.put(moduleName, new HashMap<>());

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (Map.Entry<String, List<String>> lookupEntry: entries) {
            String lookupModuleName = lookupEntry.getKey();
            List<String> lookupFieldList = lookupEntry.getValue();

            FacilioModule lookupModule = modBean.getModule(lookupModuleName);
            for (String lookupFieldName: lookupFieldList) {
                FacilioField lookupField = modBean.getField(lookupFieldName, lookupModule.getName());
                SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
                        .module(lookupModule)
                        .andCondition(CriteriaAPI.getCondition(lookupField, StringUtils.join(idList, ","),NumberOperators.EQUALS))
                        .select(modBean.getAllFields(lookupModule.getName()));

                SelectRecordsBuilder.BatchResult<Map<String, Object>> batchResult = selectRecordsBuilder.getAsPropsInBatches(lookupField.getName(), 100);

                if (relationRecords.get(moduleName).get(lookupModule.getName()) == null) {
                    relationRecords.get(moduleName).put(lookupModule.getName(), new HashMap<>());
                }

                Map lookupFieldVsRecordMap = (Map) relationRecords.get(moduleName).get(lookupModule.getName());
                lookupFieldVsRecordMap.put(lookupFieldName, new HashMap<>());

                Map<Long, List<Long>> virtualFieldMap = new HashMap<>();
                while (batchResult.hasNext()) {
                    Map<Long, Map<String, Object>> props = batchResult.getAsMap();

                    Map<String, Object> multiLookup = relationRecords.get(moduleName);
                    if (multiLookup == null) {
                        relationRecords.put(lookupModule.getName(), new HashMap<>());
                    }

                    ((Map) lookupFieldVsRecordMap.get(lookupFieldName)).putAll(props);

                    for (Map<String, Object> lookupModuleRecord : props.values()) {
                        long lookupId;
                        Object object = lookupModuleRecord.get(lookupFieldName);
                        // temp fix - since many sub module has parent as number field instead of lookup
                        if (object instanceof Map) {
                            lookupId = (long) ((Map) object).get("id");
                        } else if (object instanceof Number) {
                            lookupId = ((Number) object).longValue();
                        } else {
                            throw new IllegalArgumentException("Invalid id field value");
                        }

                        if (virtualFieldMap.get(lookupId) == null) {
                            virtualFieldMap.put(lookupId, new ArrayList<>());
                        }

                        virtualFieldMap.get(lookupId).add((Long) lookupModuleRecord.get("id"));
                    }
                }

                for (Object propObject: recordProps) {
                    Map<String, Object> prop = (Map) propObject;
                    if (virtualFieldMap.get(prop.get("id")) == null) {
                        continue;
                    }

                    prop.put(lookupModuleName+"-"+lookupFieldName+"-"+"sub", virtualFieldMap.get(prop.get("id")));
                }
            }
        }

        if (!MapUtils.isEmpty(relationRecords.get(moduleName))) {
            Constants.setListRelationRecords(context, relationRecords);
        }
        return false;
    }
}
