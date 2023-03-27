package com.facilio.datamigration.util;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class DataMigrationUtil {

    public static List<SupplementRecord> getSupplementFields(Collection<FacilioField> fields) {
        List<SupplementRecord> supplementFields = new ArrayList<>();
        for (FacilioField f : fields) {
            if ((!f.isDefault() && (f.getDataTypeEnum() == FieldType.LOOKUP || f.getDataTypeEnum() == FieldType.MULTI_LOOKUP
                        || f.getDataTypeEnum() == FieldType.MULTI_ENUM))
                    || (f.getDataTypeEnum().isRelRecordField())) {
                supplementFields.add((SupplementRecord) f);
            }
        }

        return supplementFields;
    }

    public static Map<Long, List<Long>> getIdsAndLookupIdsToFetch(List<Map<String, Object>> props, long moduleId,
                                                            Map<String, FacilioField> targetFieldNameVsFields, Map<String, Map<String, Object>> numberLookups) throws Exception {
        Map<Long, List<Long>> moduleIdVsOldRecordIds = new HashMap<>();
        for (Map<String, Object> prop : props) {
            for (Map.Entry<String, Object> value : prop.entrySet()) {
                String fieldName = value.getKey();
                Object data = value.getValue();
                if (data != null) {
                    FacilioField fieldObj = targetFieldNameVsFields.get(fieldName);
                    if(fieldObj != null) {
                        switch (fieldObj.getDataTypeEnum()) {
                            case LOOKUP:
                                FacilioModule lookupModule = ((LookupField) fieldObj).getLookupModule();
                                if (lookupModule.getModuleId() > 0 && MapUtils.isNotEmpty(((Map<String, Object>) data))) {
                                    Long lookupDataId = (Long) ((Map<String, Object>) data).get("id");
                                    if (moduleIdVsOldRecordIds.containsKey(lookupModule.getModuleId())) {
                                        moduleIdVsOldRecordIds.get(lookupModule.getModuleId()).add(lookupDataId);
                                    } else {
                                        moduleIdVsOldRecordIds.put(lookupModule.getModuleId(), new ArrayList<Long>() {{
                                            add(lookupDataId);
                                        }});
                                    }
                                }
                                break;
                            case MULTI_LOOKUP:
                                FacilioModule multilookupModule = ((MultiLookupField) fieldObj).getLookupModule();
                                if (multilookupModule.getModuleId() > 0 && CollectionUtils.isNotEmpty((List<Map<String, Object>>) data)) {
                                    for (Map<String, Object> lookupData : (List<Map<String, Object>>) data) {
                                        Long lookupDataId = (Long) (lookupData).get("id");
                                        if (moduleIdVsOldRecordIds.containsKey(multilookupModule.getModuleId())) {
                                            moduleIdVsOldRecordIds.get(multilookupModule.getModuleId()).add(lookupDataId);
                                        } else {
                                            moduleIdVsOldRecordIds.put(multilookupModule.getModuleId(), new ArrayList<Long>() {{
                                                add(lookupDataId);
                                            }});
                                        }
                                    }
                                }
                                break;
                            case NUMBER:
                                if(MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(fieldName)) {
                                    Long lookupDataId = (Long) data;
                                    if(lookupDataId != null && lookupDataId > 0) {
                                        String lookupModuleName = (String) numberLookups.get(fieldName).get("lookupModuleName");
                                        if (!(FacilioConstants.ContextNames.USERS.equals(lookupModuleName))) {
                                            Long parentModuleId = (Long) numberLookups.get(fieldName).get("lookupModuleId");
                                            if (moduleIdVsOldRecordIds.containsKey(parentModuleId)) {
                                                moduleIdVsOldRecordIds.get(parentModuleId).add(lookupDataId);
                                            } else {
                                                moduleIdVsOldRecordIds.put(parentModuleId, new ArrayList<Long>() {{
                                                    add(lookupDataId);
                                                }});
                                            }
                                        }
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    if(fieldName.equals("id")) {
                        if(moduleIdVsOldRecordIds.containsKey(moduleId)) {
                            moduleIdVsOldRecordIds.get(moduleId).add((long)data);
                        } else {
                            moduleIdVsOldRecordIds.put(moduleId, new ArrayList<Long>(){{add((long)data);}});
                        }
                    }
                }
            }
        }
        return moduleIdVsOldRecordIds;
    }
}
