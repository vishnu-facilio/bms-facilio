package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.*;
import com.facilio.time.DateRange;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ExtendedModuleUtil;
import com.facilio.v3.util.V3Util;
import lombok.SneakyThrows;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchAnswerSummaryForQuestionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PageContext> pages = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(pages)) {
            Object responseId = Constants.getQueryParam(context, "response");
            if (responseId == null) { // If response is present summary of responses will be ignored
                boolean fetchSummary = FacilioUtil.parseBoolean(Constants.getQueryParam(context, "fetchSummary"));
                if (fetchSummary) {
                    Object startTimeObj = Constants.getQueryParam(context, "startTime");
                    Object endTimeObj = Constants.getQueryParam(context, "endTime");
                    V3Util.throwRestException(startTimeObj == null || endTimeObj == null, ErrorCode.VALIDATION_ERROR, "startTime/ endTime is mandatory to fetch summary of responses");

                    try {
                        DateRange range = new DateRange(FacilioUtil.parseLong(startTimeObj), FacilioUtil.parseLong(endTimeObj));
                        Map<Long, QuestionContext> questions = pages.stream().flatMap(QAndAUtil::getQuestionStream)
                                                            .collect(Collectors.toMap(QuestionContext::getId, Function.identity()));
                        if (MapUtils.isNotEmpty(questions)) {
                            Long parentId = pages.get(0).getParent().getId();
                            fetchAnswered(questions, parentId, range);
                            handleTypeWiseSummary(questions, parentId, range);
                        }

                    }
                    catch (NumberFormatException e) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid startTime/ endTime specified");
                    }

                }
            }
        }
        return false;
    }

    private void handleTypeWiseSummary(Map<Long, QuestionContext> questionMap, Long parentId, DateRange range) {
        List<QuestionContext> questions = questionMap.values().stream()
                                            .map(this::setShowResponse)
                                            .filter(this::isSummaryNeeded).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(questions)) {
            Map<QuestionType, List<QuestionContext>> questionTypeListMap = ExtendedModuleUtil.splitRecordsByType(questions, QuestionContext::getQuestionType);
            questionTypeListMap.forEach((type, questionList) -> fetchSummaryViaHandler(type, parentId, questionList, range));
        }
    }

    @SneakyThrows
    private QuestionContext setShowResponse (QuestionContext q) {
        QuestionHandler handler = q.getQuestionType().getQuestionHandler();
        q.setShowResponses( (handler == null || !handler.hideShowResponseButton(q))
                            && q.getAnswered() != null && q.getAnswered() > 0)
                            ;
        return q;
    }

    @SneakyThrows
    private boolean isSummaryNeeded (QuestionContext q) {
        QuestionHandler handler = q.getQuestionType().getQuestionHandler();
        return handler != null && handler.showSummaryOfResponses(q);
    }

    @SneakyThrows
    private void fetchSummaryViaHandler (QuestionType type, Long parentId, List<QuestionContext> questions, DateRange range) {
        AnswerHandler handler = type.getAnswerHandler();
        if (handler != null) {
            handler.setSummaryOfResponses(parentId, questions, range);
        }
    }

    private void fetchAnswered (Map<Long, QuestionContext> questions, Long parentId, DateRange range) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule answerModule = modBean.getModule(FacilioConstants.QAndA.ANSWER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(answerModule.getName()));
        FacilioField questionField = fieldMap.get("question");
        FacilioField answerIdField = FieldFactory.getIdField(answerModule);

        List<Map<String, Object>> props = QAndAUtil.constructAnswerSelectWithQuestionAndResponseTimeRange(modBean, questions.keySet(), parentId, range)
                                            .select(Collections.singletonList(questionField))
                                            .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, answerIdField)
                                            .groupBy(questionField.getCompleteColumnName())
                                            .getAsProps();

        for (Map<String, Object> prop : props) {
            Integer count = ((Number) prop.get(answerIdField.getName())).intValue();
            Long questionId = (Long) ( (Map<String, Object>) prop.get(questionField.getName()) ).get("id");
            questions.get(questionId).setAnswered(count);
        }
    }
}
