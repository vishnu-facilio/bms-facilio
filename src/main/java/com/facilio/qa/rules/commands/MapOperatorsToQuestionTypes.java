package com.facilio.qa.rules.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.filters.FilterOperator;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionType;
import com.facilio.qa.rules.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MapOperatorsToQuestionTypes extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<FilterOperator>> filterOperatorsMap = (Map<String, List<FilterOperator>>) context.get(FacilioConstants.Filters.FILTER_OPERATORS);
        V3Util.throwRestException(MapUtils.isEmpty(filterOperatorsMap), ErrorCode.UNHANDLED_EXCEPTION, "Operators map cannot be empty. This shouldn't happen");
        Map<String, List<FilterOperator>> questionVsOperators = new HashMap<>();
        for (QuestionType type : QuestionType.values()) {
            if (type.isOperatorSupportedForRules()) {
                questionVsOperators.put(
                        type.name(),
                        removeCommonOperators(
                                Objects.requireNonNull(
                                        filterOperatorsMap.get(type.getAnswerFieldType().name()),
                                        MessageFormat.format("Operators of type {0} cannot be null", type)
                                )
                        )
                );
            }
        }
        context.put(Constants.Command.QUESTION_TYPE_VS_OPERATORS, questionVsOperators);

        return false;
    }

    private List<FilterOperator> removeCommonOperators (List<FilterOperator> operators) {
        return operators.stream().filter(this::notCommonOperator).collect(Collectors.toList());
    }

    private boolean notCommonOperator (FilterOperator operator) {
        return operator.getOperator() != CommonOperators.IS_EMPTY && operator.getOperator() != CommonOperators.IS_NOT_EMPTY;
    }
}
