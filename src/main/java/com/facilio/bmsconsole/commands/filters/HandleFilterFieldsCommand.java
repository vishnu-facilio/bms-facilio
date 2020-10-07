package com.facilio.bmsconsole.commands.filters;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
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

    private List<FilterFieldContext> createFilterFields (String moduleName, List<FacilioField> fields) { // Have to check how special handling can be done here for each module. These shouldn't be required once FieldAccessSpecifiers are migrated
        List<FilterFieldContext> filterFields = null;
        if (CollectionUtils.isNotEmpty(fields)) {
            filterFields = new ArrayList<>();
            for (FacilioField field : fields) {
                FilterFieldContext filterField = createFilterField(field);
                if (filterField != null) {
                    filterFields.add(filterField);
                }
            }
        }
        return filterFields;
    }

    private FilterFieldContext createFilterField(FacilioField field) { // We can do special handling here also maybe
        switch (field.getDataTypeEnum()) {
            case FILE:
            case ID:
            case MISC:
                return null;
        }

        switch (field.getName()) {
            case "stateFlowId" :
            case "slaPolicyId" :
            case "approvalFlowId" :
            case "formId" :
            case "siteId" :
                return null;
            default:
                return new FilterFieldContext(field);
        }
    }
}
