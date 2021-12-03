package com.facilio.qa.context.client.answers.handler;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.RatingAnswerContext;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RatingAnswerHandler extends AnswerHandler<RatingAnswerContext> {

    public RatingAnswerHandler(Class<RatingAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public RatingAnswerContext serialize(AnswerContext answer) throws Exception {
        RatingAnswerContext ratingAnswer = new RatingAnswerContext();
        ratingAnswer.setAnswer(answer.getRatingAnswer());
        return ratingAnswer;
    }

    @Override
    public AnswerContext deSerialize(RatingAnswerContext answer, QuestionContext question) throws Exception {
        V3Util.throwRestException(answer.getAnswer() < FacilioConstants.QAndA.MIN_VAL, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be less than the min value ({1})", answer.getAnswer(), FacilioConstants.QAndA.MIN_VAL));
        V3Util.throwRestException(answer.getAnswer() > FacilioConstants.QAndA.MAX_VAL, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be greater than the max value ({1})", answer.getAnswer(), FacilioConstants.QAndA.MAX_VAL));
        AnswerContext answerContext = new AnswerContext();
        answerContext.setRatingAnswer(answer.getAnswer());
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull(AnswerContext answer) throws Exception {
        return answer.getRatingAnswer() == null;
    }

    @Override
    public String getAnswerStringValue(AnswerContext answer, QuestionContext question) throws Exception {
        return answer.getRatingAnswer().toString();
    }

    @Override
    public void setSummaryOfResponses(Long parentId, List<QuestionContext> questions, DateRange range) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.QAndA.ANSWER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        FacilioField questionField = fieldMap.get("question");
        FacilioField ratingAnswerField = fieldMap.get("ratingAnswer");
        Map<Long, QuestionContext> questionMap = questions.stream().collect(Collectors.toMap(QuestionContext::_getId, Function.identity()));

        FacilioField idField = FieldFactory.getIdField(module);
        List<Map<String, Object>> props = QAndAUtil.constructAnswerSelectWithQuestionAndResponseTimeRange(modBean, questionMap.keySet(), parentId, range)
                .select(Stream.of(questionField, ratingAnswerField).collect(Collectors.toList()))
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, idField)
                .groupBy(new StringJoiner(",").add(questionField.getCompleteColumnName()).add(ratingAnswerField.getCompleteColumnName()).toString())
                .getAsProps();

        QAndAUtil.populateRatingSummary(props, questionMap, questionField, ratingAnswerField, idField);
    }
}
