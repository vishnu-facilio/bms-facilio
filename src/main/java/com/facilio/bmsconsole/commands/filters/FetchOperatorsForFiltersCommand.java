package com.facilio.bmsconsole.commands.filters;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.filters.FilterOperator;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchOperatorsForFiltersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String dataTypeStr = (String) context.get(FacilioConstants.Filters.FILTER_DATA_TYPE);
        FieldType[] types = null;
        if (StringUtils.isEmpty(dataTypeStr)) {
            types = FieldType.values();
        }
        else {
            FieldType type = FieldType.valueOf(dataTypeStr);
            FacilioUtil.throwIllegalArgumentException(type == null, "Invalid Field Data Type to fetch operators");
            types = new FieldType[] {type};
        }

        Map<String, List<FilterOperator>> filterOperatorsMap = new HashMap<>();
        for (FieldType type : types) {
            List<FilterOperator> filterOperators = createFilterOperators(type);
            if (CollectionUtils.isNotEmpty(filterOperators)) {
                filterOperatorsMap.put(type.name(), filterOperators);
            }
        }
        context.put(FacilioConstants.Filters.FILTER_OPERATORS, filterOperatorsMap);

        return false;
    }

    private List<FilterOperator> createFilterOperators (FieldType fieldType) {

        switch (fieldType) {
            case ID:
            case FILE:
            case COUNTER:
                return null;
        }

        Map<String, Operator> operators = fieldType.getOperators();
        List<FilterOperator> filterOperators = null;
        if (MapUtils.isNotEmpty(operators)) {
            filterOperators = new ArrayList<>();
            for (Operator operator : operators.values()) {
                FilterOperator filterOperator = createFilterOperator(operator);
                if (filterOperator != null) {
                    filterOperators.add(filterOperator);
                }
            }
        }
        return filterOperators;
    }

    private FilterOperator createFilterOperator (Operator operator) {
        return new FilterOperator(operator);
    }
}
