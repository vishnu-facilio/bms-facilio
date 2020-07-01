package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;
import java.util.*;

public class DefaultInit extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (MapUtils.isNotEmpty(Constants.getRecordMap(context))) {
            return false;
        }
        Map<String, Object> data = Constants.getRawInput(context);
        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        Long id = (Long) context.get(Constants.RECORD_ID);

        String moduleName = Constants.getModuleName(context);
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(data, beanClass);;
        if (id == null) {
            setFormData(data, moduleName, moduleRecord);
        }

        if (id != null) {
            moduleRecord.setId(id);
        }

        Map<String, List> recordMap = new HashMap<>();
        recordMap.put(moduleName, Arrays.asList(moduleRecord));

        context.put(Constants.RECORD_MAP, recordMap);
        return false;
    }

    private ModuleBaseWithCustomFields getRecord(Long id, JSONObject data, Class beanClass, FacilioModule module) throws Exception {
        if (id == null) {
            return (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromJson(data, beanClass);
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(module.getName());

        SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder()
                .module(module)
                .select(allFields)
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        List<Map<String, Object>> asProps = selectRecordsBuilder.getAsProps();
        Map<String, Object> recordMap = asProps.get(0);

        Set<Map.Entry> entrySet = data.entrySet();
        for (Map.Entry entry: entrySet) {
            recordMap.put((String) entry.getKey(), entry.getValue());
        }

        return (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(recordMap, beanClass);
    }

    private void setFormData(Map<String, Object> data, String moduleName, ModuleBaseWithCustomFields moduleRecord) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        Set subModuleNames = new HashSet<>(getSubModuleRels(module.getModuleId()));
        Set nodeNames = data.keySet();
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        Map<String, List<Map<String, Object>>> subFormData = new HashMap<>();
        for (Object fieldName: nodeNames) {
            String fieldNameStr = (String) fieldName;
            FacilioField facilioField = fieldMap.get(fieldNameStr);
            Object nodeValue = data.get(fieldNameStr);
            if (facilioField == null
                    && (nodeValue instanceof List)
                    && subModuleNames.contains(fieldNameStr)) {
                List jsonArray = (List) nodeValue;
                List<Map<String, Object>> subFormList = new ArrayList<>();
                for (Object obj: jsonArray) {
                    Map map = (Map) obj;
                    Set<Map.Entry> entrySet = map.entrySet();
                    Map<String, Object> subForm = new HashMap<>();
                    for (Map.Entry entry: entrySet) {
                        String key = (String) entry.getKey();
                        subForm.put(key, entry.getValue());
                    }
                    subFormList.add(subForm);
                }
                subFormData.put(fieldNameStr, subFormList);
            }
        }
        moduleRecord.setSubForm(subFormData);
    }

    private Map<String, List<ModuleBaseWithCustomFields>> getSubModuleRecords(long id, long mainModuleId, Collection<String> subModuleNames, Class beanClass) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        for (String subModuleName: subModuleNames) {
            FacilioModule subFormModule = modBean.getModule(subModuleName);
            List<FacilioField> allFields = modBean.getAllFields(subModuleName);
            LookupField lookupField = null;
            for (FacilioField field: allFields) {
                if (!(field instanceof LookupField)) {
                    continue;
                }

                LookupField possible = (LookupField) field;
                if (possible.getLookupModuleId() == mainModuleId) {
                    lookupField = possible;
                    break;
                }
            }

            SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder<>()
                    .beanClass(beanClass)
                    .select(allFields)
                    .andCondition(CriteriaAPI.getCondition(lookupField, mainModuleId+"",NumberOperators.EQUALS));
            List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = selectRecordsBuilder.get();
            recordMap.put(subModuleName, moduleBaseWithCustomFields);
        }
        return recordMap;
    }

    private List<String> getSubModuleRels(long moduleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> subModuleRelFields = new ArrayList<>();
        subModuleRelFields.add(FieldFactory.getField("childModuleId", "CHILD_MODULE_ID", FieldType.NUMBER));

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table("SubModulesRel")
                .select(subModuleRelFields)
                .andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", moduleId+"", NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectRecordBuilder.get();

        List<String> result = new ArrayList<>();

        if (CollectionUtils.isEmpty(props)) {
            return result;
        }

        for (Map<String, Object> prop: props) {
            long childModuleId = (Long) prop.get("childModuleId");
            FacilioModule module = modBean.getModule(childModuleId);
            result.add(module.getName());
        }

        return result;
    }
}
