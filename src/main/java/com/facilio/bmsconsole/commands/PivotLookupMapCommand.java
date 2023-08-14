package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.PivotRowColumnContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PivotLookupMapCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PivotRowColumnContext> rowColumns = (List<PivotRowColumnContext>) context.get(FacilioConstants.Reports.ROWS);
        ArrayList<HashMap<String, Object>> pivotTableData = (ArrayList<HashMap<String, Object>>) context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Map<String, Object> pivotLookupMap = new HashMap<>();

        for(PivotRowColumnContext row: rowColumns){
            FacilioField facilioField = null;
            LookupField lookupField = null;
            if(row.getField().getId() <=0 && row.getField().getName() != null && row.getField().getName().equals("siteId")){
                facilioField = FieldFactory.getSiteField(modBean.getModule(row.getField().getModuleId()));
            }else {
                facilioField = getField(row, modBean);
                lookupField = getLookupField(row, modBean);
            }
            if(facilioField instanceof LookupField && (facilioField != null && facilioField.getName() != null && !facilioField.getName().equals("siteId"))) {
                LookupField newLookupField = (LookupField) facilioField;
                List<Long> ids = getColumnArray(row.getAlias(), pivotTableData);
                Map<Long, Object> lookupMap = getLookUpMap(newLookupField.getSpecialType(), newLookupField.getLookupModule(), ids);
                if(lookupMap == null || CollectionUtils.isEmpty(Collections.singleton(lookupMap))) continue;
                pivotLookupMap.put(row.getAlias(), lookupMap);
            } else if(facilioField != null && facilioField.getName().equals("siteId")){
                List<Long> ids = getColumnArray(row.getAlias(), pivotTableData);
                FacilioModule module = modBean.getModule("site");
                Map<Long, Object> lookupMap = getLookUpMap(null, module, ids);
                if(lookupMap == null || CollectionUtils.isEmpty(Collections.singleton(lookupMap))) continue;
                pivotLookupMap.put(row.getAlias(), lookupMap);
            }else if(lookupField != null && facilioField != null){
                String valueString = getColumnValues(row.getAlias(), pivotTableData);
                if(valueString.equals("")) continue;
                pivotLookupMap.put(row.getAlias(), getLookUpMap(lookupField.getLookupModule(), facilioField, valueString));
            }
        }

        context.put(FacilioConstants.ContextNames.PIVOT_LOOKUP_MAP, pivotLookupMap);

        return false;
    }

    private FacilioField getField(PivotRowColumnContext rowColumn, ModuleBean modBean) throws Exception {
        return modBean.getField(rowColumn.getField().getId()).clone();
    }

    private LookupField getLookupField(PivotRowColumnContext rowColumn, ModuleBean modBean) throws Exception {
        if(rowColumn.getLookupFieldId() > 0){
            return (LookupField) modBean.getField(rowColumn.getLookupFieldId()).clone();
        }
        return null;
    }


    private String getColumnValues(String alias, ArrayList<HashMap<String, Object>> pivotTableData){
        StringJoiner stringJoiner = new StringJoiner(",");
        List<String> valueArray = new ArrayList<>();
        for (HashMap<String, Object> data: pivotTableData) {
            Map<String, Object> rows = (Map<String, Object>) data.get("rows");
            Object val = rows.get(alias);
            if (val == null || val.equals("")) continue;
            valueArray.add(val.toString());
        }

        valueArray = valueArray.stream().distinct().collect(Collectors.toList());

        for (String value: valueArray) {
            stringJoiner.add(value);
        }
        return stringJoiner.toString();
    }

    private List<Long> getColumnArray(String alias, ArrayList<HashMap<String, Object>> pivotTableData){
        List<Long> idsArray = new ArrayList<>();
        for (HashMap<String, Object> data: pivotTableData) {
            Map<String, Object> rows = (Map<String, Object>) data.get("rows");
            Object val = rows.get(alias);
            if (val == null || val.equals("")) continue;
            idsArray.add(Long.parseLong(val.toString()));
        }

        idsArray = idsArray.stream().distinct().collect(Collectors.toList());

        return idsArray;
    }

    private Map<Object, Long> getLookUpMap(FacilioModule lookupModule, FacilioField facilioField, String data) throws Exception {
        Map<Object, Long> lookupMap = new HashMap<>();

        boolean flag=true;
        List<FacilioField> selectFields = new ArrayList<>();
        if(lookupModule.getName().equals(facilioField.getModule().getName())){
            selectFields.add(FieldFactory.getIdField(lookupModule));
            selectFields.add(facilioField);
            flag=false;
        }else{
            selectFields.add(FieldFactory.getIdField(facilioField.getModule()));
            selectFields.add(facilioField);
        }

        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder()
                .beanClass(FacilioConstants.ContextNames.getClassFromModule(!flag ? lookupModule : facilioField.getModule(), false))
                .select(selectFields)
                .module(!flag ? lookupModule : facilioField.getModule());

        if(getOperatorId(facilioField) != null) {
            builder.andCondition(CriteriaAPI.getCondition(facilioField, data, getOperatorId(facilioField)));
        }
        List<Map<String, Object>> valueProps = builder.getAsProps();
        for (Map<String, Object> row : valueProps){
            lookupMap.put(row.get(facilioField.getName()), Long.parseLong(row.get("id").toString()));
        }
        return lookupMap;
    }

    private Operator getOperatorId(FacilioField field) {
        switch (field.getDataTypeEnum()) {
            case DECIMAL:
            case NUMBER:
                return NumberOperators.EQUALS;
            case STRING:
                return StringOperators.IS;
            case BOOLEAN:
                return BooleanOperators.IS;
            case ENUM:
            case SYSTEM_ENUM:
                return EnumOperators.IS;
            case MULTI_ENUM:
                return MultiFieldOperators.CONTAINS;
            case DATE:
            case DATE_TIME:
                return DateOperators.BETWEEN;
            case LOOKUP:
                return PickListOperators.IS;
        }
        return null;
    }


    private Map<Long, Object> getLookUpMap(String specialType, FacilioModule lookupModule, List<Long> ids) throws Exception {
        Map<Long, Object> lookupMap;

        if (LookupSpecialTypeUtil.isSpecialType(specialType)) {
            List list = LookupSpecialTypeUtil.getObjects(specialType, null);
            lookupMap = LookupSpecialTypeUtil.getPrimaryFieldValues(specialType, list);
            return lookupMap;
        }

        String moduleName = lookupModule.getName();


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField mainField = modBean.getPrimaryField(moduleName);

        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(mainField);
        selectFields.add(FieldFactory.getIdField(lookupModule));
        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder()
                .beanClass(FacilioConstants.ContextNames.getClassFromModule(lookupModule, false))
                .select(selectFields)
                .module(lookupModule);
        if (CollectionUtils.isNotEmpty(ids)) {
            builder.andCondition(CriteriaAPI.getIdCondition(ids, lookupModule));
        }

        List<Map<String, Object>> asProps = builder.getAsProps();

        Map<Long, Object> lookupValueMap = null;
        if (mainField instanceof LookupField) {
            LookupField lookupField = (LookupField) mainField;
            lookupValueMap = getLookUpMap(lookupField.getSpecialType(), lookupField.getLookupModule(), null);
        }

        lookupMap = new HashMap<>();
        for (Map<String, Object> map : asProps) {
            if (mainField instanceof LookupField) {
                long id = (Long) map.get("id");
                long lookupFieldId = (Long) ((Map<String, Object>) map.get(mainField.getName())).get("id");
                lookupMap.put(id, (String) lookupValueMap.get(lookupFieldId));
            } else {
                lookupMap.put((Long) map.get("id"), (String) map.get(mainField.getName()));
            }
        }
        return lookupMap;
    }
}
