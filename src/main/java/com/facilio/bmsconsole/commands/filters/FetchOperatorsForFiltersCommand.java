package com.facilio.bmsconsole.commands.filters;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.filters.FilterOperator;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.Operator;
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

        if (operator instanceof FieldOperator) {
            return null;
        }

        if (operator instanceof DateOperators) {
            switch ((DateOperators) operator) {
                case NEXT_N_DAYS: {
                    List<FilterOperator> operators = new ArrayList<>();
                    operators.add(new FilterOperator("Next 2 Days", MessageFormat.format("2_{0}", DateOperators.NEXT_N_DAYS.getOperatorId()), false));
                    operators.add(new FilterOperator("Next 7 Days", MessageFormat.format("7_{0}", DateOperators.NEXT_N_DAYS.getOperatorId()), false));
                    return operators;
                }
                case CURRENT_WEEK: {
                    return Collections.singletonList(new FilterOperator("This Week", String.valueOf(DateOperators.CURRENT_WEEK.getOperatorId()), DateOperators.CURRENT_WEEK.isValueNeeded()));
                }
                case LAST_N_WEEKS: {
                    return Collections.singletonList(new FilterOperator("Last 2 Weeks", MessageFormat.format("2_{0}", DateOperators.LAST_N_WEEKS.getOperatorId()), false));
                }
                case CURRENT_MONTH: {
                    return Collections.singletonList(new FilterOperator("This Month", String.valueOf(DateOperators.CURRENT_MONTH.getOperatorId()), DateOperators.CURRENT_MONTH.isValueNeeded()));
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
