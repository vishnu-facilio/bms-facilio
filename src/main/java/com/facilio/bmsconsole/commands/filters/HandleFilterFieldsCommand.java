package com.facilio.bmsconsole.commands.filters;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
            for (FacilioField field : fields) {
                FilterFieldContext filterField = createFilterField(moduleName, field);
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

    private FilterFieldContext createFilterField(String moduleName, FacilioField field) throws Exception { // We can do special handling here also maybe
        switch (field.getDataTypeEnum()) {
            case FILE:
            case ID:
            case MISC:
                return null;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        switch (field.getName()) {
            case "stateFlowId" :
            case "slaPolicyId" :
            case "approvalFlowId" :
            case "formId" :
            case "siteId" :
                return null;
            case "moduleState" :
                return module.isStateFlowEnabled() ? new FilterFieldContext(field, createStateFieldFilter(module)) : null;
            default:
                return new FilterFieldContext(field);
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
}
