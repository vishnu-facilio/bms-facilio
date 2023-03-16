package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
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
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.questions.BaseMCQContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FetchOtherOptionsOfMCQ extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long questionId = (long) context.get(FacilioConstants.QAndA.Command.QUESTION_ID);
        V3Util.throwRestException(questionId <= 0, ErrorCode.VALIDATION_ERROR, "errors.qa.fetchOtherOptionsOfMCQ.questionIdCheck",true,null);
        //V3Util.throwRestException(questionId <= 0, ErrorCode.VALIDATION_ERROR, "Invalid question id for fetching answers",true,null);
        DateRange range = (DateRange) context.get(FacilioConstants.QAndA.Command.ANSWER_RANGE);
        V3Util.throwRestException(range.getStartTime() <= 0 || range.getEndTime() <= 0 || range.getEndTime() < range.getStartTime(), ErrorCode.VALIDATION_ERROR, "errors.qa.fetchOtherOptionsOfMCQ.timeCheck",true,null);
        //V3Util.throwRestException(range.getStartTime() <= 0 || range.getEndTime() <= 0 || range.getEndTime() < range.getStartTime(), ErrorCode.VALIDATION_ERROR, "Invalid startTime/ endTime specified for fetching answers",true,null);
        QuestionContext question = QAndAUtil.fetchQuestionWithProps(questionId);
        V3Util.throwRestException(question == null || !(question instanceof BaseMCQContext), ErrorCode.VALIDATION_ERROR, "errors.qa.fetchOtherOptionsOfMCQ.questionIdCheck",true,null);
        //V3Util.throwRestException(question == null || !(question instanceof BaseMCQContext), ErrorCode.VALIDATION_ERROR, "Invalid question id for fetching answers",true,null);

        List<AnswerContext> answers = fetchAnswers((BaseMCQContext) question, range);
        if (CollectionUtils.isNotEmpty(answers)) {
            List<Map<String, Object>> otherResponses = answers.stream().map(this::constructOtherResponse).collect(Collectors.toList());
            context.put(FacilioConstants.QAndA.Command.OTHER_RESPONSES, otherResponses);
        }

        return false;
    }

    private Map<String, Object> constructOtherResponse (AnswerContext answer) {
        Map<String, Object> other = new HashMap<>();
        other.put("id", answer.getId());
        other.put("other", answer.getEnumOtherAnswer());
        other.put("response", answer.getResponse().getId());

        return other;
    }

    private List<AnswerContext> fetchAnswers(BaseMCQContext question, DateRange range) throws Exception {
        Optional<MCQOptionContext> otherOption = question.getOptions().stream().filter(MCQOptionContext::otherEnabled).findFirst();
        if (otherOption.isPresent()) {
            ModuleBean modBean = Constants.getModBean();
            FacilioModule module = modBean.getModule(FacilioConstants.QAndA.ANSWER);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
            FacilioField questionField = fieldMap.get("question");

            SelectRecordsBuilder<AnswerContext> answerBuilder = QAndAUtil.constructAnswerSelectWithQuestionAndResponseTimeRange(modBean, Collections.singletonList(question.getId()), question.getParent().getId(), range)
                                                                .select(Stream.of(questionField, fieldMap.get("enumOtherAnswer"), fieldMap.get("response")).collect(Collectors.toList()))
                                                                .beanClass(AnswerContext.class)
                                                                ;

            switch (question.getQuestionType()) {
                case MULTIPLE_CHOICE_ONE:
                    answerBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("enumAnswer"), otherOption.get()._getId().toString(), PickListOperators.IS));
                    break;
                case MULTIPLE_CHOICE_MANY:
                    FacilioModule relModule = modBean.getModule(FacilioConstants.QAndA.Answers.MCQ_MULTI_ANSWER_REL);
                    Map<String, FacilioField> relFieldMap = FieldFactory.getAsMap(modBean.getAllFields(relModule.getName()));
                    FacilioField idField = FieldFactory.getIdField(module);

                    answerBuilder.innerJoin(relModule.getTableName())
                            .on(new StringJoiner("=").add(idField.getCompleteColumnName()).add(relFieldMap.get("left").getCompleteColumnName()).toString())
                            .andCondition(CriteriaAPI.getCondition(relFieldMap.get("right"), otherOption.get()._getId().toString(), PickListOperators.IS));
                    break;
            }
            return answerBuilder.get();
        }
        return null;
    }
}
