package com.facilio.analytics.v2.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.UtilityMeterContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
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
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;


public class V2GetReadingsFromCategoryCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        String type = (String )context.get("type");
        Long category = (Long)context.get("category");
        String searchText = (String)context.get("searchText");
        if(type != null && type.equals("asset"))
        {
            context.put("fields",this.getAssetsReadings(category, searchText));
        }
        else if(type != null && type.equals("meter")){
            context.put("fields", this.getMetersReadings(category, searchText));
        }
        return false;
    }

    private Map<Long, Map<String,Object>> getAssetsReadings(Long categoryId, String searchText) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> assetFields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.ASSET));
        Map<String, FacilioField> assetFieldMap = FieldFactory.getAsMap(assetFields);

        FacilioModule readingDataMetaModule = ModuleFactory.getReadingDataMetaModule();
        List<FacilioField> readingFields = FieldFactory.getReadingDataMetaFields();
        Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(readingFields);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(assetModule));
        fields.add(assetFieldMap.get("name"));
        fields.add(readingFieldsMap.get("fieldId"));

        SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
                .select(fields)
                .table(assetModule.getTableName())
                .moduleName(assetModule.getName())
                .beanClass(AssetContext.class)
                .innerJoin(readingDataMetaModule.getTableName())
                .on(readingDataMetaModule.getTableName()+"."+readingFieldsMap.get("resourceId").getColumnName()+"="+assetModule.getTableName()+".ID")
                .setAggregation()
                .andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T))
                .andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY))
                .andCondition(CriteriaAPI.getCondition("CATEGORY", "category", categoryId.toString(), NumberOperators.EQUALS))
                .limit(30000);

        List<AssetContext> assets = selectBuilder.get();
        Map<Long, Map<String,Object>> fieldMap =new HashMap<>();
        if(assets != null && assets.size() > 0)
        {
            Set<Long> fieldIds = assets.stream().map(asset -> (Long) asset.getData().get("fieldId")).collect(Collectors.toSet());
            List<FacilioField> fieldDetailList = modBean.getFields(fieldIds);
            for(FacilioField field: fieldDetailList)
            {
                if(canSkipField(field)) {
                    continue;
                }
                fieldMap.put(field.getFieldId(), constructFieldObject(field));
            }
        }
        return fieldMap;
    }

    private Map<Long, Map<String,Object>> getMetersReadings(Long categoryId, String searchText)throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule meterModule = modBean.getModule(FacilioConstants.ContextNames.METER);
        List<FacilioField> meterFields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.METER));
        Map<String, FacilioField> meterFieldMap = FieldFactory.getAsMap(meterFields);

        FacilioModule readingDataMetaModule = ModuleFactory.getReadingDataMetaModule();
        List<FacilioField> readingFields = FieldFactory.getReadingDataMetaFields();
        Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(readingFields);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(meterModule));
        fields.add(meterFieldMap.get("name"));
        fields.add(readingFieldsMap.get("fieldId"));

        SelectRecordsBuilder<UtilityMeterContext> selectBuilder = new SelectRecordsBuilder<UtilityMeterContext>()
                .select(fields)
                .table(meterModule.getTableName())
                .moduleName(meterModule.getName())
                .beanClass(UtilityMeterContext.class)
                .innerJoin(readingDataMetaModule.getTableName())
                .on(readingDataMetaModule.getTableName()+"."+readingFieldsMap.get("resourceId").getColumnName()+"="+meterModule.getTableName()+".ID")
                .setAggregation()
                .andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T))
                .andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY))
                .andCondition(CriteriaAPI.getCondition(meterModule.getTableName()+".UTILITY_TYPE", "utilityType", categoryId.toString(), NumberOperators.EQUALS))
                .limit(30000);

        List<UtilityMeterContext> assets = selectBuilder.get();
        Map<Long, Map<String,Object>> fieldMap =new HashMap<>();
        if(assets != null && assets.size() > 0)
        {
            Set<Long> fieldIds = assets.stream().map(asset -> (Long) asset.getData().get("fieldId")).collect(Collectors.toSet());
            List<FacilioField> fieldDetailList = modBean.getFields(fieldIds);
            for(FacilioField field: fieldDetailList)
            {
                if(canSkipField(field)) {
                    continue;
                }
                fieldMap.put(field.getFieldId(), constructFieldObject(field));
            }
        }
        return fieldMap;
    }

    private static boolean canSkipField(FacilioField field) {
        return field.getName().equals("info") && field.getColumnName().equals("SYS_INFO");
    }
    private static Map<String, Object> constructFieldObject(FacilioField field)
    {
        Map<String, Object> details =new HashMap<>();
        details.put("name", field.getName());
        details.put("displayName", field.getDisplayName());
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
