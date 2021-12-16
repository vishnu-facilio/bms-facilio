package com.facilio.qa.context.client.answers.handler;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.MCQSingleAnswerContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.qa.context.questions.MCQSingleContext;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.google.common.base.Functions;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j
public class MCQSingleAnswerHandler extends AnswerHandler<MCQSingleAnswerContext> {
    public MCQSingleAnswerHandler(Class<MCQSingleAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public MCQSingleAnswerContext serialize(AnswerContext answer) {
        MCQSingleAnswerContext mcqSingleAnswer = new MCQSingleAnswerContext();
        MCQSingleAnswerContext.MCQAnswer mcqAnswer = new MCQSingleAnswerContext.MCQAnswer();
        mcqAnswer.setSelected(answer.getEnumAnswer());
        if ( StringUtils.isNotEmpty(answer.getEnumOtherAnswer()) )  { // Assumption is we'll validate option before adding and so not doing any check here
            mcqAnswer.setOther(answer.getEnumOtherAnswer());
        }
        mcqSingleAnswer.setAnswer(mcqAnswer);
        return mcqSingleAnswer;
    }

    @Override
    public AnswerContext deSerialize(MCQSingleAnswerContext answer, QuestionContext question) throws Exception {
        MCQSingleContext mcqQuestion = (MCQSingleContext) question;
        Long selected = answer.getAnswer().getSelected();
        V3Util.throwRestException(selected == null, ErrorCode.VALIDATION_ERROR, "At least one option need to be selected for MCQ");
        LOGGER.debug(MessageFormat.format("Question => {0} | Options => {1} | Selected => {2}", question.getId(), ((MCQSingleContext) question).getOptions(), selected));
        Optional<MCQOptionContext> option = mcqQuestion.getOptions().stream().filter(o -> o.getId() == selected).findFirst();
        V3Util.throwRestException(!option.isPresent(), ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid select option ({0}) is specified while adding MCQ Answer", selected));
        AnswerContext answerContext = new AnswerContext();
        answerContext.setEnumAnswer(selected);
        if ( option.get().otherEnabled() && StringUtils.isNotEmpty(answer.getAnswer().getOther()) ) {
            answerContext.setEnumOtherAnswer(answer.getAnswer().getOther());
        }
        else {
            answer.getAnswer().setOther(null);
        }
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return answer.getEnumAnswer() == null;
    }

    @Override
    public boolean checkIfAnswerIsNull (MCQSingleAnswerContext answer, QuestionContext question) throws Exception {
        return answer.getAnswer().getSelected() == null;
    }

    @Override
    public void setSummaryOfResponses(Long parentId, List<QuestionContext> questions, DateRange range) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.QAndA.ANSWER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        FacilioField questionField = fieldMap.get("question");
        FacilioField enumAnswerField = fieldMap.get("enumAnswer");
        Map<Long, QuestionContext> questionMap = questions.stream().collect(Collectors.toMap(QuestionContext::getId, Function.identity()));

        FacilioField idField = FieldFactory.getIdField(module);
        List<Map<String, Object>> props = QAndAUtil.constructAnswerSelectWithQuestionAndResponseTimeRange(modBean, questionMap.keySet(), parentId, range)
                                            .select(Stream.of(questionField, enumAnswerField).collect(Collectors.toList()))
                                            .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, idField)
                                            .groupBy(new StringJoiner(",").add(questionField.getCompleteColumnName()).add(enumAnswerField.getCompleteColumnName()).toString())
                                            .getAsProps();

        QAndAUtil.populateMCQSummary(props, questionMap, questionField, enumAnswerField, idField);
    }

	@Override
	public String getAnswerStringValue(AnswerContext answer, QuestionContext question) throws Exception {
		// TODO Auto-generated method stub
		MCQSingleContext mcqQuestion = (MCQSingleContext) question;
		Long enumAnswer = answer.getEnumAnswer();
		
		 Map<Long, MCQOptionContext> posMap = mcqQuestion.getOptions().stream().collect(Collectors.toMap(MCQOptionContext::getId, Function.identity()));
		
		return posMap.get(enumAnswer).getLabel();
	}
}
