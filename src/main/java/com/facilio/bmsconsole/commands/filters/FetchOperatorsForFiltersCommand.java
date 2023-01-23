package com.facilio.bmsconsole.commands.filters;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.filters.FilterOperator;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.*;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.text.MessageFormat;
import java.util.*;

public class FetchOperatorsForFiltersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String dataTypeStr = (String) context.get(FacilioConstants.Filters.FILTER_DATA_TYPE);
        FieldType[] types = (FieldType[]) context.get(FacilioConstants.Filters.FILTER_DATA_TYPES);

        if (StringUtils.isNotEmpty(dataTypeStr)) {
            FieldType type = FieldType.valueOf(dataTypeStr);
            FacilioUtil.throwIllegalArgumentException(type == null, "Invalid Field Data Type to fetch operators");
            types = new FieldType[] {type};
        }
        else if (ArrayUtils.isEmpty(types)) {
            types = FieldType.values();
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
            case FILE:
            case COUNTER:
                return null;
        }

        Map<String, Operator> operators = fetchOperatorsOfaType(fieldType);
        List<FilterOperator> filterOperators = null;
        if (MapUtils.isNotEmpty(operators)) {
            filterOperators = new ArrayList<>();
            for (Operator operator : operators.values()) {
                List<FilterOperator> filterOperator = createFilterOperator(fieldType, operator);
                if (CollectionUtils.isNotEmpty(filterOperator)) {
                    filterOperators.addAll(filterOperator);
                }
            }
        }
        return filterOperators;
    }

    private void addToOperators (Operator operator, Map<String, Operator> operators) {
        operators.put(operator.getOperator(), operator);
    }

    private Map<String, Operator> filterDateOperators() {
        Map<String, Operator> operators = new LinkedHashMap<>();
		addToOperators(DateOperators.BETWEEN, operators);
        addToOperators(CommonOperators.IS_EMPTY, operators);
        addToOperators(CommonOperators.IS_NOT_EMPTY, operators);
        addToOperators(DateOperators.IS_BEFORE, operators);
        addToOperators(DateOperators.IS_AFTER, operators);
		addToOperators (DateOperators.HOURS_OF_DAY,operators);
        addToOperators(DateOperators.TODAY, operators);
        addToOperators(DateOperators.TILL_NOW, operators);
        addToOperators(DateOperators.TOMORROW, operators);
        addToOperators(DateOperators.STARTING_TOMORROW, operators);
        addToOperators(DateOperators.YESTERDAY, operators);
        addToOperators(DateOperators.TILL_YESTERDAY, operators);
        addToOperators(DateOperators.NEXT_N_DAYS, operators);
		addToOperators (DateOperators.AFTER_N_DAYS,operators);
		addToOperators (DateOperators.BEFORE_N_DAYS,operators);
        addToOperators(DateOperators.CURRENT_WEEK, operators);
        addToOperators(DateOperators.LAST_WEEK, operators);
        addToOperators(DateOperators.LAST_N_WEEKS, operators);
        addToOperators(DateOperators.NEXT_WEEK, operators);
		addToOperators (DateOperators.DAY_OF_WEEK,operators);
        addToOperators(DateOperators.CURRENT_MONTH, operators);
        addToOperators(DateOperators.LAST_MONTH, operators);
        addToOperators(DateOperators.NEXT_MONTH, operators);
		addToOperators (DateOperators.DAY_OF_MONTH,operators);
		addToOperators (DateOperators.MONTH,operators);
//		addToOperators (DateOperators.DAY_OF_YEAR,operators);
		addToOperators (DateOperators.WEEK_OF_YEAR,operators);
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

    private List<FilterOperator> createFilterOperator (FieldType type, Operator operator) {

        if (operator instanceof FieldOperator
                || operator instanceof BuildingOperator
                || operator instanceof UserOperators
                || operator instanceof LookupOperator
                || operator == EnumOperators.VALUE_IS
                || operator == EnumOperators.VALUE_ISN_T
                || operator instanceof RelatedModuleOperator
                || operator instanceof MultiLookupOperator
                || operator instanceof SiteOperator
                || operator instanceof UserOperator
        ) {
            return null;
        }

        if (operator instanceof DateOperators) {
            switch ((DateOperators) operator) {
                case NEXT_N_DAYS: {
                    List<FilterOperator> operators = new ArrayList<>();
                    operators.add(new FilterOperator(operator, "Next 2 Days", "within next 2 days", false, "2"));
                    operators.add(new FilterOperator(operator, "Next 7 Days", "within next 7 days", false, "7"));
                    return operators;
                }
                case CURRENT_WEEK: { // Have to check with Krishna if operator name itself can be changed if they are not used in Scripts
                    return Collections.singletonList(new FilterOperator(operator, "This Week"));
                }
                case LAST_N_WEEKS: {
                    return Collections.singletonList(new FilterOperator(operator, "Last 2 Weeks", "within last 2 weeks", false, "2"));
                }
                case CURRENT_MONTH: {
                    return Collections.singletonList(new FilterOperator(operator, "This Month"));
                }
                case BETWEEN: {
                    return Collections.singletonList(new FilterOperator(operator, "Custom"));
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
