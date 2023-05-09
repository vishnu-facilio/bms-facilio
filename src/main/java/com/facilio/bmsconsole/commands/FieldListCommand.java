package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FieldListCommand extends FacilioCommand {

    public boolean executeCommand(Context context) throws Exception {
        boolean isOneLevelFields= (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_ONE_LEVEL_FIELDS,false);
        if(isOneLevelFields) {
            return false;
        }
        List<FilterFieldContext> filterFields =(List<FilterFieldContext>) context.get(FacilioConstants.Filters.FILTER_FIELDS);
        List<FacilioField> fields=getFieldsFromFilterFields(filterFields);
        context.put(FacilioConstants.ContextNames.FIELDS, fields);
        return false;
    }
    private List<FacilioField> getFieldsFromFilterFields(List<FilterFieldContext> filterFields) {
        return filterFields.stream().map(FilterFieldContext::getField).collect(Collectors.toList());
    }
}
