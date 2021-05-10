package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
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
                                                                                .map(this::setShowResponse)
                                                                                .filter(this::isSummaryNeeded)
                                                                                .collect(Collectors.toMap(QuestionContext::_getId, Function.identity()));
                        if (MapUtils.isNotEmpty(questions)) {
                            fetchAnswered(questions, range, pages.get(0).getParent()._getId());
                            Map<QuestionType, List<QuestionContext>> questionTypeListMap = ExtendedModuleUtil.splitRecordsByType(questions.values(), QuestionContext::getQuestionType);
                            questionTypeListMap.forEach((type, questionList) -> fetchSummaryViaHandler(type, questionList, range));
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

    @SneakyThrows
    private QuestionContext setShowResponse (QuestionContext q) {
        QuestionHandler handler = q.getQuestionType().getQuestionHandler();
        q.setShowResponses(handler == null || !handler.hideShowResponseButton(q));
        return q;
    }

    @SneakyThrows
    private boolean isSummaryNeeded (QuestionContext q) {
        QuestionHandler handler = q.getQuestionType().getQuestionHandler();
        return handler != null && handler.showSummaryOfResponses(q);
    }

    @SneakyThrows
    private void fetchSummaryViaHandler (QuestionType type, List<QuestionContext> questions, DateRange range) {
        AnswerHandler handler = type.getAnswerHandler();
        if (handler != null) {
            handler.setSummaryOfResponses(questions, range);
        }
    }

    private void fetchAnswered (Map<Long, QuestionContext> questions, DateRange range, Long parentId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.QAndA.ANSWER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        FacilioField questionField = fieldMap.get("question");
        FacilioField idField = FieldFactory.getIdField(module);
        List<Map<String, Object>> props = new SelectRecordsBuilder<AnswerContext>()
                                            .module(module)
                                            .select(Collections.singletonList(questionField))
                                            .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, idField)
                                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), String.valueOf(parentId), PickListOperators.IS))
                                            .andCondition(CriteriaAPI.getCondition(questionField, questions.keySet(), PickListOperators.IS))
                                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("sysModifiedTime"), range.toString(), DateOperators.BETWEEN))
                                            .groupBy(questionField.getCompleteColumnName())
                                            .getAsProps();

        for (Map<String, Object> prop : props) {
            Integer count = ((Number) prop.get(idField.getName())).intValue();
            Long questionId = (Long) ( (Map<String, Object>) prop.get(questionField.getName()) ).get("id");
            questions.get(questionId).setAnswered(count);
        }
    }
}
