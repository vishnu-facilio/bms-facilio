package com.facilio.analytics.v2.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.UtilityMeterContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.readings.context.AssetCategoryReadingFieldContext;
import com.facilio.readings.helper.ReadingFieldsUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.operations.Bool;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;


public class V2GetReadingsFromCategoryCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        String type = (String )context.get("type");
        String searchText = (String)context.get("searchText");
        Long category = (Long)context.get("category");
        Boolean applyFilter = (Boolean)context.getOrDefault("filterFields",false);
        applyFilter = applyFilter == null ? false : applyFilter;

        List<FacilioModule> modules = (ArrayList<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
        if(type != null && type.equals("asset"))
        {
            context.put("fields",this.getReadingsList(modules, searchText , category, Boolean.TRUE,applyFilter));
        }
        else if(type != null && type.equals("meter")){
            context.put("fields", this.getReadingsList(modules, searchText, category, Boolean.TRUE,applyFilter));
        }
        else if(type != null && type.equals("weather")){
            context.put("fields", this.getWeatherFields((List<FacilioField>) context.get("fields"), searchText, category));
        }
        return false;
    }

    private Map<Long, Map<String,Object>> getReadingsList(List<FacilioModule> reading_modules, String searchText , Long categoryId, boolean isSkipFaultRuleFields,boolean applyFilter)throws Exception
    {
        Map<Long, Map<String,Object>> fieldMap =new HashMap<>();
        if(reading_modules != null && !reading_modules.isEmpty())
        {
            List<FacilioField> fields = new ArrayList<>();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            for (FacilioModule reading : reading_modules) {
                if(reading != null && reading.getType() != FacilioModule.ModuleType.LIVE_FORMULA.getValue()) {
                    fields.addAll(modBean.getAllFields(reading.getName()));
                    reading.setFields(new ArrayList<>());
                }
            }
            List<String> default_fields = getDefaultReadingFieldNames();
            for(FacilioField field: fields)
            {
                if (!default_fields.contains(field.getName()) )
                {
                    if(canSkipField(field) || (searchText != null && field.getDisplayName() != null && !field.getDisplayName().toLowerCase().contains(searchText.toLowerCase())) || (isSkipFaultRuleFields && (field.getName().equals("ruleResult") || field.getName().equals("costImpact") || field.getName().equals("energyImpact")))) {
                        continue;
                    }
                    else if(!isSkipFaultRuleFields){
                        if(field.getName().equals("ruleResult")){
                            fieldMap.put(field.getFieldId(), constructFieldObject(field, categoryId));
                        }
                    }else {
                        fieldMap.put(field.getFieldId(), constructFieldObject(field, categoryId));
                    }
                }
            }
        }
        if(applyFilter) {
            return applyFilters(fieldMap);
        }
        return fieldMap;
    }
    private Map<Long, Map<String,Object>> applyFilters(Map<Long, Map<String,Object>> fieldMap) throws Exception {
        Map<Long, Map<String,Object>> resultFieldsMap = new HashMap<>();
        if(MapUtils.isNotEmpty(fieldMap)) {
            List<Long> fieldIds = fieldMap.keySet().stream().collect(Collectors.toList());
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("FIELD_ID","fieldId", StringUtils.join(fieldIds,","),NumberOperators.EQUALS));
            List<AssetCategoryReadingFieldContext> assetCategoryReadingFields = ReadingFieldsUtil.getCategoryReadingFields(criteria);
            if(CollectionUtils.isNotEmpty(assetCategoryReadingFields)) {
                List<Long> availableFieldIds = assetCategoryReadingFields.stream().map(AssetCategoryReadingFieldContext::getFieldId).collect(Collectors.toList());
                for(Long fieldId : fieldMap.keySet()) {
                    if(availableFieldIds.contains(fieldId)) {
                        resultFieldsMap.put(fieldId, fieldMap.get(fieldId));
                    }
                }
            }
        }
        return resultFieldsMap;
    }
    private Map<Long, Map<String,Object>> getWeatherFields(List<FacilioField> fields, String searchText, Long categoryId) throws Exception
    {
        Map<Long, Map<String,Object>> fieldMap =new HashMap<>();
        if(fields != null){
            for(FacilioField field : fields)
            {
                if(searchText != null && field.getDisplayName() != null && !field.getDisplayName().contains(searchText))
                {
                    continue;
                }
                fieldMap.put(field.getFieldId(), constructFieldObject(field, categoryId));
            }
        }
        return fieldMap;
    }
    private List<String> getDefaultReadingFieldNames() throws Exception {
        List<String> fieldNames = new ArrayList<>();
        List<FacilioField> fields = FieldFactory.getDefaultReadingFields(null);
        for(FacilioField field : fields) {
            fieldNames.add(field.getName());
        }
        fieldNames.add("sysCreatedTime");
        return fieldNames;
    }

    private static boolean canSkipField(FacilioField field) {
        return field.getName().equals("info") && field.getColumnName().equals("SYS_INFO") || field.getName().equals("ruleResult");
    }
    private static Map<String, Object> constructFieldObject(FacilioField field, Long category)
    {
        Map<String, Object> details =new HashMap<>();
        details.put("name", field.getName());
        details.put("displayName", field.getDisplayName());
        details.put("categoryId", category);
        details.put("fieldId", field.getFieldId());
        details.put("id", field.getFieldId());
        details.put("dataType", field.getDataType());
        details.put("default", field.getDefault());
        details.put("module", Collections.singletonMap("type", field.getModule().getType()));
        if (field instanceof NumberField) {
            NumberField numberField = (NumberField)field;
            details.put("unit", numberField.getUnit());
        }
        return details;
    }
}
