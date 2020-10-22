package com.facilio.bmsconsole.commands.filters;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.filters.FilterOperator;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.*;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.*;

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

        Map<String, Operator> operators = fetchOperatorsOfaType(fieldType);
        List<FilterOperator> filterOperators = null;
        if (MapUtils.isNotEmpty(operators)) {
            filterOperators = new ArrayList<>();
            for (Operator operator : operators.values()) {
                List<FilterOperator> filterOperator = createFilterOperator(operator);
                if (CollectionUtils.isNotEmpty(filterOperator)) {
                    filterOperators.addAll(filterOperator);
                }
            }
        }
        return filterOperators;
    }

    private Map<String, Operator> filterDateOperators() {
        Map<String, Operator> operators = new LinkedHashMap<>();
        operators.put(DateOperators.TODAY.getOperator(), DateOperators.TODAY);
        operators.put(DateOperators.TOMORROW.getOperator(), DateOperators.TOMORROW);
        operators.put(DateOperators.YESTERDAY.getOperator(), DateOperators.YESTERDAY);
        operators.put(DateOperators.TILL_YESTERDAY.getOperator(), DateOperators.TILL_YESTERDAY);
        operators.put(DateOperators.NEXT_N_DAYS.getOperator(), DateOperators.NEXT_N_DAYS);
        operators.put(DateOperators.CURRENT_WEEK.getOperator(), DateOperators.CURRENT_WEEK);
        operators.put(DateOperators.LAST_WEEK.getOperator(), DateOperators.LAST_WEEK);
        operators.put(DateOperators.LAST_N_WEEKS.getOperator(), DateOperators.LAST_N_WEEKS);
        operators.put(DateOperators.NEXT_WEEK.getOperator(), DateOperators.NEXT_WEEK);
        operators.put(DateOperators.CURRENT_MONTH.getOperator(), DateOperators.CURRENT_MONTH);
        operators.put(DateOperators.LAST_MONTH.getOperator(), DateOperators.LAST_MONTH);
        operators.put(DateOperators.BETWEEN.getOperator(), DateOperators.BETWEEN);
        return operators;
    }

    private Map<String, Operator> fetchOperatorsOfaType(FieldType type) {
        switch (type) {
            case DATE:
            case DATE_TIME:
                return filterDateOperators();
            default:
                return type.getOperators();
        }
    }

    private List<FilterOperator> createFilterOperator (Operator operator) {

        if (operator instanceof FieldOperator
                || operator instanceof BuildingOperator
                || operator instanceof UserOperators
                || operator instanceof LookupOperator
                || operator == EnumOperators.VALUE_IS
                || operator == EnumOperators.VALUE_ISN_T
        ) {
            return null;
        }

        if (operator instanceof DateOperators) {
            switch ((DateOperators) operator) {
                case NEXT_N_DAYS: {
                    List<FilterOperator> operators = new ArrayList<>();
                    operators.add(new FilterOperator("Next 2 Days", "within next 2 days", operator.getOperatorId(), false, "2"));
                    operators.add(new FilterOperator("Next 7 Days", "within next 7 days", operator.getOperatorId(), false, "7"));
                    return operators;
                }
                case CURRENT_WEEK: { // Have to check with Krishna if operator name itself can be changed if they are not used in Scripts
                    return Collections.singletonList(new FilterOperator("This Week", operator.getTagDisplayName(), operator.getOperatorId(), operator.isValueNeeded()));
                }
                case LAST_N_WEEKS: {
                    return Collections.singletonList(new FilterOperator("Last 2 Weeks", "within last 2 weeks", operator.getOperatorId(), false, "2"));
                }
                case CURRENT_MONTH: {
                    return Collections.singletonList(new FilterOperator("This Month", operator.getTagDisplayName(), operator.getOperatorId(), operator.isValueNeeded()));
                }
                case BETWEEN: {
                    return Collections.singletonList(new FilterOperator("Custom", operator.getTagDisplayName(), operator.getOperatorId(), operator.isValueNeeded()));
                }
                default:
                    return Collections.singletonList(new FilterOperator(operator));
            }
        }
        else {
            return Collections.singletonList(new FilterOperator(operator));
        }
    }
}
