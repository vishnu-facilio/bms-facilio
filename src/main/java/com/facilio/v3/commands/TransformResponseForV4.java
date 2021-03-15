package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class TransformResponseForV4 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isV4 = Constants.isV4(context);
        if (!isV4) {
            return false;
        }

        String moduleName = Constants.getModuleName(context);
        JSONObject recordJSON = Constants.getJsonRecordMap(context);
        if (MapUtils.isEmpty(recordJSON)) {
            Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
            if (MapUtils.isEmpty(recordMap)) {
                return false;
            }
            recordJSON = FieldUtil.getAsJSON(recordMap);
            Constants.setJsonRecordMap(context, recordJSON);
        }

        ArrayList recordList = (ArrayList) recordJSON.get(moduleName);
        if (CollectionUtils.isEmpty(recordList)) {
            return false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

        for (Object recordRaw: recordList) {
            Map recordObj = (Map) recordRaw;
            Iterator<Map.Entry> iterator = recordObj.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry next = iterator.next();
                String key = (String) next.getKey();
                Object existingVal = recordObj.get(key);
                if (key.equals("id")) {
                    recordObj.put(key, existingVal);
                    continue;
                }

                FacilioField field = fieldMap.get(key);
                if (field == null) {
                    iterator.remove();
                    continue;
                }

                long accessType = field.getAccessType();
                boolean isPresent = FacilioField.AccessType.READ.isPresent(accessType);

                if (!isPresent) {
                    iterator.remove();
                    continue;
                }

                FieldType dataTypeEnum = field.getDataTypeEnum();
                if (existingVal == null) {
                    continue;
                }
                Object changedVal;
                switch (dataTypeEnum) {
                    case LOOKUP:
                        changedVal = getChangedValForLookup((Map) existingVal, field);
                        break;
                    case ENUM:
                        changedVal = getChangedValForEnum(existingVal, field);
                        break;
                    case SYSTEM_ENUM:
                        changedVal = getChangedValForSystemEnum(existingVal, field);
                        break;
                    case DATE:
                        changedVal = getChangedValForDate(existingVal, field);
                        break;
                    default:
                        changedVal = existingVal;
                        break;
                }
                recordObj.put(key, changedVal);
            }
        }

        return false;
    }

    private Object getChangedValForEnum(Object existingVal, FacilioField field) {
        EnumField enumField = (EnumField) field;
        return enumField.getValue((Integer) existingVal);
    }

    private Object getChangedValForSystemEnum(Object existingVal, FacilioField field) {
        SystemEnumField systemEnumField = (SystemEnumField) field;
        return systemEnumField.getValue((Integer) existingVal);
    }

    private Object getChangedValForDate(Object existingVal, FacilioField field) {
        if (!(existingVal instanceof Long)) {
            return existingVal;
        }

       return DateTimeUtil.getZonedDateTime((Long) existingVal).toLocalDate().toString();
    }

    private Object getChangedValForLookup(Map existingVal, FacilioField field) throws Exception {
        Long lookupId = (Long) existingVal.get("id");
        if (lookupId == null) {
            return existingVal;
        }

        LookupField lookupField = (LookupField) field;

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = lookupField.getLookupModule();

        if (module == null) {
            return existingVal;
        }

        FacilioModule.ModuleType moduleTypeEnum = module.getTypeEnum();

        if (moduleTypeEnum == FacilioModule.ModuleType.PICK_LIST) {
            List<FacilioField> allFields = modBean.getAllFields(module.getName());
            FacilioField mainField = null;

            List<FacilioField> accessibleFields = new ArrayList<>();
            for (FacilioField f: allFields) {
                if (f.isMainField()) {
                    mainField = f;
                }

                if (FacilioField.AccessType.READ.isPresent(f.getAccessType())) {
                    accessibleFields.add(f);
                }
            }

            if (accessibleFields.isEmpty() && mainField == null) {
                return existingVal;
            }

            boolean getAllAccessibleFields = false;
            if (accessibleFields.size() > 0) {
                getAllAccessibleFields = true;
            }

            if (getAllAccessibleFields) {
                accessibleFields.add(FieldFactory.getIdField(module));
                SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder();
                selectRecordsBuilder
                        .module(module)
                        .select(accessibleFields)
                        .andCondition(CriteriaAPI.getIdCondition(lookupId, module));

                List<Map<String, Object>> asProps = selectRecordsBuilder.getAsProps();
                if (CollectionUtils.isNotEmpty(asProps)) {
                    Map<String, Object> res = new HashMap<>();
                    for (FacilioField accessibleField: accessibleFields) {
                        res.put(accessibleField.getName(), asProps.get(0).get(accessibleField.getName()));
                    }
                    return res;
                }
            } else {
                SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder();
                selectRecordsBuilder
                        .module(module)
                        .select(Arrays.asList(mainField, FieldFactory.getIdField(module)));

                List<Map<String, Object>> asProps = selectRecordsBuilder.getAsProps();
                for (Map<String, Object> prop: asProps) {
                    long id = (long) prop.get("id");

                    if (id == lookupId) {
                        return prop.get(mainField.getName());
                    }
                }
            }
        }
        return lookupId;
    }
}
