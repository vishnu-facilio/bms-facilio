package com.facilio.bmsconsole.commands.filters;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.bmsconsole.context.filters.FilterOperator;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ScopeHandler;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class HandleFilterFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<FilterFieldContext> filterFields = createFilterFields(moduleName, fields);
        context.put(FacilioConstants.Filters.FILTER_FIELDS, filterFields);
        return false;
    }

    private List<FilterFieldContext> createFilterFields (String moduleName, List<FacilioField> fields) throws Exception { // Have to check how special handling can be done here for each module. These shouldn't be required once FieldAccessSpecifiers are migrated
        List<FilterFieldContext> filterFields = null;
        if (CollectionUtils.isNotEmpty(fields)) {
            filterFields = new ArrayList<>();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            Objects.requireNonNull(module, "Invalid module name when fetching filter fields");

            for (FacilioField field : filterFields(module, fields)) {
                FilterFieldContext filterField = createFilterField(module, field);
                if (filterField != null) {
                    filterFields.add(filterField);
                }
            }
        }
//        long startTime = System.currentTimeMillis();
        Collections.sort(filterFields, Comparator.comparing(FilterFieldContext::getDisplayName)); //Sorting in java because we take fields from cache and Collections.sort is pretty efficient
//        System.out.println("Time taken to sort => "+ (System.currentTimeMillis() - startTime));
        return filterFields;
    }

    private List<FacilioField> filterFields (FacilioModule module, List<FacilioField> fields) {
        if (AssetsAPI.isAssetsModule(module)) {
            fields = filterAssetFields(fields);
        }
        return filterScopeFields(module, fields);
    }

    private List<FacilioField> filterScopeFields (FacilioModule module, List<FacilioField> fields) {
        ScopeHandler.ScopeFieldsAndCriteria scopeFieldsAndCriteria = ScopeHandler.getInstance().getFieldsAndCriteriaForSelect(module, null);


        // TODO Handle multiple scope fields
        // Need to filter only if a valid criteria is present which means scope is set
        if (scopeFieldsAndCriteria != null && scopeFieldsAndCriteria.getCriteria() != null && CollectionUtils.isNotEmpty(scopeFieldsAndCriteria.getFields())) {
            fields.removeAll(scopeFieldsAndCriteria.getFields());
        }
        return fields;
    }

    private FilterFieldContext createFilterField(FacilioModule module, FacilioField field) throws Exception { // We can do special handling here also maybe
        switch (field.getDataTypeEnum()) {
            case FILE:
            case ID:
            case MISC:
                return null;
        }

        FilterFieldContext filterField = null;
        switch (field.getName()) {
            case "stateFlowId" :
            case "slaPolicyId" :
            case "approvalFlowId" :
            case "formId" :
                filterField = null;
                break;
            case "siteId" :
                filterField = new FilterFieldContext(FieldFactory.getSiteField(module), false);
                break;
            case "moduleState" :
                filterField = module.isStateFlowEnabled() ? new FilterFieldContext(field, createStateFieldFilter(module)) : null;
                break;
            default:
                filterField = new FilterFieldContext(field);
                break;
        }

        if (filterField != null) {
            handleSpecialOperators(filterField);
        }

        return filterField;
    }

    private void handleSpecialOperators (FilterFieldContext field) {
        if (field.getLookupModule() != null &&
                (   field.getLookupModule().getName().equals(FacilioConstants.ContextNames.RESOURCE)
                        || field.getLookupModule().getName().equals(FacilioConstants.ContextNames.BASE_SPACE)
                )) {
            field.setOperators(Collections.singletonList(new FilterOperator(BuildingOperator.BUILDING_IS, "within", true)));
        }
    }

    private JSONObject createStateFieldFilter(FacilioModule module) {
        JSONObject operator = new JSONObject();
        operator.put("operatorId", NumberOperators.EQUALS.getOperatorId());
        JSONArray values = new JSONArray();
        values.add(String.valueOf(module.getModuleId()));
        operator.put("value", values);

        JSONObject filter = new JSONObject();
        filter.put("parentModuleId", operator);
        return filter;
    }

    private List<FacilioField> filterAssetFields (List<FacilioField> fields) {
        return fields.stream().
                filter(
                        f -> FieldFactory.Fields.assetFieldsInclude.contains(f.getName()) || !f.isDefault()
                ).collect(Collectors.toList());
    }
}
